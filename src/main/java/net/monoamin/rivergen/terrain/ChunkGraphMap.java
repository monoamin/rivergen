package net.monoamin.rivergen.terrain;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.monoamin.rivergen.mathutils.WeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkGraphMap {
    private final Map<ChunkPos, WeightedGraph> map;
    public ChunkGraphMap()
    {
        map = new HashMap<>();
    }

    public void destroy(ChunkPos chunkPos)
    {
        map.remove(chunkPos);
    }

    public void add(WeightedGraph chunkGraph, ChunkPos chunkPos)
    {
        map.put(chunkPos,chunkGraph);

        int xChunk = chunkPos.x;
        int zChunk = chunkPos.z;

        // Check for neighboring chunks
        ChunkPos xpChunk = new ChunkPos(xChunk+1, zChunk);
        ChunkPos xnChunk = new ChunkPos(xChunk-1, zChunk);
        ChunkPos zpChunk = new ChunkPos(xChunk, zChunk+1);
        ChunkPos znChunk = new ChunkPos(xChunk, zChunk-1);

        // Stitch with neighboring chunks if they exist
        if (map.containsKey(xpChunk)) {
            stitchChunks(chunkGraph, chunkPos, map.get(xpChunk), "right"); // Right border stitching
        }
        if (map.containsKey(xnChunk)) {
            stitchChunks(chunkGraph, chunkPos, map.get(xnChunk), "left"); // Left border stitching
        }
        if (map.containsKey(zpChunk)) {
            stitchChunks(chunkGraph, chunkPos, map.get(zpChunk), "top"); // Top border stitching
        }
        if (map.containsKey(znChunk)) {
            stitchChunks(chunkGraph, chunkPos, map.get(znChunk), "bottom"); // Bottom border stitching
        }
    }

    private void stitchChunks(WeightedGraph currentGraph, ChunkPos sourceChunk, WeightedGraph neighborGraph, String direction) {
        // Determine the border nodes to connect based on the direction
        for (int i = 0; i < 16; i++) { // Assuming the chunks are 16x16
            Vec2 sourceNode;
            Vec2 neighborNode;
            double edgeWeight;

            ChunkPos targetChunk;

            // Example for right-edge stitching
            if (direction.equals("right")) {
                sourceNode = new Vec2(15, i);  // Right edge of the current chunk
                neighborNode = new Vec2(0, i);  // Left edge of the neighboring chunk
                targetChunk = new ChunkPos(sourceChunk.x+1, sourceChunk.z);
                edgeWeight = Math.abs(currentGraph.getNodeWeight(TerrainUtils.getAbsoluteBlockPos(sourceChunk, new Vec2(15, i))) - neighborGraph.getNodeWeight(TerrainUtils.getAbsoluteBlockPos(targetChunk, new Vec2(0, i))));
            } else if (direction.equals("left")) {
                sourceNode = new Vec2(0, i);  // Left edge of the current chunk
                neighborNode = new Vec2(15, i);  // Right edge of the neighboring chunk
                targetChunk = new ChunkPos(sourceChunk.x-1, sourceChunk.z);
                edgeWeight = Math.abs(currentGraph.getNodeWeight(TerrainUtils.getAbsoluteBlockPos(sourceChunk, new Vec2(0, i))) - neighborGraph.getNodeWeight(TerrainUtils.getAbsoluteBlockPos(targetChunk, new Vec2(15, i))));
            } else if (direction.equals("top")) {
                sourceNode = new Vec2(i, 15);  // Top edge of the current chunk
                neighborNode = new Vec2(i, 0);  // Bottom edge of the neighboring chunk
                targetChunk = new ChunkPos(sourceChunk.x, sourceChunk.z+1);
                edgeWeight = Math.abs(currentGraph.getNodeWeight(TerrainUtils.getAbsoluteBlockPos(sourceChunk, new Vec2(i, 15))) - neighborGraph.getNodeWeight(TerrainUtils.getAbsoluteBlockPos(targetChunk, new Vec2(i, 0))));
            } else {
                sourceNode = new Vec2(i, 0);  // Bottom edge of the current chunk
                neighborNode = new Vec2(i, 15);  // Top edge of the neighboring chunk
                targetChunk = new ChunkPos(sourceChunk.x, sourceChunk.z-1);
                edgeWeight = Math.abs(currentGraph.getNodeWeight(TerrainUtils.getAbsoluteBlockPos(sourceChunk, new Vec2(i, 0))) - neighborGraph.getNodeWeight(TerrainUtils.getAbsoluteBlockPos(targetChunk, new Vec2(i, 15))));
            }

            Vec2 absCurrentNode = TerrainUtils.getAbsoluteBlockPos(sourceChunk, sourceNode);
            Vec2 absNeighborNode = TerrainUtils.getAbsoluteBlockPos(targetChunk, neighborNode);

            // Add edges between the current chunk and the neighbor
            currentGraph.addDirectedEdge(absCurrentNode, absNeighborNode, edgeWeight);
            neighborGraph.addDirectedEdge(absCurrentNode, absNeighborNode, edgeWeight);
        }
    }

    public boolean exists(ChunkPos chunkPos) {
        return map.containsKey(chunkPos);
    }

    public WeightedGraph get(ChunkPos chunkPos) {
        return map.get(chunkPos);
    }

    public void remove(ChunkPos chunkPos) {
        map.remove(chunkPos);
    }
}
