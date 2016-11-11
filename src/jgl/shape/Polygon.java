package jgl.shape;

import jgl.math.Vec2;

/**
 * Created by william on 10/26/16.
 */
public class Polygon implements Shape {

    private final Vec2[] points;
    private final Vec2 center;
    private double containingRadius = -1;

    public Polygon(Vec2 center, Vec2... points) {
        this.points = points;
        this.center = center != null ? center : Vec2.sum(points).div(points.length);
    }

    @Override
    public boolean intersects(Shape other, boolean tryOther) {
        if (other instanceof Polygon) {
            return SATIntersection.intersects(this, (Polygon) other);
        }

        if (tryOther) {
            return other.intersects(this, false);
        }

        throw new UnsupportedOperationException("Cannot find calculate intersection between " + this + " and " + other);
    }

    private boolean checkAngleShifted(double min, double angle, double max) {
        min = (min + Math.PI) % (Math.PI * 2);
        angle = (angle + Math.PI) % (Math.PI * 2);
        max = (max + Math.PI) % (Math.PI * 2);
        return min <= angle && angle <= max;
    }

    public Vec2 findClosestSideNormal(Vec2 vec) {
        boolean flip;
        {
            double a1 = points[0].sub(center).angle();
            double a2 = points[1].sub(center).angle();
            flip = a2 < a1;
        }

        for (int i = 0; i < points.length; i++) {
            int j = (i + 1) % points.length;

            double a = vec.sub(center).angle();
            double a1 = points[i].sub(center).angle();
            double a2 = points[j].sub(center).angle();

            a = (a + Math.PI * 2) % (Math.PI * 2);
            a1 = (a1 + Math.PI * 2) % (Math.PI * 2);
            a2 = (a2 + Math.PI * 2) % (Math.PI * 2);

            if (flip) {
                double a_ = a2;
                a2 = a1;
                a1 = a_;
            }

            if ((a1 <= a && a <= a2) || checkAngleShifted(a1, a, a2)) {
                return points[i].sub(points[j]).orthogonal().normalized();
            }
        }

        throw new IllegalArgumentException("Invalid vector input");

    }

    public Polygon rotate(Vec2 origin, double theta) {
        Vec2[] newPoints = new Vec2[points.length];
        for (int i = 0; i < points.length; i++) {
            newPoints[i] = points[i].rotate(origin, theta);
        }
        return new Polygon(center.rotate(origin, theta), newPoints);
    }

    public Polygon rotate(double theta) {
        return rotate(getCenter(), theta);
    }

    public Polygon translate(Vec2 vec) {
        Vec2[] newPoints = new Vec2[points.length];
        for (int i = 0; i < points.length; i++) {
            newPoints[i] = points[i].add(vec);
        }
        return new Polygon(center.add(vec), newPoints);
    }

    public double getContainingRadius() {
        if (containingRadius == -1) {
            double radius = 0;
            for (Vec2 point : points) {
                radius = Math.max(radius, point.sub(center).length());
            }
            containingRadius = radius;
        }
        return containingRadius;
    }

    public Vec2[] getPoints() {
        return points;
    }

    @Override
    public Vec2 getCenter() {
        return center;
    }
}
