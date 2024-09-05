package net.monoamin.rivergen.terrain;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.monoamin.rivergen.mathutils.WeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkGraphMap {
    private Map<ChunkPos, WeightedGraph> map;
    public ChunkGraphMap()
    {
        map = new HashMap<>();
    }

    public void destroy(ChunkPos chunkPos)
    {
        map.remove(chunkPos);
    }

    public void add(ChunkPos chunkPos, WeightedGraph chunkGraph)
    {
        map.put(chunkPos,chunkGraph);

        // TODO: Add stitching of ChunkGraphs
        int xChunk = chunkPos.x;
        int zChunk = chunkPos.z;

        // Check for neighboring chunks
        ChunkPos xpChunk = new ChunkPos(xChunk+1, zChunk);
        ChunkPos xnChunk = new ChunkPos(xChunk-1, zChunk);
        ChunkPos zpChunk = new ChunkPos(xChunk, zChunk+1);
        ChunkPos znChunk = new ChunkPos(xChunk, zChunk-1);

        // Stitch with neighboring chunks if they exist
        if (map.containsKey(xpChunk)) {
            stitchChunks(chunkGraph, map.get(xpChunk), "right"); // Right border stitching
        }
        if (map.containsKey(xnChunk)) {
            stitchChunks(chunkGraph, map.get(xnChunk), "left"); // Left border stitching
        }
        if (map.containsKey(zpChunk)) {
            stitchChunks(chunkGraph, map.get(zpChunk), "top"); // Top border stitching
        }
        if (map.containsKey(znChunk)) {
            stitchChunks(chunkGraph, map.get(znChunk), "bottom"); // Bottom border stitching
        }
    }

    private void stitchChunks(WeightedGraph currentGraph, WeightedGraph neighborGraph, String direction) {
        // Determine the border nodes to connect based on the direction
        for (int i = 0; i < 16; i++) { // Assuming the chunks are 16x16
            Vec2 currentNode;
            Vec2 neighborNode;
            double weight;

            // Example for right-edge stitching
            if (direction.equals("right")) {
                currentNode = new Vec2(15, i);  // Right edge of the current chunk
                neighborNode = new Vec2(0, i);  // Left edge of the neighboring chunk
                weight = Math.abs(currentGraph.getHeight(15, i) - neighborGraph.getHeight(0, i));
            } else if (direction.equals("left")) {
                currentNode = new Vec2(0, i);  // Left edge of the current chunk
                neighborNode = new Vec2(15, i);  // Right edge of the neighboring chunk
                weight = Math.abs(currentGraph.getHeight(0, i) - neighborGraph.getHeight(15, i));
            } else if (direction.equals("top")) {
                currentNode = new Vec2(i, 15);  // Top edge of the current chunk
                neighborNode = new Vec2(i, 0);  // Bottom edge of the neighboring chunk
                weight = Math.abs(currentGraph.getHeight(i, 15) - neighborGraph.getHeight(i, 0));
            } else {
                currentNode = new Vec2(i, 0);  // Bottom edge of the current chunk
                neighborNode = new Vec2(i, 15);  // Top edge of the neighboring chunk
                weight = Math.abs(currentGraph.getHeight(i, 0) - neighborGraph.getHeight(i, 15));
            }

            // Add edges between the current chunk and the neighbor
            currentGraph.addEdge(currentNode, neighborNode, weight);
            neighborGraph.addEdge(neighborNode, currentNode, weight);
        }
    }

    private void stitchChunks(WeightedGraph currentGraph, WeightedGraph neighborGraph, String direction) {
        // Determine the border nodes to connect based on the direction
        for (int i = 0; i < 16; i++) { // Assuming the chunks are 16x16
            Vec3 currentNode;
            Vec3 neighborNode;
            double weight;

            // Example for right-edge stitching
            if (direction.equals("right")) {
                currentNode = new Vec2(15, i);  // Right edge of the current chunk
                neighborNode = new Vec2(0, i);  // Left edge of the neighboring chunk
                weight = Math.abs(currentGraph.getNodeWeight(15, i) - neighborGraph.getNodeWeight(0, i));
            } else if (direction.equals("left")) {
                currentNode = new Vec2(0, i);  // Left edge of the current chunk
                neighborNode = new Vec2(15, i);  // Right edge of the neighboring chunk
                weight = Math.abs(currentGraph.getNodeWeight(0, i) - neighborGraph.getNodeWeight(15, i));
            } else if (direction.equals("top")) {
                currentNode = new Vec2(i, 15);  // Top edge of the current chunk
                neighborNode = new Vec2(i, 0);  // Bottom edge of the neighboring chunk
                weight = Math.abs(currentGraph.getNodeWeight(i, 15) - neighborGraph.getNodeWeight(i, 0));
            } else {
                currentNode = new Vec2(i, 0);  // Bottom edge of the current chunk
                neighborNode = new Vec2(i, 15);  // Top edge of the neighboring chunk
                weight = Math.abs(currentGraph.getNodeWeight(i, 0) - neighborGraph.getNodeWeight(i, 15));
            }

            // Add edges between the current chunk and the neighbor
            currentGraph.addDirectedEdge(currentNode, neighborNode, weight);
            neighborGraph.addDirectedEdge(neighborNode, currentNode, weight);
        }
    }


    public boolean exists(ChunkPos chunkPos) {
        return map.containsKey(chunkPos);
    }
}
