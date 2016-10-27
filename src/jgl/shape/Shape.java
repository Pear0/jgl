package jgl.shape;

import jgl.math.Vec2;

/**
 * Created by william on 10/26/16.
 */
public interface Shape {

    boolean intersects(Shape other, boolean tryOther);

    default boolean intersects(Shape other) {
        return intersects(other, true);
    }

    Vec2 getCenter();

}
