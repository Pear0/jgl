package jgl.math;

import jgl.MassLogger;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by william on 10/17/16.
 */
public class Vec2 implements Serializable {

    private static class RandomHolder {
        static Random random = new Random();
    }

    public static final Vec2 ZERO = new Vec2(0, 0);
    public static final Vec2 AXIS_X = new Vec2(1, 0);
    public static final Vec2 AXIS_Y = new Vec2(0, 1);

    public static final double EPSILON = 1e-6;

    public static Vec2 sum(Vec2... vecs) {
        double x = 0;
        double y = 0;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < vecs.length; i++) {
            x += vecs[i].x;
            y += vecs[i].y;
        }
        return new Vec2(x, y);
    }

    public static Vec2 unitAngle(double radians) {
        return AXIS_X.rotate(radians);
    }

    public static Vec2 unitRandom() {
        double radians = RandomHolder.random.nextDouble() * 2 * Math.PI;
        return unitAngle(radians);
    }

    public final double x, y;

    private volatile transient double length = -1;
    private volatile transient Vec2 normalized;

    public Vec2(double x, double y) {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("component values must be finite. [" + x + ", " + y + "]");
        }
        this.x = x;
        this.y = y;
    }
    public Vec2(double a) {
        this(a, a);
    }

    public Vec2() {
        this.x = 0;
        this.y = 0;
    }

    public Vec2 add(Vec2 that) {
        if (this == ZERO) {
            return that;
        }
        if (that == ZERO) {
            return this;
        }
        return new Vec2(this.x + that.x, this.y + that.y);
    }

    public Vec2 sub(Vec2 that) {
        if (that == ZERO) {
            return this;
        }
        return new Vec2(this.x - that.x, this.y - that.y);
    }

    public Vec2 mul(double a) {
        if (a == 1) return this;
        if (a == 0) return ZERO;
        return new Vec2(this.x * a, this.y * a);
    }

    public Vec2 div(double a) {
        if (a == 1) return this;
        return new Vec2(this.x / a, this.y / a);
    }

    public double dot(Vec2 that) {
        return this.x * that.x + this.y * that.y;
    }

    public double length() {
        if (length >= 0) {
            return length;
        }
        length = Math.sqrt(x * x + y * y);
        return length;
    }

    public Vec2 normalized() {
        if (normalized != null) {
            return normalized;
        }
        double l = length();
        normalized = l == 0 ? Vec2.ZERO : this.div(l);
        return normalized;
    }

    public Vec2 projectedOn(Vec2 axis) {
        double l = axis.length();
        return axis.mul(this.dot(axis) / (l * l));
    }

    public Vec2 lerp(Vec2 that, double amt) {
        if (amt == 0) return this;
        if (amt == 1) return that;
        double dX = that.x - this.x;
        double dY = that.y - this.y;
        return new Vec2(this.x + amt * dX, this.y + amt * dY);
    }

    public Vec2 rotate(double radians) {
        if (radians == 0) {
            return this;
        }

        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        double x = this.x * cos - this.y * sin;
        double y = this.x * sin + this.y * cos;
        return new Vec2(x, y);
    }

    public Vec2 rotate(Vec2 origin, double radians) {
        return this.sub(origin).rotate(radians).add(origin);
    }

    public double angleBetween(Vec2 other) {
        return Math.acos(normalized().dot(other.normalized()));
    }

    public double angle() {
        return Math.atan2(y, x);
    }

    public double directedAngleBetween(Vec2 other) {
        return other.angle() - angle();
    }

    public Vec2 orthogonal() {
        //noinspection SuspiciousNameCombination
        return new Vec2(-y, x);
    }

    public Vec2 reflectOver(Vec2 axis) {
        Vec2 common = this.projectedOn(axis);
        return new Vec2(this.x - 2 * common.x, this.y - 2 * common.y);
    }

    @Override
    public String toString() {
        return "Vec2(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec2 vec2 = (Vec2) o;
        return Math.abs(vec2.x - x) < EPSILON && Math.abs(vec2.y - y) < EPSILON;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
