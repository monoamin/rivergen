package net.monoamin.rivergen.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.monoamin.rivergen.render.RenderHandler;
import net.monoamin.rivergen.mathutils.SplineNode;
import java.util.concurrent.CompletableFuture;

import java.util.*;

public class TerrainCarver {

    private final ServerLevel level; // Your Minecraft world object
    private final int channelRadiusMax; // Radius of the channel's spherical cursor
    private final int channelRadiusMin;

    public TerrainCarver(ServerLevel level, int channelRadiusMin, int channelRadiusMax) {
        this.level = level;
        this.channelRadiusMax = channelRadiusMax;
        this.channelRadiusMin = channelRadiusMin;
    }

    public void asyncCarveChannelSpline(ArrayList<SplineNode> splineNodes) {
        carveChannelSplineAsync(splineNodes).thenRun(() -> {
            // This code runs after carving is complete.
            System.out.println("Carving completed!");
        }).exceptionally(ex -> {
            // Handle any exceptions that occurred during the async operation
            ex.printStackTrace();
            return null;
        });
    }

    private CompletableFuture<Void> carveChannelSplineAsync(ArrayList<SplineNode> splineNodes) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (splineNodes.size() < 2) {
                    return; // Need at least two points to interpolate
                }

                // Step 1: Calculate the total length of the spline
                float totalLength = 0.0f;
                for (int i = 0; i < splineNodes.size() - 1; i++) {
                    Vec3 start = splineNodes.get(i).vec3();
                    Vec3 end = splineNodes.get(i + 1).vec3();
                    totalLength += (float) start.distanceTo(end);
                }

                // Step 2: Iterate through each segment and carve the river
                float cumulativeLength = 0.0f;
                for (int i = 0; i < splineNodes.size() - 1; i++) {
                    SplineNode startNode = splineNodes.get(i);
                    SplineNode endNode = splineNodes.get(i + 1);
                    carveSegment(startNode, endNode, cumulativeLength, totalLength);

                    // Update cumulative length
                    cumulativeLength += (float) startNode.vec3().distanceTo(endNode.vec3());

                    // Optionally, yield control to avoid locking the game loop
                    if (i % 10 == 0) {
                        Thread.sleep(10); // Introduce a brief delay to yield control
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Handle thread interruption
            }
        });
    }


    private void carveSegment(SplineNode startNode, SplineNode endNode, float cumulativeLength, float totalLength) {
        Vec3 start = startNode.vec3();
        Vec3 end = endNode.vec3();

        float segmentLength = (float) start.distanceTo(end);
        int numSteps = (int) (segmentLength / (channelRadiusMin / 2.0)); // Adjust steps based on radius

        for (int step = 0; step <= numSteps; step++) {
            float t = (float) step / numSteps;
            Vec3 interpolatedPoint = lerp(start, end, t);

            // Calculate progress along the entire spline
            float currentDistanceAlongSpline = cumulativeLength + t * segmentLength;
            float normalizedDistance = currentDistanceAlongSpline / totalLength;

            // Calculate the target radius based on the normalized distance along the entire spline
            int targetRadius = Math.round(channelRadiusMin + normalizedDistance * (channelRadiusMax - channelRadiusMin));

            int yLevelDiff = TerrainUtils.getYValueAt(interpolatedPoint)-(int)interpolatedPoint.y + 2;
            if (yLevelDiff < 0) yLevelDiff = 0;
            // Carve the terrain at the interpolated point
            carveParabolicChannel(interpolatedPoint, targetRadius, 0, yLevelDiff, Blocks.AIR, Blocks.WATER);
            carveParabolicChannel(interpolatedPoint, targetRadius, 0, -3, Blocks.WATER, Blocks.WATER);
        }
    }


    public void simpleCarveChannelSpline(ArrayList<SplineNode> splineNodes) {
        if (splineNodes.size() < 2) {
            return; // Need at least two points to interpolate
        }

        // Step 1: Calculate the total length of the spline
        float totalLength = 0.0f;
        for (int i = 0; i < splineNodes.size() - 1; i++) {
            Vec3 start = splineNodes.get(i).vec3();
            Vec3 end = splineNodes.get(i + 1).vec3();
            totalLength += (float) start.distanceTo(end);
        }

        // Step 2: Iterate through each segment and carve the river
        float cumulativeLength = 0.0f;

        for (int i = 0; i < splineNodes.size() - 1; i++) {

            if(i>0) {
                RenderHandler.AddLineIfAbsent("spline-"+ splineNodes.get(i).vec3().toString(), splineNodes.get(i - 1).vec3(), splineNodes.get(i).vec3(), 255, 100, 180, 255);
            }

            Vec3 start = splineNodes.get(i).vec3();
            Vec3 end = splineNodes.get(i + 1).vec3();

            float segmentLength = (float) start.distanceTo(end);
            int numSteps = (int) (segmentLength / (channelRadiusMin / 2.0)); // Adjust steps based on radius

            for (int step = 0; step <= numSteps; step++) {
                float t = (float) step / numSteps;
                Vec3 interpolatedPoint = lerp(start, end, t);

                // Calculate progress along the entire spline
                float currentDistanceAlongSpline = cumulativeLength + t * segmentLength;
                float normalizedDistance = currentDistanceAlongSpline / totalLength;

                // Calculate the target radius based on the normalized distance along the entire spline
                int targetRadius = Math.round(channelRadiusMin + normalizedDistance * (channelRadiusMax - channelRadiusMin));

                // Carve the terrain at the interpolated point
                //carveSphere(interpolatedPoint, targetRadius);
                carveParabolicChannel(interpolatedPoint, targetRadius, 0, 10, Blocks.AIR, Blocks.WATER);
                carveParabolicChannel(interpolatedPoint, targetRadius, 0, -3, Blocks.WATER, Blocks.WATER);
                // TODO: Fix flood fill algo
                //fillWater(interpolatedPoint, targetRadius);
            }
            cumulativeLength += segmentLength;
        }
    }


    private Vec3 lerp(Vec3 start, Vec3 end, float t) {
        double x = start.x + t * (end.x - start.x);
        double y = start.y + t * (end.y - start.y);
        double z = start.z + t * (end.z - start.z);
        return new Vec3(x, y, z);
    }

    private void carveSphere(Vec3 center, int radius) {
        int radiusSquared = radius * radius;
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radiusSquared) {
                        blockPos.set(center.x + x, center.y + y, center.z + z);
                        TerrainUtils.setBlock(blockPos, Blocks.AIR);
                    }
                }
            }
        }

    }

    private void fillWater(Vec3 start, int maxRadius) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        // Start filling from the center bottom of the channel
        blockPos.set(start.x, start.y, start.z);
        queue.add(blockPos.immutable());

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!visited.add(current)) {
                continue; // Skip if already visited
            }

            // Set the current position to water
            TerrainUtils.setBlock(current, Blocks.WATER);

            // Get neighboring positions (6 directions: up, down, north, south, east, west)
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) == 1) { // Ensure only orthogonal neighbors
                            BlockPos neighbor = current.offset(dx, dy, dz);

                            // Check if the neighbor is within the channel bounds and is air
                            if (!visited.contains(neighbor) && isWithinChannelBounds(neighbor, start, maxRadius, -2)) {
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isWithinChannelBounds(BlockPos pos, Vec3 center, int maxRadius, int heightOffset) {
        // Calculate the squared distance from the center
        int dx = pos.getX() - (int)center.x;
        int dz = pos.getZ() - (int)center.z;
        int distanceSquared = dx * dx + dz * dz;

        // Ensure the position is within the max radius and below or at the water level (y-coordinate)
        return distanceSquared <= maxRadius * maxRadius && pos.getY() < (center.y - heightOffset);
    }

    private void carveParabolicChannel(Vec3 center, int maxRadius, int heightOffset, int heightAboveCenter, Block block, Block ifNotBlock) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        // Define the falloff of the parabola, you can adjust the coefficient to make it steeper or shallower
        float falloffCoefficient = 0.8f / (maxRadius * maxRadius);

        for (int x = -maxRadius; x <= maxRadius; x++) {
            for (int z = -maxRadius; z <= maxRadius; z++) {
                // Calculate distance from the center horizontally
                int distanceSquared = x * x + z * z;

                if (distanceSquared <= maxRadius * maxRadius) {
                    // Calculate the maximum depth and height of the channel at this point
                    float normalizedDistance = distanceSquared * falloffCoefficient;
                    int depth = Math.round(maxRadius * (1 - normalizedDistance)); // Parabolic falloff

                    // Adjust carving range to reach above the center
                    for (int y = -depth; y <= heightAboveCenter + heightOffset; y++) {
                        blockPos.set(center.x + x, center.y + y, center.z + z);
                        if (level.getBlockState(blockPos) != ifNotBlock.defaultBlockState()) {
                            TerrainUtils.setBlock(blockPos, block);
                        }
                    }
                }
            }
        }
    }

    private void carveGaussianChannel(Vec3 center, int maxRadius, int heightOffset, int heightAboveCenter, Block block, Block ifNotBlock) {
    BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

    // Define the coefficient for the Gaussian falloff (inverse bell curve)
    float bellCoefficient = 4.0f / (maxRadius * maxRadius); // Adjust this coefficient to control the curve's width

    for (int x = -maxRadius; x <= maxRadius; x++) {
        for (int z = -maxRadius; z <= maxRadius; z++) {
            // Calculate the squared distance from the center horizontally
            int distanceSquared = x * x + z * z;

            if (distanceSquared <= maxRadius * maxRadius) {
                // Calculate the depth using the inverse bell curve (Gaussian function)
                float normalizedDistance = (float) distanceSquared / (maxRadius * maxRadius);
                int depth = Math.round(maxRadius * (1.0f - (float) Math.exp(-bellCoefficient * distanceSquared)));

                // Adjust carving range to reach above the center
                for (int y = -depth; y <= heightAboveCenter + heightOffset; y++) {
                    blockPos.set(center.x + x, center.y + y, center.z + z);
                    if (level.getBlockState(blockPos) != ifNotBlock.defaultBlockState()) {
                        TerrainUtils.setBlock(blockPos, block);
                    }
                }
            }
        }
    }
}



}
