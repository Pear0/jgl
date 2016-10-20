package jgl.math.interpolator;

import jgl.math.Vec2;

/**
 * Created by william on 10/19/16.
 */
public class LinearInterpolator extends BezierInterpolator {

    public LinearInterpolator(Vec2 start, Vec2 end) {
        super(new Vec2[] {start, end});
    }

}
