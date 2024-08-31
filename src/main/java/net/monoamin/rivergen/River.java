package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.CubicSpline;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Stack;

public class River {
    //private HashMap<Vec3, Integer> riverPath;
    private ArrayList<Vec3> riverPath;
    private int segmentCount;
    private String id;
    private int stepRadiusMax = 32;
    private int stepRadiusMin = 1;
    private double forceStepMinSlope = 0.5;
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

        while (radius <= stepRadiusMax && !stepped) {
            Vec3 candidatePoint = nextPoint;
            double minElevation = nextPoint.y;

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos cursorPos = new BlockPos((int) currentPoint.x + x, (int) currentPoint.y, (int) currentPoint.z + z);
                    int yLevelAtCursor = Util.getHeightFromDensity(cursorPos.getX(), cursorPos.getZ(), serverLevel.getServer().overworld());

                    if (yLevelAtCursor < minElevation) {
                        minElevation = yLevelAtCursor;
                        candidatePoint = new Vec3(currentPoint.x + x, yLevelAtCursor, currentPoint.z + z);
                        stepped = true;
                    }
                }
            }

            if (stepped) {
                nextPoint = candidatePoint;
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
        }
    }

    public void doStepAuto2() {
        Vec3 currentPoint = riverPath.get(segmentCount);
        Vec3 nextPoint = currentPoint;
        boolean stepped = false;
        int radius = stepRadiusMin;
        while (radius <= stepRadiusMax && !stepped) {
            nextPoint = currentPoint;
            BlockPos lowestCircular = Util.getLowestCircular(currentPoint, radius, 16, serverLevel.getServer().overworld());
            if (lowestCircular.getY() < currentPoint.y)
            {
                nextPoint = Util.BlockPosToVec3(lowestCircular);
                stepped = true;
            }
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
