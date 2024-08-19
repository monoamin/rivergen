package net.monoamin.erosion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Stack;

public class FluidGrid {

    // Stores surface normals at each BlockPos
    HashMap<BlockPos, Vec3> normals;

    // Stores flow directions as 3D vectors at each BlockPos
    HashMap<BlockPos, Vec3> flows;

    // Stores flow accumulation vectors at each BlockPos
    HashMap<BlockPos, Vec3> accumulations;

    // Stack for processing accumulations
    Stack<BlockPos> accumulationProcessingStack;

    ServerLevel serverLevel;

    Boolean lines = false;

    public FluidGrid(ServerLevel level, boolean drawLines) {
        // Initialize data structures
        normals = new HashMap<>();
        accumulations = new HashMap<>();
        flows = new HashMap<>();
        accumulationProcessingStack = new Stack<>();
        serverLevel = level;
        lines = drawLines;
    }

    // Start the process at the player's position or any given BlockPos
    public void start(BlockPos pos, boolean drawLines) {
        // Initial push to the accumulation processing stack
        accumulationProcessingStack.push(pos);

        // Process accumulations until the stack is empty
        while (!accumulationProcessingStack.isEmpty()) {
            processAccumulations(drawLines);
        }
    }

    // Process flow accumulation in the stack
    private void processAccumulations(boolean drawLines) {
        while (!accumulationProcessingStack.isEmpty()) {
            BlockPos pos = accumulationProcessingStack.pop();

            // If accumulation at this position hasn't been calculated yet
            if (!accumulations.containsKey(pos)) {
                // Ensure normal and flow are calculated for this position
                if (!normals.containsKey(pos)) {
                    calculateNormalAndFlow(pos, drawLines);
                }

                Vec3 accumulation = new Vec3(0, 0, 0);

                // Look at all neighbors and accumulate their flow if their flow intersects with this point
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && z == 0) continue;  // Skip the center point itself

                        BlockPos neighborPos = pos.offset(x, 0, z);
                        BlockPos surfacePosAtNeighbor = new BlockPos(neighborPos.getX(), Util.getYValueAt(neighborPos, serverLevel), neighborPos.getZ());

                        if (surfacePosAtNeighbor.getY() <= pos.getY()) continue;

                        // If the neighbor's accumulation hasn't been processed yet, process it first
                        if (!accumulations.containsKey(surfacePosAtNeighbor)) {
                            accumulationProcessingStack.push(surfacePosAtNeighbor);
                            continue;  // Process the neighbor before continuing
                        }

                        // Calculate contribution from this neighbor
                        Vec3 neighborFlow = flows.get(surfacePosAtNeighbor);
                        Vec3 neighborAccumulation = accumulations.get(surfacePosAtNeighbor);
                        Vec3 toCurrent = calculateFlowDirectionToCurrent(pos, surfacePosAtNeighbor);

                        // Accumulate the flow if it points towards the current position
                        if (neighborFlow.dot(toCurrent) > 0.5) {  // Use a threshold to ensure alignment
                            accumulation = accumulation.add(neighborAccumulation);
                        }
                    }
                }

                // Add the cell's own contribution to the accumulation
                accumulations.put(pos, accumulation.add(new Vec3(0, 1, 0)));  // 1 unit for self

                // After calculating accumulation, you may process downstream flow or continue to trace rivers
            }
        }
    }

    // Calculate normal and flow direction for a given position
    private void calculateNormalAndFlow(BlockPos pos, boolean drawLines) {
        Vec3 vec_pos = new Vec3(pos.getX(), pos.getY(), pos.getZ());

        // Calculate smoothed normal
        Vec3 n_pos = Util.getSmoothedNormal(pos, serverLevel, 3);

        // Calculate flow direction from the normal (projecting to the xz-plane)
        Vec3 f_pos = new Vec3(n_pos.x, 0d, n_pos.z).normalize();

        // Store normal and flow direction
        normals.put(pos, n_pos);
        flows.put(pos, f_pos);

        if (drawLines) {
            ChatMessageHandler.Send(vec_pos.toString() + " | " + n_pos.toString(), serverLevel);
            RenderHandler.AddLineIfAbsent(Util.idFromXZ(pos), vec_pos.add(new Vec3(0,0.5,0)), vec_pos.add(new Vec3(0,0.5,0)).add(f_pos));
        }
    }

    private Vec3 calculateFlowDirectionToCurrent(BlockPos currentPos, BlockPos neighborPos) {
        // Calculate the direction vector from the neighbor position to the current position
        return new Vec3(
                currentPos.getX() - neighborPos.getX(),
                0d,
                currentPos.getZ() - neighborPos.getZ()
        ).normalize();
    }

    // Trace and carve river paths from points of maximum accumulation
    private void traceRivers() {
        // Implementation of tracing river paths, carving channels, and applying random perturbation
        // would go here. You'd iterate over accumulations, find maxima, and follow downstream paths
        // while adding perturbations and creating splines as described.
    }

    // Additional helper methods can be added here for river tracing, carving, and finalizing the terrain.

}
