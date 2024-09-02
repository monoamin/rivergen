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
    private final int stepRadiusMin = 16;
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
        while (radius <= stepRadiusMax && !stepped && currentPoint.y > 62 && segmentCount < 100) {
            nextPoint = currentPoint;
            Vec3 directionalBias = Vec3.ZERO;
            if (segmentCount > 1) {
                directionalBias = currentPoint.subtract(riverPath.get(segmentCount - 1)).multiply(1, forceStepMinSlope, 1).normalize();
            }

            if (!stepped) {
                Vec3 flowDirection = TerrainUtils.getWeightedDirectionTowardsLowest(currentPoint, radius, 36).normalize();
                Vec3 finalDirection = TerrainUtils.blendVec3(flowDirection, directionalBias, 0.5);
                Vec3 stepCandidate = currentPoint.add(flowDirection.multiply(radius, 1.0, radius));
                int yValue = TerrainUtils.getYValueAt(stepCandidate);
                if (yValue < currentPoint.y) {
                    stepCandidate = stepCandidate.multiply(1,0,1).add(0,yValue,0);
                    nextPoint = stepCandidate;
                    stepped = true;
                    //straightSteps = 0;
                /*} else if (yValue == currentPoint.y) {
                    nextPoint = stepCandidate;
                    stepped = true;
                    straightSteps++;
                */}
            }

            // If stepped at all continue with next segment
            if (stepped) {
                break;
            } else {
                radius += stepRadiusMin;
            }
        }

        if (stepped) {
            riverPath.add(nextPoint);
            segmentCount++;
        } else {
            finalized = true;
            // TODO: Add another 2 segments as padding to keep the path after catmull-rom interpolation
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
