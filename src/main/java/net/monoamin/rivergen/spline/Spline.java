package net.monoamin.rivergen.spline;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Spline {
    private final List<SplineNode> nodes;

    public Spline() {
        this.nodes = new ArrayList<>();
    }
    public Spline(ArrayList<Vec3> points) {
        this.nodes = new ArrayList<>();
        for (Vec3 point: points) {
            nodes.add(new SplineNode(point));
        }
    }

    public void addNode(SplineNode node) {
        nodes.add(node);
    }

    // Catmull-Rom Spline Interpolation
    public SplineNode interpolate(float t) {
        // Find the segment corresponding to t
        int numSegments = nodes.size() - 3; // Catmull-Rom requires at least 4 points to define a segment

        if (numSegments < 1) {
            throw new IllegalStateException("Spline requires at least 4 points to interpolate.");
        }

        int segment = (int) Math.floor(t * numSegments);
        float localT = t * numSegments - segment;

        // Clamp the segment index
        segment = Math.max(0, Math.min(segment, numSegments - 1));

        SplineNode P0 = nodes.get(segment);
        SplineNode P1 = nodes.get(segment + 1);
        SplineNode P2 = nodes.get(segment + 2);
        SplineNode P3 = nodes.get(segment + 3);

        // Catmull-Rom spline formula
        double t2 = localT * localT;
        double t3 = t2 * localT;

        double x = 0.5f * (2 * P1.x +
                (-P0.x + P2.x) * localT +
                (2 * P0.x - 5 * P1.x + 4 * P2.x - P3.x) * t2 +
                (-P0.x + 3 * P1.x - 3 * P2.x + P3.x) * t3);

        double y = 0.5f * (2 * P1.y +
                (-P0.y + P2.y) * localT +
                (2 * P0.y - 5 * P1.y + 4 * P2.y - P3.y) * t2 +
                (-P0.y + 3 * P1.y - 3 * P2.y + P3.y) * t3);

        double z = 0.5f * (2 * P1.z +
                (-P0.z + P2.z) * localT +
                (2 * P0.z - 5 * P1.z + 4 * P2.z - P3.z) * t2 +
                (-P0.z + 3 * P1.z - 3 * P2.z + P3.z) * t3);

        return new SplineNode(x, y, z);
    }

    // Generate points along the spline
    public ArrayList<SplineNode> generateSplinePoints(int numPoints) {
        ArrayList<SplineNode> splinePoints = new ArrayList<>();
        for (int i = 0; i <= numPoints; i++) {
            float t = (float) i / (float) numPoints;
            splinePoints.add(interpolate(t));
        }
        return splinePoints;
    }
}
