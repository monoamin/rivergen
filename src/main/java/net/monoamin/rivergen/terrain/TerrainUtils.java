package net.monoamin.rivergen.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.monoamin.rivergen.gen.WorldStateHandler;

import java.util.*;
import java.util.stream.Collectors;

public class TerrainUtils {

    public static Vec3 getSmoothedNormalCorrect(BlockPos pos, int radius) {
        Vec3 accumulatedNormal = new Vec3(0, 0, 0);
        double totalWeight = 0;

        // Loop through a square grid around the target position
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                // Get the heights at the current sample point and its neighbors
                int currentX = pos.getX() + dx;
                int currentZ = pos.getZ() + dz;
                BlockPos currentPos = pos.offset(dx, 0, dz);

                int y = getYValueAt(currentPos);
                int ynx = getYValueAt(currentPos.offset(-1, 0, 0));
                int ypx = getYValueAt(currentPos.offset(1, 0, 0));
                int ynz = getYValueAt(currentPos.offset(0, 0, -1));
                int ypz = getYValueAt(currentPos.offset(0, 0, 1));

                // Correct calculation of the local normal
                double dxHeight = (double) (ypx - ynx) / 2.0;
                double dzHeight = (double) (ypz - ynz) / 2.0;
                Vec3 localNormal = new Vec3(-dxHeight, 1.0, -dzHeight).normalize();

                // Calculate the distance from the center and use it as a weight
                double distance = Math.sqrt(dx * dx + dz * dz);
                double weight = 1.0 / (distance + 1.0); // Add 1.0 to avoid division by zero

                // Accumulate the weighted normal
                accumulatedNormal = accumulatedNormal.add(localNormal.scale(weight));
                totalWeight += weight;
            }
        }

        // Average the accumulated normal
        Vec3 averagedNormal = accumulatedNormal.scale(1.0 / totalWeight);

        // Normalize the final vector to ensure it is a unit vector
        return averagedNormal.normalize();
    }

    public static Vec3 getSmoothedNormal(BlockPos pos, int radius) {
        Vec3 accumulatedNormal = new Vec3(0, 0, 0);
        double totalWeight = 0;

        // Loop through a square grid around the target position
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                // Get the heights at the current sample point and its neighbors
                //int currentX = pos.getX() + dx;
                //int currentZ = pos.getZ() + dz;
                //int y = getYValueAt(pos);
                int ynx = getYValueAt(pos.offset(-1, 0, 0));
                int ypx = getYValueAt(pos.offset(1, 0, 0));
                int ynz = getYValueAt(pos.offset(0, 0, -1));
                int ypz = getYValueAt(pos.offset(0, 0, 1));

                // Correct calculation of the local normal
                double dxHeight = (double) (ypx - ynx) / 2.0;
                double dzHeight = (double) (ypz - ynz) / 2.0;
                Vec3 localNormal = new Vec3(-dxHeight, 1.0, -dzHeight).normalize();

                // Calculate the distance from the center and use it as a weight
                double distance = Math.sqrt(dx * dx + dz * dz);
                double weight = 1.0 / (distance + 1.0); // Add 1.0 to avoid division by zero

                // Accumulate the weighted normal
                accumulatedNormal = accumulatedNormal.add(localNormal.scale(weight));
                totalWeight += weight;
            }
        }

        // Average the accumulated normal
        Vec3 averagedNormal = accumulatedNormal.scale(1.0 / totalWeight);

        // Normalize the final vector to ensure it is a unit vector
        return averagedNormal.normalize();
    }

    public static int getYValueAt(Vec3 vec3) {
        return getYValueAt((int) vec3.x, (int) vec3.z);
    }

    public static int getYValueAt(BlockPos blockPos) {
        return getYValueAt(blockPos.getX(), blockPos.getZ());
    }

    public static List<Vec2> getLowestVonNeumannNeighbors(Vec2 from, int[] neighborHeights) {
        // VonNeumann neighborhood includes 4 directions: north, south, east, west
        // Order of heights in the array:
        // [ypx, ynx, ypz, ynz]

        Vec2[] directions = {
                Vec2.UNIT_X,        // East
                Vec2.NEG_UNIT_X,    // West
                Vec2.UNIT_Y,        // North
                Vec2.NEG_UNIT_Y     // South
        };

        int lowest = Integer.MAX_VALUE;
        List<Vec2> lowestNeighbors = new ArrayList<>();

        // Find the lowest height value among neighbors
        for (int i = 0; i < neighborHeights.length; i++) {
            if (neighborHeights[i] < lowest) {
                lowest = neighborHeights[i];
                lowestNeighbors.clear();  // Clear current list when a new lowest is found
                lowestNeighbors.add(directions[i]);
            } else if (neighborHeights[i] == lowest) {
                lowestNeighbors.add(directions[i]);
            }
        }

        // Convert directions relative to 'from'
        return lowestNeighbors.stream()
                .map(from::add)
                .collect(Collectors.toList());
    }

    public static List<Vec2> getHighestVonNeumannNeighbors(Vec2 from) {
        int highest = -10000;
        int ypx = getYValueAt((int) from.x + 1, (int) from.y);
        int ynx = getYValueAt((int) from.x - 1, (int) from.y);
        int ypz = getYValueAt((int) from.x, (int) from.y + 1);
        int ynz = getYValueAt((int) from.x, (int) from.y - 1);

        // Find the minimum value among all neighbors
        highest = Math.max(highest, ypx);
        highest = Math.max(highest, ynx);
        highest = Math.max(highest, ypz);
        highest = Math.max(highest, ynz);

        Map<Vec2, Integer> neighbors = new HashMap<>();
        neighbors.put(from.add(Vec2.UNIT_X), ypx);
        neighbors.put(from.add(Vec2.NEG_UNIT_X), ynx);
        neighbors.put(from.add(Vec2.UNIT_Y), ypz);
        neighbors.put(from.add(Vec2.NEG_UNIT_Y), ynz);

        int a = neighbors.get(Vec2.UNIT_X);
        int b = neighbors.get(Vec2.NEG_UNIT_X);
        int c = neighbors.get(Vec2.UNIT_Y);
        int d = neighbors.get(Vec2.NEG_UNIT_Y);

        List<Vec2> allHighest = new ArrayList<>();
        if (a == highest) allHighest.add(Vec2.UNIT_X);
        if (b == highest) allHighest.add(Vec2.NEG_UNIT_X);
        if (c == highest) allHighest.add(Vec2.UNIT_Y);
        if (d == highest) allHighest.add(Vec2.NEG_UNIT_Y);

        return allHighest;
    }

    public static List<Vec2> getLowestMooreNeighbors(Vec2 from, int[] neighborHeights) {
        // Moore neighborhood includes 8 directions: north, south, east, west, and the 4 diagonals.
        // Order of heights in the array:
        // [ypx, ynx, ypz, ynz, ypxz, ynxz, ypxnz, ynxnz]

        Vec2[] directions = {
                Vec2.UNIT_X,        // East
                Vec2.NEG_UNIT_X,    // West
                Vec2.UNIT_Y,        // North
                Vec2.NEG_UNIT_Y,    // South
                Vec2.UNIT_X.add(Vec2.UNIT_Y),    // Northeast (East + North)
                Vec2.NEG_UNIT_X.add(Vec2.UNIT_Y),// Northwest (West + North)
                Vec2.UNIT_X.add(Vec2.NEG_UNIT_Y),// Southeast (East + South)
                Vec2.NEG_UNIT_X.add(Vec2.NEG_UNIT_Y) // Southwest (West + South)
        };

        int lowest = Integer.MAX_VALUE;
        List<Vec2> lowestNeighbors = new ArrayList<>();

        // Find the lowest height value among neighbors
        for (int i = 0; i < neighborHeights.length; i++) {
            if (neighborHeights[i] < lowest) {
                lowest = neighborHeights[i];
                lowestNeighbors.clear();  // Clear current list when a new lowest is found
                lowestNeighbors.add(directions[i]);
            } else if (neighborHeights[i] == lowest) {
                lowestNeighbors.add(directions[i]);
            }
        }

        // Convert directions relative to 'from'
        return lowestNeighbors.stream()
                .map(from::add)
                .collect(Collectors.toList());
    }



    public static int getYValueAt(int x, int z) {
        BlockPos blockPos = new BlockPos(x, 0, z);

        ChunkAccess chunkAccess = WorldStateHandler.serverLevel.getChunk(blockPos);
        ChunkStatus chunkStatus = chunkAccess.getStatus();
        if (chunkStatus.isOrAfter(ChunkStatus.NOISE)) {
            return chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        } else {
            // This triggers full chunk generation :(
            // Maybe.. //RiverGenerationHandler.serverLevel.getChunkSource().getGenerator().fillFromNoise() ...
            chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
            return chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        }
    }


    public static double getFinalDensityAt(BlockPos blockPos) {
        NoiseBasedChunkGenerator generator = (NoiseBasedChunkGenerator) WorldStateHandler.serverLevel.getChunkSource().getGenerator();
        return generator.generatorSettings().get().noiseRouter().finalDensity().compute(new DensityFunction.SinglePointContext(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    public static BlockPos getLowestCircular(Vec3 center, int initialRadius, int samplingDirections) {
        BlockPos lowestPos = TerrainUtils.Vec3ToBlockPos(center);
        double angleStep = Math.toRadians(360.0 / samplingDirections);

        for (int r = 0; r < samplingDirections; r++)
        {
            double finalAngle_rads = r * angleStep;
            int posX = (int)Math.round(initialRadius * Math.cos(finalAngle_rads));
            int posZ = (int)Math.round(initialRadius * Math.sin(finalAngle_rads));
            int posY = getYValueAt((int)center.x + posX, (int)center.z + posZ);  // Adjusted if needed

            if (posY < lowestPos.getY())
            {
                lowestPos = new BlockPos((int)center.x + posX, posY, (int)center.z + posZ);
            }
        }

        return lowestPos;
    }

    public static Vec3 getWeightedDirectionTowardsLowest(Vec3 center, int initialRadius, int samplingDirections) {
        Vec3 weightedDirection = new Vec3(0, 0, 0); // Initialize a zero vector
        double angleStep = Math.toRadians(360.0 / samplingDirections);
        double totalWeight = 0;

        for (int r = 0; r < samplingDirections; r++) {
            double finalAngle_rads = r * angleStep;
            int posX = (int) Math.round(initialRadius * Math.cos(finalAngle_rads));
            int posZ = (int) Math.round(initialRadius * Math.sin(finalAngle_rads));
            int posY = getYValueAt((int) center.x + posX, (int) center.z + posZ);

            // Calculate the difference in height relative to the center
            int heightDifference = (int) center.y - posY;

            // If the sampled point is lower, it has a higher influence
            if (heightDifference > 0) {
                // Compute the direction vector from the center to the current sampled point
                Vec3 direction = new Vec3(posX, 0, posZ).normalize(); // Ignore the Y component for direction

                // Weight this direction by the height difference and add it to the weighted direction
                weightedDirection = weightedDirection.add(direction.scale(heightDifference));
                totalWeight += heightDifference;
            }
        }

        // Normalize the weighted direction to get the final direction vector
        if (totalWeight > 0) {
            weightedDirection = weightedDirection.scale(1.0 / totalWeight);
        }

        return weightedDirection.normalize();
    }


    public static Vec3 BlockPosToVec3(BlockPos pos)
    {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }
    public static BlockPos Vec3ToBlockPos(Vec3 vec)
    {
        return new BlockPos((int)vec.x, (int)vec.y, (int)vec.z);
    }

    public static void setBlock(BlockPos blockPos, Block block) {
        //RiverGenerationHandler.serverLevel.getChunkSource().getChunkNow(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        WorldStateHandler.serverLevel.setBlock(blockPos, block.defaultBlockState(), 3);
    }

    public static BlockPos getRandomXZWithinCircle(int centerX, int centerZ, double minDistance, double maxDistance, ServerLevel level) {
        Random random = new Random();

        // Generate a random angle between 0 and 2π
        double angle = random.nextDouble() * 2 * Math.PI;

        // Generate a random distance between minDistance and maxDistance
        double distance = minDistance + (random.nextDouble() * (maxDistance - minDistance));

        // Calculate the new X and Z coordinates
        int x = centerX + (int)(distance * Math.cos(angle));
        int z = centerZ + (int)(distance * Math.sin(angle));

        return new BlockPos(x, getYValueAt(x, z), z);
    }

    public static Vec3 blendVec3(Vec3 directionA, Vec3 directionBias, double biasFactor) {
        // Ensure the bias factor is within the range [0, 1]
        biasFactor = Math.max(0, Math.min(1, biasFactor));

        // Calculate the blended vector
        Vec3 blendedVec = directionA.scale(1 - biasFactor).add(directionBias.scale(biasFactor));

        // Optionally normalize the blended vector if you want a unit vector
        return blendedVec.normalize();
    }

    public static long[][] deserializeHeightMap(long[] heights)
    {
        if (heights.length != 256) {
            throw new IllegalArgumentException("Array length must be 256 for a 16x16 matrix.");
        }

        long[][] matrix = new long[16][16];

        for (int i = 0; i < heights.length; i++) {
            int row = i / 16;  // Calculate the row index
            int col = i % 16;  // Calculate the column index
            matrix[row][col] = heights[i];
        }

        return matrix;
    }
}
