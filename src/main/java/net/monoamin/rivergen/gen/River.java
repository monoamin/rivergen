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
    private final int stepRadiusMax = 64;
    private final int stepRadiusMin = 16;
    private final int maxStraightDistance = 128;
    private final double forceStepMinSlope = 0.0001;
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
                Vec3 directionalBias = currentPoint.subtract(riverPath.get(segmentCount - 1)).multiply(1, forceStepMinSlope, 1).normalize();
                Vec3 straightCandidate;

                // Step straight only if we didn't do so in the last segment
                if ( directionalBias.y > -0.1 && directionalBias.y < 0.1) {
                    for (int i = 1; i <= maxStraightDistance; i++) {
                        straightCandidate = currentPoint.add(directionalBias.multiply(i, i, i));
                        if (TerrainUtils.getYValueAt((int) straightCandidate.x, (int) straightCandidate.z) == currentPoint.y) {
                            nextPoint = straightCandidate;
                            stepped = true;
                            break;
                        }
                    }
                }
            }

                // Otherwise step to the lowest neighbor in range
                if (!stepped) {
                    BlockPos lowestCircular = TerrainUtils.getLowestCircular(currentPoint, radius, 36);
                    if (lowestCircular.getY() < currentPoint.y) {
                        nextPoint = TerrainUtils.BlockPosToVec3(lowestCircular);
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
            //RenderHandler.AddLineIfAbsent("line-"+TerrainUtils.idFromVec3(currentPoint), currentPoint, nextPoint, 50, 100, 255, 255);
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
