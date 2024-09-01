package net.monoamin.rivergen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGenerators;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class RivergenChunkGenerator extends NoiseBasedChunkGenerator {

    public RivergenChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> noiseGeneratorSettings)
    {
        super(biomeSource, noiseGeneratorSettings);
    }

    @Override
    public void applyCarvers(WorldGenRegion pLevel, long pSeed, RandomState pRandom,BiomeManager pBiomeManager, StructureManager pStructureManager, ChunkAccess pChunk, GenerationStep.Carving pStep )
    {
        // If chunk has deferred River update, process that before.
        super.applyCarvers(pLevel, pSeed, pRandom, pBiomeManager, pStructureManager, pChunk, pStep);


    }

    @Override
    public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManagerm, RandomState pRandom, ChunkAccess pChunk)
    {
        super.buildSurface(pLevel, pStructureManagerm, pRandom, pChunk);
    }
}