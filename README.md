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
TBD
