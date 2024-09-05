package net.monoamin.rivergen.gen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.monoamin.rivergen.terrain.TerrainUtils;

import java.util.ArrayList;

public class RiverNetwork {

    ArrayList<River> rivers;
    ServerLevel serverLevel;
    Boolean lines;

    public RiverNetwork(ServerLevel level, boolean drawLines) {
        // Initialize data structures
        rivers = new ArrayList<>();
        serverLevel = level;
        lines = drawLines;
    }

    public River traceRiverFrom(Vec3 startCoordinate, boolean drawLines) {
        River river = new River(startCoordinate, startCoordinate.toString(), serverLevel);
        while (!river.finalized)
        {
            river.doStepAuto();
        }
        rivers.add(river);
        return river;
    }
}
