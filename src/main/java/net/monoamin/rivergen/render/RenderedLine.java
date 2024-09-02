package net.monoamin.rivergen.render;

import net.minecraft.world.phys.Vec3;

public class RenderedLine {
    private final Vec3 start;
    private final Vec3 end;
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    public RenderedLine(Vec3 start, Vec3 end, int red, int green, int blue, int alpha) {
        this.start = start;
        this.end = end;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Vec3 getStart() {
        return start;
    }

    public Vec3 getEnd() {
        return end;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getAlpha() {
        return alpha;
    }
}
