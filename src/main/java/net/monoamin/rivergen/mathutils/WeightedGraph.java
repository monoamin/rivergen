package net.monoamin.rivergen.mathutils;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec2;
import net.monoamin.rivergen.terrain.ContextLayer;
import net.monoamin.rivergen.terrain.TerrainUtils;

import java.util.*;

public class WeightedGraph {
    private final Map<Vec2, Set<Vec2>> adjacentList = new HashMap<>();
    private final Map<Vec2, Map<Vec2, Double>> weights = new HashMap<>();

    // Adds a node to the graph
    public void addNode(Vec2 node) {
        adjacentList.computeIfAbsent(node, key -> new HashSet<>());
    }

    // Adds an undirected edge between two nodes with a specific weight
    public void addEdge(Vec2 one, Vec2 two, double weight) {
        addDirectedEdge(one, two, weight);
        addDirectedEdge(two, one, weight);
    }

    // Adds a directed edge from one node to another with a specific weight
    private void addDirectedEdge(Vec2 one, Vec2 two, double weight) {
        addNode(one);
        addNode(two);
        adjacentList.get(one).add(two);
        setWeight(one, two, weight);
    }

    // Sets the weight of a directed edge between two nodes
    private void setWeight(Vec2 one, Vec2 two, double weight) {
        weights.computeIfAbsent(one, key -> new HashMap<>());
        weights.get(one).put(two, weight);
    }

    // Gets the weight of the edge between two nodes, returns null if the edge does not exist
    public Double getEdgeWeight(Vec2 one, Vec2 two) {
        if (weights.containsKey(one) && weights.get(one).containsKey(two)) {
            return weights.get(one).get(two);
        }
        return null;
    }

    // Removes a node and all its associated edges
    public void removeNode(Vec2 node) {
        // Remove all edges to this node
        for (Vec2 neighbor : adjacentList.keySet()) {
            removeEdge(neighbor, node);
        }
        // Remove the node from the graph
        adjacentList.remove(node);
        weights.remove(node);
    }

    // Removes an undirected edge between two nodes
    public void removeEdge(Vec2 one, Vec2 two) {
        removeDirectedEdge(one, two);
        removeDirectedEdge(two, one);
    }

    // Removes a directed edge from one node to another
    private void removeDirectedEdge(Vec2 one, Vec2 two) {
        if (adjacentList.containsKey(one)) {
            adjacentList.get(one).remove(two);
            if (adjacentList.get(one).isEmpty()) {
                adjacentList.remove(one);
            }
        }
        if (weights.containsKey(one)) {
            weights.get(one).remove(two);
            if (weights.get(one).isEmpty()) {
                weights.remove(one);
            }
        }
    }

    // Gets the neighbors of a given node
    public Set<Vec2> getNeighbors(Vec2 node) {
        return adjacentList.getOrDefault(node, Collections.emptySet());
    }

    // Checks if there is an edge between two nodes
    public boolean hasEdge(Vec2 one, Vec2 two) {
        return adjacentList.containsKey(one) && adjacentList.get(one).contains(two);
    }

    // Returns the nodes in the graph
    public Set<Vec2> getNodes() {
        return adjacentList.keySet();
    }

    // Returns the adjacency list representation of the graph
    public Map<Vec2, Set<Vec2>> getAdjacencyList() {
        return adjacentList;
    }

    // Returns the weights of the graph
    public Map<Vec2, Map<Vec2, Double>> getWeights() {
        return weights;
    }

    @Deprecated
    public static WeightedGraph fromChunk(ChunkAccess chunkAccess){
        WeightedGraph weightedGraph = new WeightedGraph();
        // TODO: Evaluate better alternatives
        long[] yLevels = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE).getRawData();
        long[][] gridYLevels = new long[16][16];
        for (int i = 0; i < yLevels.length; i++) {
            int relY = (int) i / 16;
            int relX = i % 16;
            gridYLevels[relX][relY] = yLevels[i];
        }
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                int ypx = 0, ynx = 0, ypz = 0, ynz = 0;
                if (x >= 15) ypx = (int)gridYLevels[x][z];
                if (x <= 0) ynx = (int)gridYLevels[x][z];
                if (z >= 15) ypz = (int)gridYLevels[x][z];
                if (z <= 0) ynz = (int)gridYLevels[x][z];

                if (x > 0 && x < 15 && z > 0 && z < 15) {
                    ypx = (int)gridYLevels[x+1][z];
                    ynx = (int)gridYLevels[x-1][z];
                    ypz = (int)gridYLevels[x][z+1];
                    ynz = (int)gridYLevels[x][z-1];
                }

                Vec2 cursor = new Vec2(x, z);
                for (Vec2 lowerNeighbor : TerrainUtils.getLowestVonNeumannNeighbors(cursor, new int[]{ypx, ynx, ypz, ynz})) {
                    weightedGraph.addEdge(cursor, lowerNeighbor, 1);
                }
            }
        }

        return new WeightedGraph();
    }

    public static WeightedGraph fromHeightmap(long[][] heightMap) {
        WeightedGraph weightedGraph = new WeightedGraph();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Get current height at (x, z)
                int yxz = (int)heightMap[x][z];

                // Determine neighbors, ensuring to stay within bounds
                int ypx = (x < 15) ? (int)heightMap[x+1][z] : yxz;
                int ynx = (x > 0) ? (int)heightMap[x-1][z] : yxz;
                int ypz = (z < 15) ? (int)heightMap[x][z+1] : yxz;
                int ynz = (z > 0) ? (int)heightMap[x][z-1] : yxz;

                // Create a vector for the current position
                Vec2 cursor = new Vec2(x, z);

                // Get the lowest Von Neumann neighbors and add them to the graph
                for (Vec2 lowerNeighbor : TerrainUtils.getLowestVonNeumannNeighbors(cursor, new int[]{ypx, ynx, ypz, ynz})) {
                    weightedGraph.addEdge(cursor, lowerNeighbor, 1);
                }
            }
        }

        return weightedGraph;
    }
}
