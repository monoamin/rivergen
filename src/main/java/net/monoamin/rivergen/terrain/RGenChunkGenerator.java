package net.monoamin.rivergen.terrain;

import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.monoamin.rivergen.gen.WorldStateHandler;
import net.monoamin.rivergen.mathutils.WeightedGraph;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class RGenChunkGenerator extends NoiseBasedChunkGenerator {

    public RGenChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> noiseGeneratorSettings)
    {
        super(biomeSource, noiseGeneratorSettings);
    }

    // Method that handles the initial terrain filling before all other processing of the chunk is done
    @Override
    public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, Blender pBlender, RandomState pRandom, StructureManager pStructureManager, ChunkAccess pChunk) {
        // Fill chunk from noise
        CompletableFuture<ChunkAccess> processed = super.fillFromNoise(pExecutor, pBlender, pRandom, pStructureManager, pChunk);

        // Get raw heightmap data and add it to Context layer, then construct chunk-limited connection graph
        if (!WorldStateHandler.contextLayerManager.getLayer(ContextLayer.Types.ELEVATION).exists(pChunk.getPos())) {
            long[][] chunkHeightmap = TerrainUtils.deserializeHeightMap(pChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE).getRawData());
            WorldStateHandler.contextLayerManager.getLayer(ContextLayer.Types.ELEVATION).addChunk(pChunk.getPos(), chunkHeightmap);

            WeightedGraph chunkGraph = WeightedGraph.fromHeightmap(chunkHeightmap, pChunk.getPos());
            WorldStateHandler.contextLayerManager.getLayer(ContextLayer.Types.ELEVATION).addChunk(pChunk.getPos(), chunkGraph);
        }

        // Return Control
        return processed;
    }

    @Override
    public void applyCarvers(WorldGenRegion pLevel, long pSeed, RandomState pRandom,BiomeManager pBiomeManager, StructureManager pStructureManager, ChunkAccess pChunk, GenerationStep.Carving pStep )
    {
        /*

        */
        super.applyCarvers(pLevel, pSeed, pRandom, pBiomeManager, pStructureManager, pChunk, pStep);
    }

    @Override
    public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManagerm, RandomState pRandom, ChunkAccess pChunk)
    {
        super.buildSurface(pLevel, pStructureManagerm, pRandom, pChunk);
    }
}