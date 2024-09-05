package net.monoamin.rivergen.terrain;

import net.minecraft.world.level.ChunkPos;
import net.monoamin.rivergen.mathutils.WeightedGraph;

import java.util.HashMap;
import java.util.Map;

public class GraphContextLayer extends ContextLayer{

    private Map<ChunkPos, ChunkGraphMap> layerChunks;

    public GraphContextLayer() {
        layerChunks = new HashMap<ChunkPos, ChunkGraphMap>();
    }

    public WeightedGraph getChunk(ChunkPos chunkPos) {
        return layerChunks.get(chunkPos).get(chunkPos);
    }

    public void addChunk(ChunkPos chunkPos, WeightedGraph graphData) {
        layerChunks.get(chunkPos).add(graphData, chunkPos);
    }

    public void delChunk(ChunkPos chunkPos) {
        layerChunks.get(chunkPos).remove(chunkPos);
    }

    public boolean exists(ChunkPos chunkPos) {
        return layerChunks.containsKey(chunkPos);
    }
}
