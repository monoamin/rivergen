package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class RiverNetwork {

    ArrayList<River> rivers;
    ServerLevel serverLevel;
    Boolean lines = false;

    public RiverNetwork(ServerLevel level, boolean drawLines) {
        // Initialize data structures
        rivers = new ArrayList<>();
        serverLevel = level;
        lines = drawLines;
    }

    public River start(Vec3 startCoordinate, boolean drawLines) {
        River river = new River(startCoordinate, Util.idFromVec3(startCoordinate), serverLevel);

        while (!river.finalized)
        {
            river.doStepAuto();
        }


        rivers.add(river);

        traceRivers();
        return river;
    }

    // Trace and carve river paths from points of maximum accumulation
    private void traceRivers() {
        // Implementation of tracing river paths, carving channels, and applying random perturbation
        // would go here. You'd iterate over paths, while adding perturbations and creating splines as described.

        // foreach river in rivers
        for (River river: rivers)
        {
            for (int i = 1; i <= river.length(); i++) {
                RenderHandler.AddLineIfAbsent(Util.idFromVec3(river.getCoordinateAtIndex(i)), river.getPath().get(i-1).add(0,1,0), river.getPath().get(i).add(0,1,0), 50, 100, 255, 255);
            }
        }

        // carve terrain and place water
    }
}
