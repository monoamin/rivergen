package net.monoamin.rivergen.terrain;

import net.minecraft.world.level.ChunkPos;
import net.monoamin.rivergen.mathutils.WeightedGraph;

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
    }

    public boolean exists(ChunkPos chunkPos) {
        return map.containsKey(chunkPos);
    }
}
