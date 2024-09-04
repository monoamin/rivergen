package net.monoamin.rivergen.gen;

import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.monoamin.rivergen.mathutils.WeightedGraph;
import net.monoamin.rivergen.terrain.TerrainUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class RivergenChunkGenerator extends NoiseBasedChunkGenerator {

    public RivergenChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> noiseGeneratorSettings)
    {
        super(biomeSource, noiseGeneratorSettings);
    }

    /*@Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, Blender pBlender, RandomState pRandom, StructureManager pStructureManager, ChunkAccess pChunk) {
        CompletableFuture<ChunkAccess> processed = super.fillFromNoise(pExecutor,pBlender,pRandom, pStructureManager,pChunk);
        return processed;
    }*/

    @Override
    public void applyCarvers(WorldGenRegion pLevel, long pSeed, RandomState pRandom,BiomeManager pBiomeManager, StructureManager pStructureManager, ChunkAccess pChunk, GenerationStep.Carving pStep )
    {
        // TODO: Implement
        WeightedGraph chunkGraph = TerrainUtils.calculateGraphForChunk(pChunk);
        RiverGenerationHandler.chunkGraphMap.chunkWeightedGraphMap.put(pChunk.getPos(), chunkGraph);
        super.applyCarvers(pLevel, pSeed, pRandom, pBiomeManager, pStructureManager, pChunk, pStep);
    }

    @Override
    public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManagerm, RandomState pRandom, ChunkAccess pChunk)
    {
        super.buildSurface(pLevel, pStructureManagerm, pRandom, pChunk);
    }
}