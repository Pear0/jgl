package jgl.shape;

import jgl.math.Vec2;

/**
 * Created by william on 10/26/16.
 */
public class Polygon implements Shape {

    private final Vec2[] points;
    private final Vec2 center;

    public Polygon(Vec2... points) {
        this.points = points;
        this.center = Vec2.sum(points).div(points.length);
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

    public Polygon rotate(Vec2 origin, double theta) {
        Vec2[] newPoints = new Vec2[points.length];
        for (int i = 0; i < points.length; i++) {
            newPoints[i] = points[i].rotate(origin, theta);
        }
        return new Polygon(newPoints);
    }

    public Polygon rotate(double theta) {
        return rotate(getCenter(), theta);
    }

    public Polygon translate(Vec2 vec) {
        Vec2[] newPoints = new Vec2[points.length];
        for (int i = 0; i < points.length; i++) {
            newPoints[i] = points[i].add(vec);
        }
        return new Polygon(newPoints);
    }

    public Vec2[] getPoints() {
        return points;
    }

    @Override
    public Vec2 getCenter() {
        return center;
    }
}
