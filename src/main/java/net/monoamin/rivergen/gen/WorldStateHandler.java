package net.monoamin.rivergen.gen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.monoamin.rivergen.debug.DebugMessage;
import net.monoamin.rivergen.mathutils.Spline;
import net.monoamin.rivergen.terrain.*;

import java.util.HashMap;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "rivergen")
public class WorldStateHandler {

    static int tickCounter = 0;
    static int radius = 20;
    public static ServerLevel serverLevel;
    static boolean world_isLoaded = false;
    public static ErosionDataHolder erosionDataHolder;
    static RiverNetwork riverNetwork;
    static TerrainCarver terrainCarver;
    public static ChunkGraphMap chunkGraphMap;
    public static ContextLayerManager contextLayerManager;
    static Random r = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            serverLevel = level.getServer().overworld();
            world_isLoaded = true;
            riverNetwork = new RiverNetwork(serverLevel, true);
            terrainCarver = new TerrainCarver(serverLevel, 3, 8);
            contextLayerManager = new ContextLayerManager();

            // Initialize our context layers
            contextLayerManager.addLayer(ContextLayer.Types.ELEVATION, new ElevationContextLayer());
            contextLayerManager.addLayer(ContextLayer.Types.CONNECTION_GRAPH, new GraphContextLayer());

            DebugMessage.Send("startup complete.", serverLevel);
        }
    }

    // TODO: Move somewhere more appropriate or delete
    public static void traceRiver(Vec3 pos) {
        DebugMessage.Send("running rivergen...", serverLevel);
        River r = riverNetwork.traceRiverFrom(pos, false);

        if (r.length() >= 4) { // catmull rom needs at least 4 points to interpolate
            Spline s = new Spline(r.getPath());
            terrainCarver.asyncCarveChannelSpline(s.generateSplinePoints(20));
        }
        /* if we have less than use the simple carver
        else{
            terrainCarver.simpleCarveChannelSpline(r.getPath());
        }
        */
    }
}
