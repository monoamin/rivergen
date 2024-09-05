package net.monoamin.rivergen.mathutils;
import net.minecraft.world.phys.Vec2;
import net.monoamin.rivergen.terrain.TerrainUtils;

import java.util.*;

public class WeightedGraph {
    private final Map<Vec2, Set<Vec2>> adjacentList = new HashMap<>();
    private final Map<Vec2, Map<Vec2, Double>> weights = new HashMap<>();
    private final Map<Vec2, Double> nodeWeights = new HashMap<>();

    // Adds a node to the graph
    public void addNode(Vec2 node, double weight ) {
        adjacentList.computeIfAbsent(node, key -> new HashSet<>());
        nodeWeights.put(node, weight);
    }

    // Adds a directed edge from one node to another with a specific weight
    public void addDirectedEdge(Vec2 one, Vec2 two, double weight) {
        //addNode(one);
        //addNode(two);
        adjacentList.get(one).add(two);
        setDirectedEdgeWeight(one, two, weight);
    }

    // Sets the weight of a directed edge between two nodes
    public void setDirectedEdgeWeight(Vec2 one, Vec2 two, double weight) {
        weights.computeIfAbsent(one, key -> new HashMap<>());
        weights.get(one).put(two, weight);
    }

    // Gets the weight of the edge between two nodes, returns null if the edge does not exist
    public Double getDirectedEdgeWeight(Vec2 one, Vec2 two) {
        if (weights.containsKey(one) && weights.get(one).containsKey(two)) {
            return weights.get(one).get(two);
        }
        return null;
    }

    // Removes a node and all its associated edges
    public void removeNode(Vec2 node) {
        // Remove all edges to this node
        for (Vec2 neighbor : adjacentList.keySet()) {
            removeDirectedEdge(neighbor, node);
        }
        // Remove the node from the graph
        adjacentList.remove(node);
        weights.remove(node);
        nodeWeights.remove(node);
    }

    // Removes a directed edge from one node to another
    public void removeDirectedEdge(Vec2 one, Vec2 two) {
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
    public Set<Vec2> getAllNodes() {
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

    public static WeightedGraph fromHeightmap(long[][] heightMap) {
        WeightedGraph weightedGraph = new WeightedGraph();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Get current height at (x, z)
                int yxz = (int)heightMap[x][z];

                // Determine neighbors, ensuring to stay within bounds
                double px = (x < 15) ? heightMap[x+1][z] : yxz;
                double nx = (x > 0)  ? heightMap[x-1][z] : yxz;
                double pz = (z < 15) ? heightMap[x][z+1] : yxz;
                double nz = (z > 0)  ? heightMap[x][z-1] : yxz;

                // Create a vector for the current position
                Vec2 sourceNode = new Vec2(x, z);

                // Get the lowest Von Neumann neighbors and add them to the graph
                for (Vec2 lowerNeighbor : TerrainUtils.getLowestVonNeumannNeighbors(sourceNode, List.of(px,nx,pz,nz))) {
                    int yDiff = Math.abs((int)sourceNode.y - (int)lowerNeighbor.y);
                    weightedGraph.addDirectedEdge(sourceNode, lowerNeighbor, yDiff);
                }
            }
        }

        return weightedGraph;
    }

    public double getNodeWeight (Vec2 node) {
        return nodeWeights.getOrDefault(node, 0d);
    }

    public void setNodeWeight (Vec2 node, double weight) {
        nodeWeights.replace(node, weight);
    }
}
