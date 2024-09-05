package net.monoamin.rivergen.terrain;

import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;

public class ElevationContextLayer extends ContextLayer{

    private final Map<ChunkPos, int[][]> layerChunks;

    public ElevationContextLayer() {
        layerChunks = new HashMap<>();
    }

    public int[][] getChunk(ChunkPos chunkPos) {
        return layerChunks.get(chunkPos);
    }

    public void addChunk(ChunkPos chunkPos, int[][] heightData) {
        layerChunks.put(chunkPos, heightData);
    }

    public void delChunk(ChunkPos chunkPos) {
        layerChunks.remove(chunkPos);
    }

    public boolean exists(ChunkPos chunkPos) {
        return layerChunks.containsKey(chunkPos);
    }
}
