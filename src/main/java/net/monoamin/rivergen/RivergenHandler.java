package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "rivergen")
public class RivergenHandler {

    static int tickCounter = 0;
    static int radius = 20;
    static ServerLevel serverLevel;
    static boolean world_isLoaded = false;
    static FluidGrid fluidGrid;
    static RiverNetwork riverNetwork;
    static TerrainCarver terrainCarver;
    static Random r = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        tickCounter++;
        if (tickCounter % 200 == 0) { // Once per 10s
            if (world_isLoaded) {
                for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
                    BlockPos playerPos = new BlockPos(player.getBlockX(), player.getBlockY() - 1, player.getBlockZ());
                    BlockPos targetPos = new BlockPos(0,0,0);
                    while (targetPos.getY() < 80)  {
                        targetPos = Util.getRandomXZWithinCircle(playerPos.getX(), playerPos.getZ(), 10, 100, serverLevel.getServer().overworld());
                    }
                    traceRiver(targetPos);
                    ChatMessageHandler.Send("Traced River at " + targetPos.toString(), serverLevel);
                }
            }
            tickCounter = 0;
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            serverLevel = level;
            world_isLoaded = true;
            //fluidGrid = new FluidGrid(level, true);
            riverNetwork = new RiverNetwork(level, true);
            terrainCarver = new TerrainCarver(level, 3, 8);
            ChatMessageHandler.Send("Loaded.", serverLevel);
        }
    }

    public static void traceRiver(BlockPos blockPos) {
        //ChatMessageHandler.Send("Running...", serverLevel);
        River r = riverNetwork.start(Util.BlockPosToVec3(blockPos), true);
        terrainCarver.carveChannel(r.getPath());
    }
}
