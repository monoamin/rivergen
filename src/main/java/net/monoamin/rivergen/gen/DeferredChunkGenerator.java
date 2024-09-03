package net.monoamin.rivergen.gen;

import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;

public class DeferredChunkGenerator extends NoiseBasedChunkGenerator {

    public DeferredChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> noiseGeneratorSettings)
    {
        super(biomeSource, noiseGeneratorSettings);
    }

    @Override
    public void applyCarvers(WorldGenRegion pLevel, long pSeed, RandomState pRandom,BiomeManager pBiomeManager, StructureManager pStructureManager, ChunkAccess pChunk, GenerationStep.Carving pStep )
    {
        // If chunk has deferred River update, process that before.
        // TODO: Implement
        super.applyCarvers(pLevel, pSeed, pRandom, pBiomeManager, pStructureManager, pChunk, pStep);


    }

    @Override
    public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManagerm, RandomState pRandom, ChunkAccess pChunk)
    {
        super.buildSurface(pLevel, pStructureManagerm, pRandom, pChunk);
    }
}