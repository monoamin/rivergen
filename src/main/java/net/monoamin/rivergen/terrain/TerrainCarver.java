package net.monoamin.rivergen.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.monoamin.rivergen.render.RenderHandler;
import net.monoamin.rivergen.spline.SplineNode;

import java.util.ArrayList;

public class TerrainCarver {

    private final ServerLevel level; // Your Minecraft world object
    private final int channelRadiusMax; // Radius of the channel's spherical cursor
    private final int channelRadiusMin;

    public TerrainCarver(ServerLevel level, int channelRadiusMin, int channelRadiusMax) {
        this.level = level;
        this.channelRadiusMax = channelRadiusMax;
        this.channelRadiusMin = channelRadiusMin;
    }

    public void carveChannel(ArrayList<Vec3> pathNodes) {
        if (pathNodes.size() < 2) {
            return; // Need at least two points to interpolate
        }

        for (Vec3 node: pathNodes){
        }


        for (int i = 0; i < pathNodes.size() - 1; i++) {

            if(i>0) {
                RenderHandler.AddLineIfAbsent("spline-"+ TerrainUtils.idFromVec3(pathNodes.get(i)), pathNodes.get(i - 1), pathNodes.get(i), 255, 100, 180, 255);
            }

            Vec3 start = pathNodes.get(i);
            Vec3 end = pathNodes.get(i + 1);

            // Interpolate between start and end
            float distance = (float) start.distanceTo(end);
            int numSteps = (int) (distance / (channelRadiusMin / 2.0)); // Adjust steps based on radius

            for (int step = 0; step <= numSteps; step++) {
                float t = (float) step / numSteps;
                Vec3 interpolatedPoint = lerp(start, end, t);

                // Carve the terrain at the interpolated point
                var normalizedStep = step / (float) pathNodes.size();
                int targetRadius = Math.round(channelRadiusMin + normalizedStep * (channelRadiusMax - channelRadiusMin));
                carveSphere(interpolatedPoint, targetRadius);
            }
        }
    }

    public void carveChannelSpline(ArrayList<SplineNode> splineNodes)
    {
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
                carveSphere(interpolatedPoint, targetRadius);
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

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y < 0; y++) {
                for (int z = -radius; z <= radius; z++) {
                    int j = x * x + y * y + z * z;
                    if ((j <= radiusSquared) && (j >= channelRadiusMin/2)) {
                        blockPos.set(center.x + x, center.y + y, center.z + z);
                        TerrainUtils.setBlock(blockPos, Blocks.WATER);
                    }
                }
            }
        }

    }
}
