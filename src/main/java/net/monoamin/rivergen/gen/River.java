package net.monoamin.rivergen.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.monoamin.rivergen.render.RenderHandler;
import net.monoamin.rivergen.terrain.TerrainUtils;

import java.util.ArrayList;

public class River {
    //private HashMap<Vec3, Integer> riverPath;
    private final ArrayList<Vec3> riverPath;
    private int segmentCount;
    private String id;
    private final int stepRadiusMax = 128;
    private final int stepRadiusMin = 8;
    private final int maxStraightDistance = 128;
    private final double forceStepMinSlope = 0;
    private int straightSteps = 0;
    ServerLevel serverLevel;

    public boolean finalized = false;

    public River(Vec3 startCoordinate, String id, ServerLevel level)
    {
        riverPath = new ArrayList<>(); //  HashMap<>();
        riverPath.add(startCoordinate);
        segmentCount = 0;
        serverLevel = level;
    }

    public void addCoordinate(Vec3 coordinate)
    {
        riverPath.add(coordinate);
        segmentCount++;
    }

    public void doStepAuto() {
        Vec3 currentPoint = riverPath.get(segmentCount);
        Vec3 nextPoint = currentPoint;
        boolean stepped = false;
        int radius = stepRadiusMin;

        while (radius <= stepRadiusMax && !stepped && currentPoint.y > 62) {
            nextPoint = currentPoint;

            // Calculate inflow vector
            Vec3 flowBias = Vec3.ZERO;
            if (segmentCount > 1) {
                flowBias = currentPoint.subtract(riverPath.get(segmentCount - 1)).multiply(1, forceStepMinSlope, 1).normalize();
            }


            if (!stepped) {

                // Calculate terrain bias
                Vec3 terrainBias = TerrainUtils.getWeightedDirectionTowardsLowest(currentPoint, radius+3, 36);//).normalize();

                //  Calculate outflow vector
                Vec3 finalDirection = TerrainUtils.blendVec3(terrainBias, flowBias, 0.5);

                // Calculate candidate next point and actual y level
                Vec3 stepCandidate = currentPoint.add(finalDirection.multiply(radius, 0.0, radius));
                int yStepCandidate = TerrainUtils.getYValueAt(stepCandidate);

                // If terrain at candidate is lower
                if (yStepCandidate < currentPoint.y) {
                    // Then step to that height
                    nextPoint = stepCandidate.multiply(1,0,1).add(0,yStepCandidate,0);;
                    stepped = true;
                } else if (yStepCandidate >= currentPoint.y) {
                    // Else stay at the current height
                    nextPoint = stepCandidate.add(0,0,0);
                    stepped = true;
                }
            }

            // If stepped at all
            if (stepped) {
                // continue with next segment
                break;
            } else {
                // increase radius and try again
                // This is currently never reached. The river always steps even on flat terrain.
                // TODO: reimplement the maxStraightDistance mechanic again in conjunction with current search radius
                radius += stepRadiusMin;
            }
        }

        if (stepped) {
            // Add candidate point to river path and increase segment count
            riverPath.add(nextPoint);
            segmentCount++;
        } else {
            finalized = true;
            // Get inflow vector and project it out one segment
            // Compensates for trimmed ends after spline interpolation
            Vec3 lastDirection = riverPath.get(segmentCount).subtract(riverPath.get(segmentCount - 1));
            Vec3 finalPoint = nextPoint.add(lastDirection);
            riverPath.add(finalPoint);
        }
    }

    public ArrayList<Vec3> getPath()
    {
        return riverPath;
    }

    public Vec3 getCoordinateAtIndex(int index)
    {
        return riverPath.get(index);
    }

    public int length()
    {
        return segmentCount;
    }
}
