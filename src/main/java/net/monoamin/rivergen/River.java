package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Stack;

public class River {
    //private HashMap<Vec3, Integer> riverPath;
    private ArrayList<Vec3> riverPath;
    private int segmentCount;
    private String id;
    private int stepRadiusMax = 32;
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
        Vec3 nextPoint = riverPath.get(segmentCount);
        boolean stepped = false;
        for (int x = -(stepRadiusMax / 2); x < (stepRadiusMax / 2); x++) {
            for (int z = -(stepRadiusMax / 2); z < (stepRadiusMax / 2); z++) {
                BlockPos cursorPos = new BlockPos((int) nextPoint.x + x, (int) nextPoint.y, (int) nextPoint.z + z);
                int yLevelAtCursor = Util.getYValueAt(cursorPos.getX(), cursorPos.getZ(), serverLevel);
                double dotp = 1 - Util.getSmoothedNormal(cursorPos, serverLevel, 5).normalize().dot(new Vec3(0, -1, 0));
                if (yLevelAtCursor < nextPoint.y) // || dotp > 0.5)
                {
                    nextPoint = new Vec3(nextPoint.x + x, yLevelAtCursor, nextPoint.z + z);
                    stepped = true;
                }
            }
        }

        if (stepped) {
            riverPath.add(nextPoint);
            segmentCount++;
        }
        else
        {
            finalized = true;
        }
    }

    public ArrayList<Vec3> getPath()
    {
        return riverPath;
    }
}
