package net.monoamin.rivergen.mathutils;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class WeightedGraph {
    private final Map<Vec2, Set<Vec2>> adjacentList = new HashMap<>();
    private final Map<Vec2, Map<Vec2, Double>> weights = new HashMap<>();

    // Adds a node to the graph
    public void addNode(Vec2 node) {
        adjacentList.computeIfAbsent(node, key -> new TreeSet<>());
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
    public boolean hasEdge(Vec2 one, int two) {
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
}
