package net.monoamin.rivergen.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.monoamin.rivergen.debug.DebugMessage;
import net.monoamin.rivergen.spline.Spline;
import net.monoamin.rivergen.terrain.TerrainCarver;
import net.monoamin.rivergen.terrain.TerrainUtils;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "rivergen")
public class RiverGenerationHandler {

    static int tickCounter = 0;
    static int radius = 20;
    public static ServerLevel serverLevel;
    static boolean world_isLoaded = false;
    public static ErosionDataHolder erosionDataHolder;
    static RiverNetwork riverNetwork;
    static TerrainCarver terrainCarver;
    static Random r = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        /*tickCounter++;
        if (tickCounter % 200 == 0) { // Once per 10s
            if (world_isLoaded) {
                for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
                    BlockPos playerPos = new BlockPos(player.getBlockX(), player.getBlockY() - 1, player.getBlockZ());
                    BlockPos targetPos = new BlockPos(0,0,0);
                    while (targetPos.getY() < 80)  {
                        targetPos = TerrainUtils.getRandomXZWithinCircle(playerPos.getX(), playerPos.getZ(), 10, 100, serverLevel.getServer().overworld());
                    }
                    traceRiver(targetPos);
                    DebugMessage.Send("Traced River at " + targetPos.toString(), serverLevel);
                }
            }
            tickCounter = 0;
        }*/
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            serverLevel = level.getServer().overworld();
            world_isLoaded = true;
            //erosionDataHolder = new ErosionDataHolder(level, true);
            riverNetwork = new RiverNetwork(serverLevel, true);
            terrainCarver = new TerrainCarver(serverLevel, 3, 8);
            DebugMessage.Send("Loaded.", serverLevel);
        }
    }

    public static void traceRiver(BlockPos blockPos) {
        //DebugMessage.Send("Running...", serverLevel);
        River r = riverNetwork.start(TerrainUtils.BlockPosToVec3(blockPos), false);

        if (r.length() >= 4) {
            Spline s = new Spline(r.getPath());
            terrainCarver.carveChannelSpline(s.generateSplinePoints(20));
        }
        else{
            terrainCarver.carveChannel(r.getPath());
        }

    }
}
