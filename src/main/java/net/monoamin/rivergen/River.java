package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class River {
    //private HashMap<Vec3, Integer> riverPath;
    private final ArrayList<Vec3> riverPath;
    private int segmentCount;
    private String id;
    private final int stepRadiusMax = 64;
    private final int stepRadiusMin = 16;
    private final int maxStraightDistance = 64;
    private final double forceStepMinSlope = 0.5;
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

            if (segmentCount > 1) {
                // Step on even terrain for a maximum distance
                Vec3 directionalBias = currentPoint.subtract(riverPath.get(segmentCount - 1)).multiply(1, 0, 1).normalize();
                Vec3 straightCandidate;
                for (int i = 1; i <= maxStraightDistance; i++) {
                    straightCandidate = currentPoint.add(directionalBias.multiply(i, 1, i));
                    if (Util.getHeightFromDensity((int) straightCandidate.x, (int) straightCandidate.z, serverLevel.getServer().overworld()) == currentPoint.y) {
                        nextPoint = straightCandidate;
                        stepped = true;
                        break;
                    }
                }
            }

                // Otherwise step to the lowest neighbor in range
                if (!stepped) {
                    BlockPos lowestCircular = Util.getLowestCircular(currentPoint, radius, 16, serverLevel.getServer().overworld());
                    if (lowestCircular.getY() < currentPoint.y) {
                        nextPoint = Util.BlockPosToVec3(lowestCircular);
                        stepped = true;
                    }
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
