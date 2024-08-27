package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.ArrayList;

public class TerrainCarver {

    private ServerLevel level; // Your Minecraft world object
    private int channelRadius; // Radius of the channel's spherical cursor

    public TerrainCarver(ServerLevel level, int channelRadius) {
        this.level = level;
        this.channelRadius = channelRadius;
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
            int numSteps = (int) (distance / (channelRadius / 2.0)); // Adjust steps based on radius

            for (int step = 0; step <= numSteps; step++) {
                float t = (float) step / numSteps;
                Vec3 interpolatedPoint = lerp(start, end, t);

                // Carve the terrain at the interpolated point
                carveSphere(interpolatedPoint);
            }
        }
    }

    private Vec3 lerp(Vec3 start, Vec3 end, float t) {
        double x = start.x + t * (end.x - start.x);
        double y = start.y + t * (end.y - start.y);
        double z = start.z + t * (end.z - start.z);
        return new Vec3(x, y, z);
    }

    private void carveSphere(Vec3 center) {
        int radiusSquared = channelRadius * channelRadius;
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (int x = -channelRadius; x <= channelRadius; x++) {
            for (int y = -channelRadius; y <= channelRadius; y++) {
                for (int z = -channelRadius; z <= channelRadius; z++) {
                    if (x * x + y * y + z * z <= radiusSquared) {
                        blockPos.set(center.x + x, center.y + y, center.z + z);
                        //if (level.getBlockState(blockPos) != Blocks.AIR.defaultBlockState()) {
                            Util.setBlock(level.getServer().overworld(), blockPos, Blocks.AIR);
                        //}
                    }
                }
            }
        }

        for (int x = -channelRadius; x <= channelRadius; x++) {
            for (int y = -channelRadius; y < 0; y++) {
                for (int z = -channelRadius; z <= channelRadius; z++) {
                    if (x * x + y * y + z * z <= radiusSquared) {
                        blockPos.set(center.x + x, center.y + y, center.z + z);
                        //if (level.getBlockState(blockPos) != Blocks.AIR.defaultBlockState()) {
                        Util.setBlock(level.getServer().overworld(), blockPos, Blocks.WATER);
                        //}
                    }
                }
            }
        }

    }
}
