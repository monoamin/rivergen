package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

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

        for (int i = 0; i < pathNodes.size() - 1; i++) {
            Vec3 start = pathNodes.get(i);
            Vec3 end = pathNodes.get(i + 1);

            // Interpolate between start and end
            float distance = (float) start.distanceTo(end);
            int numSteps = (int) (distance / (channelRadiusMax / 2.0)); // Adjust steps based on radius

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
                        Util.setBlock(level.getServer().overworld(), blockPos, Blocks.AIR);
                    }
                }
            }
        }

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y < 0; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radiusSquared) {
                        blockPos.set(center.x + x, center.y + y, center.z + z);
                        Util.setBlock(level.getServer().overworld(), blockPos, Blocks.WATER);
                    }
                }
            }
        }

    }
}
