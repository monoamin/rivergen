package net.monoamin.rivergen.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.phys.Vec3;
import net.monoamin.rivergen.gen.RiverGenerationHandler;

import java.util.Random;

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
                int ynx = getYValueAt(pos.offset(-1,0,0));
                int ypx = getYValueAt(pos.offset(1,0,0));
                int ynz = getYValueAt(pos.offset(0,0,-1));
                int ypz = getYValueAt(pos.offset(0,0,1));

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

    public static int getYValueAt(BlockPos blockPos) {
        return getYValueAt(blockPos.getX(), blockPos.getZ());
    }

    public static int getYValueAt(int x, int z) {
        BlockPos blockPos = new BlockPos(x, 0, z);

        ChunkAccess chunkAccess = RiverGenerationHandler.serverLevel.getChunk(blockPos);
        ChunkStatus chunkStatus = chunkAccess.getStatus();
        if (chunkStatus.isOrAfter(ChunkStatus.SURFACE))
        {
            return chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x,z);
        }
        else
        {
            chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
            return chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x,z);
        }
    }

    public static double getFinalDensityAt(BlockPos blockPos) {
        NoiseBasedChunkGenerator generator = (NoiseBasedChunkGenerator) RiverGenerationHandler.serverLevel.getChunkSource().getGenerator();
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


    public static String idFromXZ(BlockPos pos) {
        return String.format("%05d", pos.getX()) + String.format("%05d", pos.getZ());
    }

    public static String idFromVec3(Vec3 vec) {
        return String.format("%08d", (int)vec.x) + String.format("%08d", (int)vec.y) + String.format("%08d", (int)vec.z);
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
        RiverGenerationHandler.serverLevel.getChunkSource().getChunkNow(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        RiverGenerationHandler.serverLevel.setBlock(blockPos, block.defaultBlockState(), 3);
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
}
