package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
        while (!accumulationProcessingStack.isEmpty() && accumulationProcessingStack.size() <= 100) {
            processAccumulations(drawLines);

            if (accumulationProcessingStack.size() == 50)
            {
                ChatMessageHandler.Send("Stopped due to: stack = max stack size", serverLevel);
                accumulationProcessingStack.clear();
            }
        }
    }

    // Process flow accumulation in the stack
    // Process flow accumulation in the stack
    private void processAccumulations(boolean drawLines) {
        // Define the offsets for neighbor positions
        int[][] offsets = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        while (!accumulationProcessingStack.isEmpty()) {
            BlockPos pos = accumulationProcessingStack.pop();

            // If accumulation at this position hasn't been calculated yet
            if (!accumulations.containsKey(pos)) {
                // Ensure normal and flow are calculated for this position

                // Process the neighbors for normal and flow calculation
                for (int[] offset : offsets) {
                    BlockPos cursorPos = pos.offset(offset[0], 0, offset[1]);
                    BlockPos surfacePosAtCursor = new BlockPos(cursorPos.getX(), Util.getYValueAt(cursorPos, serverLevel), cursorPos.getZ());
                    if (!normals.containsKey(surfacePosAtCursor)) {
                        calculateNormalAndFlow(surfacePosAtCursor, drawLines);
                    }
                }

                Vec3 accumulation = new Vec3(0, 0, 0);

                // Look at all neighbors and accumulate their flow if their flow intersects with this point
                for (int[] offset : offsets) {
                    BlockPos cursorPos = pos.offset(offset[0], 0, offset[1]);
                    BlockPos surfacePosAtCursor = new BlockPos(cursorPos.getX(), Util.getYValueAt(cursorPos, serverLevel), cursorPos.getZ());

                    // Calculate flow contribution from neighbor
                    Vec3 neighborFlow = flows.get(surfacePosAtCursor);
                    Vec3 toCurrent = calculateFlowDirectionToCurrent(pos, surfacePosAtCursor);
                    double flowToCurrent = neighborFlow.dot(toCurrent);

                    if (flowToCurrent > 0.1) {

                        // If the neighbor's accumulation hasn't been processed yet, process it first
                        if (!accumulations.containsKey(cursorPos)) {
                            accumulationProcessingStack.push(cursorPos);

                            ChatMessageHandler.Send("Adding required neighbor at " + offset[0] + "," + offset[1] + " to the queue", serverLevel);

                            continue;  // Process the neighbor before continuing
                        }

                        // Calculate contribution from this neighbor
                        Vec3 neighborAccumulation = accumulations.get(cursorPos);

                        // Accumulate the flow if it points towards the current position
                        accumulation = accumulation.add(neighborAccumulation);
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
        //Vec3 vec_pos = new Vec3(pos.getX(), 0d, pos.getZ());

        // Calculate smoothed normal
        Vec3 n_pos = Util.getSmoothedNormalCorrect(pos, serverLevel.getServer().overworld(), 5);

        //BlockPos surfacePosAtCursor = new BlockPos(pos.getX(), Util.getYValueAt(pos, serverLevel.getServer().overworld()), pos.getZ());

        // Calculate flow direction from the normal (projecting to the xz-plane)
        Vec3 f_pos = new Vec3(n_pos.x, 0d, n_pos.z).normalize();

        // Store normal and flow direction
        normals.put(pos, n_pos);
        flows.put(pos, f_pos);

        if (drawLines) {
            //ChatMessageHandler.Send(vec_pos.toString() + " | " + n_pos.toString(), serverLevel);

            RenderHandler.AddLineIfAbsent(
                    "n"+Util.idFromXZ(pos),
                    Util.BlockPosToVec3(pos),
                    Util.BlockPosToVec3(pos).add(n_pos),
                    0,255,0,255
            );

            RenderHandler.AddLineIfAbsent(
                    "f"+Util.idFromXZ(pos),
                    Util.BlockPosToVec3(pos),
                    Util.BlockPosToVec3(pos).add(f_pos),
                    0,0,255,255
            );
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
