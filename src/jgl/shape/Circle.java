package jgl.shape;

import jgl.math.Vec2;

/**
 * Created by william on 10/26/16.
 */
public class Circle implements Shape {

    private Vec2 center;
    private double radius;

    public Circle(Vec2 center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public boolean intersects(Shape other, boolean tryOther) {
        if (other instanceof Polygon) {
            return SATIntersection.intersects((Polygon) other, this);
        }else if (other instanceof Circle) {
            Circle that = (Circle) other;
            return that.getCenter().sub(getCenter()).length() - getRadius() - that.getRadius() > 0;
        }

        if (tryOther) {
            return other.intersects(this, false);
        }

        throw new UnsupportedOperationException("Cannot find calculate intersection between " + this + " and " + other);
    }

    @Override
    public Vec2 getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }
}
