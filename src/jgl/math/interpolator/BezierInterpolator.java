package jgl.math.interpolator;

import jgl.math.MathUtils;
import jgl.math.Vec2;

/**
 * Created by william on 10/19/16.
 */
public class BezierInterpolator implements IInterpolator {

    private Vec2[] points;

    public BezierInterpolator(Vec2... points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("BezierInterpolator requires at least 2 points, " + points.length + " given");
        }
        this.points = points;
    }

    @Override
    public Vec2 interpolate(double fraction) {
        // hardcode linear, and quadratic implementations
        if (points.length == 2) {
            return points[0].lerp(points[1], fraction);
        } else if (points.length == 3) {
            double p0 = (1 - fraction) * (1 - fraction);
            double p1 = 2 * (1 - fraction) * fraction;
            double p2 = fraction * fraction;
            return Vec2.sum(points[0].mul(p0), points[1].mul(p1), points[2].mul(p2));
        } else {
            int n = points.length - 1;
            Vec2 acc = Vec2.ZERO;
            for (int i = 0; i <= n; i++) {
                double k = MathUtils.combination(n, i) * Math.pow(1 - fraction, n - i) * Math.pow(fraction, i);
                acc = acc.add(points[i].mul(k));
            }
            return acc;
        }
    }

    @Override
    public Vec2 getStart() {
        return points[0];
    }

    @Override
    public Vec2 getEnd() {
        return points[points.length - 1];
    }
}
