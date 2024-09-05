package net.monoamin.rivergen.terrain;

import com.mojang.datafixers.functions.PointFreeRule;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;

public abstract class ContextLayer {
    public enum Types {
        ELEVATION,
        CONNECTION_GRAPH
    }

    private Map<ChunkPos, Object> layerChunks;

    // Constructor to initialize layerObject
    public ContextLayer() {

    }

    public Object getChunk(ChunkPos chunkPos) {
        return layerChunks.get(chunkPos);
    }

    public Object addChunk(ChunkPos chunkPos, Object data) {
        return layerChunks.put(chunkPos, data);
    }

    public Object delChunk(ChunkPos chunkPos) {
        return layerChunks.remove(chunkPos);
    }

    public boolean exists(ChunkPos chunkPos) {
        return layerChunks.containsKey(chunkPos);
    }
}