package net.monoamin.rivergen.terrain;

import net.minecraft.world.level.ChunkPos;
import net.monoamin.rivergen.mathutils.WeightedGraph;

import java.util.HashMap;
import java.util.Map;

public class GraphContextLayer extends ContextLayer{

    private final Map<ChunkPos, WeightedGraph> layerChunks;

    public GraphContextLayer() {
        layerChunks = new HashMap<>();
    }

    public WeightedGraph getChunk(ChunkPos chunkPos) {
        return layerChunks.get(chunkPos);
    }

    public WeightedGraph addChunk(ChunkPos chunkPos, WeightedGraph graphData) {
        return layerChunks.put(chunkPos, graphData);
    }

    public WeightedGraph delChunk(ChunkPos chunkPos) {
        return layerChunks.remove(chunkPos);
    }

    public boolean exists(ChunkPos chunkPos) {
        return layerChunks.containsKey(chunkPos);
    }
}
