package jgl.math.interpolator;

import jgl.math.Vec2;

/**
 * Created by william on 10/19/16.
 */
public interface IInterpolator {

    Vec2 interpolate(double fraction);

    default Vec2 getStart() {
        return interpolate(0);
    }

    default Vec2 getEnd() {
        return interpolate(1);
    }

}
