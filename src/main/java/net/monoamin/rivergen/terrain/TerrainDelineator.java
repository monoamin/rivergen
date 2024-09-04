package net.monoamin.rivergen.terrain;

import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.extensions.IForgeEnchantment;
import net.monoamin.rivergen.mathutils.WeightedGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class TerrainDelineator {


    public void delineateWatershed(Vec2 startNode) {
        Stack<Vec2> processingStack = new Stack<>();
        Set<Vec2> visitedNodes = new HashSet<>();
        WeightedGraph terrainGraph = new WeightedGraph();

        processingStack.push(startNode);

        while (!processingStack.isEmpty()) {
            Vec2 currentNode = processingStack.pop();

            // If the node is already visited, skip it
            if (visitedNodes.contains(currentNode)) {
                continue;
            }

            visitedNodes.add(currentNode);

            // Identify the lowest neighboring node
            List<Vec2> lowestNeighbors = TerrainUtils.getLowestVonNeumannNeighbors(currentNode);

            if (lowestNeighbors == null) {
                // If there's no lower neighbor (local minimum), explore higher neighbors
                for (Vec2 higherNeighbor : TerrainUtils.getHighestVonNeumannNeighbors(currentNode)) {
                    if (!visitedNodes.contains(higherNeighbor)) {
                        processingStack.push(higherNeighbor);
                    }
                }
            } else {
                // Create directed edge to the lowest neighbors
                // and push them to the processing stack
                for (Vec2 neighbor : lowestNeighbors) {
                    terrainGraph.addEdge(currentNode, neighbor, 1);
                    processingStack.push(neighbor);
                }

            }

            // If the neighbor is in an unloaded chunk, load or generate it
            /*if (chunkNotLoaded(currentNode.chunk)) {
                loadChunk(currentNode.chunk);
            }

            // If the node reaches a boundary (e.g., ocean), terminate that path
            if (isBoundaryNode(currentNode)) {
                continue;
            }*/
        }
    }

}
