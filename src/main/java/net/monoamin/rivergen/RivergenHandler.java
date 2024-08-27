package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rivergen")
public class RivergenHandler {

    static int tickCounter = 0;
    static int radius = 20;
    static ServerLevel serverLevel;
    static boolean world_isLoaded = false;
    static FluidGrid fluidGrid;
    static RiverNetwork riverNetwork;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        tickCounter++;
        if (tickCounter % 600 == 0) { // Once per 30s
            if (world_isLoaded) {
                //for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
                    //BlockPos pos = new BlockPos(player.getBlockX(), player.getBlockY() - 1, player.getBlockZ());
                    //ChatMessageHandler.Send("pX: " + pos.getX() + " pY: " + pos.getY() + " pZ: " + pos.getZ(), serverLevel);
                //}
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
            ChatMessageHandler.Send("Loaded.", serverLevel);
        }
    }

    public static void traceRiver(BlockPos blockPos) {
        ChatMessageHandler.Send("Running...", serverLevel);
        riverNetwork.start(Util.BlockPosToVec3(blockPos), true);
    }
}
