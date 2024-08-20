---

# Erosion and River Generation in Procedurally Generated Worlds

## Project Overview

This project implements a deterministic method for calculating flow accumulation and generating river paths in a procedurally generated, potentially infinite world. The core idea is to simulate natural erosion and water flow using surface normals, flow accumulation, and adaptive procedural generation.

### Features
- **Surface Normal Calculation**: Calculates smooth surface normals based on local terrain.
- **Flow Accumulation**: Determines flow accumulation by recursively processing upstream neighbors, ensuring that each position only uses the necessary context.
- **River Path Tracing**: Traces river paths based on flow accumulation and dynamically generates terrain as needed.
- **Rendering**: Visualizes flow directions and normals directly in the game world for debugging purposes.

## Methodology

### 1. **Surface Normal Calculation**

The surface normals are computed using a smoothing function that considers surrounding terrain points within a specified radius. These normals are then projected onto the xz-plane to determine the primary flow direction at each point.

```java
Vec3 n_pos = Util.getSmoothedNormal(pos, serverLevel, 2);
Vec3 f_pos = new Vec3(n_pos.x, 0d, n_pos.z).normalize();
```

![grafik](https://github.com/user-attachments/assets/a16691bc-02b6-4e63-85d1-1c4a80640fa2)


### 2. **Flow Accumulation**

Flow accumulation is computed by:
1. Starting from a specific point (e.g., the player’s position).
2. Calculating normals for nearby points as needed.
3. Recursively determining the accumulation of flow from upstream neighbors.
4. Expanding the grid to include new points as needed to accurately simulate flow.

The recursive processing ensures that only the necessary portions of the terrain are generated and processed, making the system scalable to infinite worlds.

### 3. **Adaptive Flow Processing**

To prevent infinite loops and unnecessary processing:
- **Normals are calculated on-demand**: Normals are only computed for points when required by the flow accumulation process.
- **Selective Expansion**: Neighboring cells are added to the processing stack only if their flow direction intersects with the current cell.

```java
if (!accumulations.containsKey(surfacePosAtNeighbor)) {
    accumulationProcessingStack.push(surfacePosAtNeighbor);
    continue;
}
```

### 4. **River Path Tracing**

River paths are traced by following the flow accumulation:
- Identify points of maximum accumulation.
- Trace downstream paths by connecting these points.
- Apply random perturbations to simulate natural meandering.

This method generates realistic river paths that adapt dynamically to the terrain.

### 5. **Rendering for Debugging**

The project includes a rendering system that visualizes the calculated flow directions and surface normals. This is implemented using Minecraft’s rendering engine:

```java
public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
    // Rendering logic here
}
```

The lines are drawn using the `RenderType.LINES` and are visualized based on their calculated positions in the world.

## Implementation Details

### `FluidGrid` Class

The `FluidGrid` class manages the core logic for calculating normals, flow directions, and accumulations. It uses stack-based processing to handle the recursive nature of flow accumulation without causing stack overflow errors typical of deep recursion.

### Rendering with `RenderHandler`

The `RenderHandler` class is responsible for rendering the normals and flow lines within the Minecraft world. It subscribes to Minecraft’s rendering events to draw lines representing normals and flow directions.

```java
@Mod.EventBusSubscriber(modid = "erosion")
public class RenderHandler {
    public static Map<String, Tuple<Vec3, Vec3>> solutionSet = new HashMap<>();
    
    @SubscribeEvent
    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        // Draw the lines representing normals and flow
    }
}
```

## Issues and Considerations

- **Event Handling**: Ensure the correct rendering event is used (`RenderWorldLastEvent` is recommended for general rendering tasks).
- **Performance**: Be mindful of the processing load, especially when expanding the grid dynamically in an infinite world.
- **Debugging**: Use logging to ensure that the rendering and flow accumulation processes are working as expected.

## Future Work

- **River Path Optimization**: Further optimize the path tracing and meandering logic to produce more natural-looking rivers.
- **Terrain Carving**: Implement terrain modification based on the traced river paths to simulate erosion and create river channels.
- **Integration with World Generation**: Integrate the system more tightly with the procedural world generation to create seamless, natural environments.

---
