package net.monoamin.rivergen.terrain;

import net.minecraft.world.level.ChunkPos;
import net.monoamin.rivergen.mathutils.WeightedGraph;

import java.util.HashMap;
import java.util.Map;

public class ChunkGraphMap {
    public Map<ChunkPos, WeightedGraph> chunkWeightedGraphMap = new HashMap<>();
    public ChunkGraphMap()
    {

    }
}
