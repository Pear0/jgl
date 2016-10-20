package jgl.math.interpolator;

import jgl.math.Vec2;

/**
 * Created by william on 10/19/16.
 */
public class AcceleratingInterpolator implements IInterpolator {

    private IInterpolator interpolator;
    private double acceleration = 1;
    private double deceleration = 1;

    public AcceleratingInterpolator(IInterpolator interpolator) {
        this.interpolator = interpolator;
    }
    public AcceleratingInterpolator(Vec2 start, Vec2 end) {
        this(new LinearInterpolator(start, end));
    }

    protected double transformFraction(double fraction) {
        return 1 - Math.pow(1 - Math.pow(fraction, 1 / acceleration), 1 / deceleration);
    }

    @Override
    public Vec2 interpolate(double fraction) {
        return interpolator.interpolate(transformFraction(fraction));
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public double getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(double deceleration) {
        this.deceleration = deceleration;
    }
}
