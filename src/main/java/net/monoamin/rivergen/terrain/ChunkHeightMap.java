package net.monoamin.rivergen.terrain;

import net.minecraft.world.level.ChunkPos;
import net.monoamin.rivergen.mathutils.WeightedGraph;

import java.util.HashMap;
import java.util.Map;

public class ChunkHeightMap {
    private Map<ChunkPos, long[][]> map;
    public ChunkHeightMap()
    {
        map = new HashMap<>();
    }

    public void destroy(ChunkPos chunkPos)
    {
        map.remove(chunkPos);
    }

    public void add(ChunkPos chunkPos, long[][] heights)
    {
        map.put(chunkPos,heights);
    }

    public boolean exists(ChunkPos chunkPos) {
        return map.containsKey(chunkPos);
    }
}
