package tanks.entity;

import jgl.math.Vec2;
import tanks.UserInterface;
import tanks.World;

/**
 * Created by william on 10/26/16.
 */
public class UserTank extends Tank {

    private UserInterface userInterface;
    private double speed = 85;
    private double rotationSpeed = 2;

    public UserTank(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    @Override
    public void tick(World world, float d) {
        rotationalVelocity = 0;
        if (userInterface.isTurnRight() && !userInterface.isTurnLeft()) {
            rotationalVelocity = rotationSpeed;
        }
        if (userInterface.isTurnLeft() && !userInterface.isTurnRight()) {
            rotationalVelocity = -rotationSpeed;
        }

        Vec2 v = new Vec2(0, -speed);

        if (userInterface.isForward()) {
            velocity = v;
        }else if (userInterface.isBackward()) {
            velocity = v.mul(-1);
        }else {
            velocity = Vec2.ZERO;
        }

        super.tick(world, d);
    }
}
