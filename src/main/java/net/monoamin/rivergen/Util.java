package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.phys.Vec3;

public class Util {

    public static BlockState getColoredWoolForNormal(Vec3 normal) {
        // Extract the x and z components of the normal
        double nx = normal.x;
        double nz = normal.z;

        // Determine the direction of the normal in the XY plane
        double angle = Math.atan2(nz, nx); // Angle in radians from the x-axis

        // Normalize the angle to be between 0 and 2 * PI
        if (angle < 0) {
            angle += 2 * Math.PI;
        }

        if (calculateSlope(normal) > 0.01) {
            // Define thresholds for color mapping (8 directions corresponding to Minecraft wool colors)
            if (angle >= 0 && angle < Math.PI / 4) {
                return Blocks.BLUE_WOOL.defaultBlockState(); // East (0 degrees)
            } else if (angle >= Math.PI / 4 && angle < Math.PI / 2) {
                return Blocks.CYAN_WOOL.defaultBlockState(); // North-East (45 degrees)
            } else if (angle >= Math.PI / 2 && angle < 3 * Math.PI / 4) {
                return Blocks.WHITE_WOOL.defaultBlockState(); // North (90 degrees)
            } else if (angle >= 3 * Math.PI / 4 && angle < Math.PI) {
                return Blocks.PINK_WOOL.defaultBlockState(); // North-West (135 degrees)
            } else if (angle >= Math.PI && angle < 5 * Math.PI / 4) {
                return Blocks.MAGENTA_WOOL.defaultBlockState(); // West (180 degrees)
            } else if (angle >= 5 * Math.PI / 4 && angle < 3 * Math.PI / 2) {
                return Blocks.PURPLE_WOOL.defaultBlockState(); // South-West (225 degrees)
            } else if (angle >= 3 * Math.PI / 2 && angle < 7 * Math.PI / 4) {
                return Blocks.BLACK_WOOL.defaultBlockState(); // South (270 degrees)
            } else {
                return Blocks.GRAY_WOOL.defaultBlockState(); // South-East (315 degrees)
            }
        }
        else
        {
            return Blocks.WHITE_WOOL.defaultBlockState(); // Low Slope / Flat area
        }
    }

    public static double calculateSlope(Vec3 normal) {
        double horizontalMagnitude = Math.sqrt(normal.x * normal.x + normal.z * normal.z);
        double verticalComponent = normal.y;
        return horizontalMagnitude / verticalComponent;
    }

    public static Vec3 getSmoothedNormalCorrect(BlockPos pos, ServerLevel level, int radius) {
        Vec3 accumulatedNormal = new Vec3(0, 0, 0);
        double totalWeight = 0;

        // Loop through a square grid around the target position
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                // Get the heights at the current sample point and its neighbors
                int currentX = pos.getX() + dx;
                int currentZ = pos.getZ() + dz;
                BlockPos currentPos = pos.offset(dx, 0, dz);

                int y = getYValueAt(currentPos, level);
                int ynx = getYValueAt(currentPos.offset(-1, 0, 0), level);
                int ypx = getYValueAt(currentPos.offset(1, 0, 0), level);
                int ynz = getYValueAt(currentPos.offset(0, 0, -1), level);
                int ypz = getYValueAt(currentPos.offset(0, 0, 1), level);

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

    public static Vec3 getSmoothedNormal(BlockPos pos, ServerLevel level, int radius) {
        Vec3 accumulatedNormal = new Vec3(0, 0, 0);
        double totalWeight = 0;

        // Loop through a square grid around the target position
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                // Get the heights at the current sample point and its neighbors
                int currentX = pos.getX() + dx;
                int currentZ = pos.getZ() + dz;
                int y = getYValueAt(pos, level);
                int ynx = getYValueAt(pos.offset(-1,0,0), level);
                int ypx = getYValueAt(pos.offset(1,0,0), level);
                int ynz = getYValueAt(pos.offset(0,0,-1), level);
                int ypz = getYValueAt(pos.offset(0,0,1), level);

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

    public static int getYValueAt(BlockPos blockPos, ServerLevel level) {
        //level.getChunk(pos).getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
        //NoiseBasedChunkGenerator generator = (NoiseBasedChunkGenerator) level.getChunkSource().getGenerator();

        return level.getChunkSource().getChunkNow(blockPos.getX() >> 4, blockPos.getZ() >> 4).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPos.getX(), blockPos.getZ());}

    public static int getYValueAt(int x, int z, ServerLevel level) {
        BlockPos blockPos = new BlockPos(x, 0, z);
        return level.getChunkSource().getChunkNow(blockPos.getX() >> 4, blockPos.getZ() >> 4).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    public static double getFinalDensityAt(BlockPos blockPos, ServerLevel level) {
        NoiseBasedChunkGenerator generator = (NoiseBasedChunkGenerator) level.getChunkSource().getGenerator();
        return generator.generatorSettings().get().noiseRouter().finalDensity().compute(new DensityFunction.SinglePointContext(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    public static int getHeightFromDensity(int blockX, int blockZ, ServerLevel level)
    {
        int height = -1;
        for (int y = level.getMaxBuildHeight()-1; y > 62; y--)
        {
            BlockPos cursorPos = new BlockPos(blockX, y, blockZ);

            // Skip if Block is already air
            // Might be faster than simply checking all noise values
            if (level.getBlockState(cursorPos) != null && level.getBlockState(cursorPos) == Blocks.AIR.defaultBlockState()) {
                //ChatMessageHandler.Send("Block at " + cursorPos.toString() + " is air! Skipping...", level);
                continue;
            }

            double density = getFinalDensityAt(cursorPos, level);
            //ChatMessageHandler.Send("Density at " + y + " is: " + Double.toString(density), level);
            if (density > -0.4584d) // Air is -0.4583333333333333333, don't ask me why
            {
                height = y;
                break;
            }


        }
        return height;
    }

    public static BlockPos getLowestCircular(Vec3 center, int initialRadius, int samplingDirections, ServerLevel level)
    {
        BlockPos lowestPos = Util.Vec3ToBlockPos(center);
        double angleStep = Math.toRadians(360.0 / samplingDirections);

        for (int r = 0; r < samplingDirections; r++)
        {
            double finalAngle_rads = r * angleStep;
            int posX = (int)Math.round(initialRadius * Math.cos(finalAngle_rads));
            int posZ = (int)Math.round(initialRadius * Math.sin(finalAngle_rads));
            int posY = getHeightFromDensity((int)center.x + posX, (int)center.z + posZ, level);  // Adjusted if needed

            if (posY < lowestPos.getY())
            {
                lowestPos = new BlockPos((int)center.x + posX, posY, (int)center.z + posZ);
            }
        }

        return lowestPos;
    }


    public static String idFromXZ(BlockPos pos)
    {
        return String.format("%05d", pos.getX()) + String.format("%05d", pos.getZ());
    }

    public static String idFromVec3(Vec3 vec)
    {
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

    public static void setBlock(ServerLevel level, BlockPos blockPos, Block block) {
        level.getChunkSource().getChunkNow(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        level.setBlock(blockPos, block.defaultBlockState(), 3);
    }
}
