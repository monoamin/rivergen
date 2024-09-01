package net.monoamin.rivergen;

import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class SplineNode {
    public double x, y, z;

    public Vec3 vec3()
    {
        return new Vec3(x,y,z);
    }

    public SplineNode(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SplineNode(Vec3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    // Helper method to add two SplineNodes
    public SplineNode add(SplineNode other) {
        return new SplineNode(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    // Helper method to subtract two SplineNodes
    public SplineNode subtract(SplineNode other) {
        return new SplineNode(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    // Helper method to multiply a SplineNode by a scalar
    public SplineNode multiply(float scalar) {
        return new SplineNode(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    // Helper method to divide a SplineNode by a scalar
    public SplineNode divide(float scalar) {
        return new SplineNode(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SplineNode that = (SplineNode) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "SplineNode{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
