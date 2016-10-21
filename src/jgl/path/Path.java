package jgl.path;

import jgl.IRenderable;
import jgl.Renderer;
import jgl.math.Vec2;
import jgl.math.interpolator.IInterpolator;

/**
 * Created by william on 10/19/16.
 */
public class Path implements IInterpolator, IRenderable {

    private static double calculateInterpolatorLength(IInterpolator interpolator, double step) {
        Vec2 last = interpolator.getStart();
        double distance = 0;

        for (double fraction = step; fraction <= 1; fraction += step) {
            Vec2 current = interpolator.interpolate(fraction);
            distance += current.sub(last).length();
            last = current;
        }

        return distance;
    }

    public static Path from(IInterpolator[] interpolators, double[] lengths) {
        return new Path(interpolators, lengths);
    }

    public static Path from(IInterpolator[] interpolators, double step) {
        double[] lengths = new double[interpolators.length];
        for (int i = 0; i < interpolators.length; i++) {
            lengths[i] = calculateInterpolatorLength(interpolators[i], step);
        }
        return from(interpolators, lengths);
    }

    public static Path from(IInterpolator... interpolators) {
        return from(interpolators, 0.005);
    }

    private IInterpolator[] interpolators;
    private double[] lengths;
    private double length;
    private double[] partialSumLengths;

    private boolean isNormalized;

    private Path(IInterpolator[] interpolators, double[] lengths) {
        this.interpolators = interpolators;
        this.lengths = lengths;
        this.length = 0;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < lengths.length; i++) {
            this.length += lengths[i];
        }

        this.partialSumLengths = new double[lengths.length];
        double acc = 0;
        for (int i = 0; i < lengths.length; i++) {
            acc += lengths[i];
            this.partialSumLengths[i] = acc;
        }
    }

    private int findIndex(double distance) {
        for (int i = 0; i < interpolators.length; i++) {
            if (distance < partialSumLengths[i]) {
                return i;
            }
        }
        return interpolators.length - 1;
    }

    private double getPreviousPartialSum(int index) {
        if (index == 0) {
            return 0;
        }
        return partialSumLengths[index - 1];
    }

    @Override
    public Vec2 interpolate(double distance) {
        if (isNormalized) {
            distance = distance * length;
        }

        if (distance <= 0) {
            return interpolators[0].interpolate(0);
        }

        if (distance >= length) {
            return interpolators[interpolators.length - 1].interpolate(1);
        }

        int index = findIndex(distance);

        double myDistance = distance - getPreviousPartialSum(index);
        double myLength = lengths[index];

        return interpolators[index].interpolate(myDistance / myLength);
    }

    public void render(Renderer r, double step) {
        for (double distance = 0; distance < getLength(); distance += step) {
            Vec2 current = interpolate(isNormalized ? distance / length : distance);
            r.drawPoint(current);
        }
    }

    @Override
    public void render(Renderer r) {
        render(r, 0.1);
    }

    public boolean isNormalized() {
        return isNormalized;
    }

    public void setNormalized(boolean normalized) {
        isNormalized = normalized;
    }

    @Override
    public double getInterpolatorRange() {
        return isNormalized ? 1 : length;
    }

    public double getLength() {
        return length;
    }

}
