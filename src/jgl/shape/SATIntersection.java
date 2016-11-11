package jgl.shape;

import jgl.math.Vec2;

import java.util.HashSet;

/**
 * Created by william on 10/26/16.
 */
public class SATIntersection {

    private static void calcMinMax(Vec2 axis, Vec2[] points, double[] out) {
        if (points.length == 0) {
            out[0] = 0;
            out[1] = 0;
            return;
        }

        double minProjection = axis.dot(points[0]);
        double maxProjection = axis.dot(points[0]);

        for (int i = 1; i < points.length; i++) {
            double dot = axis.dot(points[i]);
            if (dot < minProjection) {
                minProjection = dot;
            }
            if (dot > maxProjection) {
                maxProjection = dot;
            }
        }

        out[0] = minProjection;
        out[1] = maxProjection;
    }

    private static Vec2 optimizedSubOrthogonalNormal(Vec2 a, Vec2 b) {
        // return a.sub(b).orthogonal().normalized();
        // Allocation optimized version of the above:

        // Subtraction and component inverse reciprocal (orthogonal)
        double x = -(a.y - b.y);
        double y = a.x - b.x;

        double l = Math.sqrt(x * x + y * y);
        return new Vec2(x / l, y / l);
    }

    public static boolean intersects(Polygon a, Polygon b) {
        // Exit early
        {
            Vec2 aCenter = a.getCenter();
            Vec2 bCenter = b.getCenter();
            double dX = aCenter.x - bCenter.x;
            double dY = aCenter.y - bCenter.y;
            double length = Math.sqrt(dX * dX + dY * dY);

            if (length > a.getContainingRadius() + b.getContainingRadius()) {
                return false;
            }
        }

        HashSet<Vec2> normals = new HashSet<>();
        Vec2[] aPoints = a.getPoints();
        Vec2[] bPoints = b.getPoints();

        for (int i = 0; i < aPoints.length; i++) {
            int j = (i + 1) % aPoints.length;
            //Vec2 n = aPoints[j].sub(aPoints[i]).orthogonal().normalized();
            Vec2 n = optimizedSubOrthogonalNormal(aPoints[j], aPoints[i]);
            normals.add(n);
        }
        for (int i = 0; i < bPoints.length; i++) {
            int j = (i + 1) % bPoints.length;
            //Vec2 n = bPoints[j].sub(bPoints[i]).orthogonal().normalized();
            Vec2 n = optimizedSubOrthogonalNormal(bPoints[j], bPoints[i]);
            normals.add(n);
        }

        double[] aMinMax = new double[2];
        double[] bMinMax = new double[2];

        for (Vec2 normal : normals) {
            calcMinMax(normal, aPoints, aMinMax);
            calcMinMax(normal, bPoints, bMinMax);

            if (aMinMax[1] < bMinMax[0] || bMinMax[1] < aMinMax[0]) {
                return false;
            }
        }

        return true;
    }

    public static boolean intersects(Polygon a, Circle b) {

        Vec2 centerDistance = b.getCenter().sub(a.getCenter());
        Vec2 axis = centerDistance.normalized();

        Vec2[] aPoints = a.getPoints();

        double[] aMinMax = new double[2];
        calcMinMax(axis, aPoints, aMinMax);

        return centerDistance.length() - aMinMax[1] - b.getRadius() > 0;

    }

}
