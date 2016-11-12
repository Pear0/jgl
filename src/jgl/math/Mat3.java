package jgl.math;

import java.util.function.Function;

/**
 * Created by william on 11/11/16.
 */
public class Mat3 implements Function<Vec2, Vec2> {

    private final double[] array;

    public Mat3(double[] array) {
        if (array.length != 9) {
            throw new IllegalArgumentException("Array must have length 9, given array of length " + array.length);
        }
        this.array = array;
    }

    public Mat3(Vec2 basisX, Vec2 basisY, Vec2 translation) {
        this(new double[]{
                basisX.x, basisY.x, translation.x,
                basisX.y, basisY.y, translation.y,
                0, 0, 1
        });
    }

    public Mat3(Vec2 basisX, Vec2 basisY) {
        this(basisX, basisY, Vec2.ZERO);
    }

    public double get(int r, int c) {
        return array[r * 3 + c];
    }

    public Mat3 mul(Mat3 that) {
        double[] out = new double[9];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                double acc = 0;
                for (int i = 0; i < 3; i++) {
                    acc += this.get(r, i) * that.get(i, c);
                }
                out[r * 3 + c] = acc;
            }
        }
        return new Mat3(out);
    }

    public Vec2 mul(Vec2 vec, double z) {
        double x = vec.x * get(0, 0) + vec.y * get(0, 1) + z * get(0, 2);
        double y = vec.x * get(1, 0) + vec.y * get(1, 1) + z * get(1, 2);
        return new Vec2(x, y);
    }

    public Vec2 mul(Vec2 vec) {
        return mul(vec, 1);
    }

    @Override
    public Vec2 apply(Vec2 vec) {
        return mul(vec);
    }

}
