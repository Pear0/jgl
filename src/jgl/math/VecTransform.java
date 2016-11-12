package jgl.math;

import java.util.function.Function;

/**
 * Created by william on 11/11/16.
 */
public abstract class VecTransform implements Function<Vec2, Vec2> {

    public static VecTransform createVolatile(Function<Vec2, Vec2> predicate) {
        return new VecTransform() {
            @Override
            public Vec2 apply(Vec2 vec) {
                return predicate.apply(vec);
            }
        };
    }

}
