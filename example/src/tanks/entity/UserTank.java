package tanks.entity;

import jgl.math.Vec2;
import tanks.world.IWorld;
import tanks.UserInterface;
import tanks.entity.projectile.BasicProjectile;

/**
 * Created by william on 10/26/16.
 */
public class UserTank extends Tank {

    public static class Builder extends Tank.Builder {
        @Override
        public Tank build() {
            throw new UnsupportedOperationException("Builder not supported for UserTank");
        }
    }

    protected UserInterface userInterface;
    protected double speed = 85;
    protected double rotationSpeed = 2;
    protected long shootTimeout;

    public UserTank(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    @Override
    public void tick(IWorld world, float d) {
        rotationalVelocity = 0;
        if (userInterface.isTurnRight() && !userInterface.isTurnLeft()) {
            rotationalVelocity = rotationSpeed;
        }
        if (userInterface.isTurnLeft() && !userInterface.isTurnRight()) {
            rotationalVelocity = -rotationSpeed;
        }

        Vec2 v = new Vec2(0, -speed).rotate(getRotation());

        if (userInterface.isForward()) {
            setVelocity(v);
        }else if (userInterface.isBackward()) {
            setVelocity(v.mul(-1));
        }else {
            setVelocity(Vec2.ZERO);
        }

        super.tick(world, d);

        long time = System.currentTimeMillis();
        if (time > shootTimeout && userInterface.isFiring()) {
            shootTimeout = time + 1000;

            world.add(new BasicProjectile.Builder(getUUID(), getPosition(), getRotation()));
        }

    }
}
