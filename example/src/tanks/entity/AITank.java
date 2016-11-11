package tanks.entity;

import tanks.world.IWorld;
import tanks.UserInterface;

/**
 * Created by william on 10/30/16.
 */
public class AITank extends UserTank {

    protected class Controls implements UserInterface {

        @Override
        public boolean isForward() {
            return buttons[0];
        }

        @Override
        public boolean isBackward() {
            return buttons[1];
        }

        @Override
        public boolean isTurnRight() {
            return buttons[2];
        }

        @Override
        public boolean isTurnLeft() {
            return buttons[3];
        }

        @Override
        public boolean isFiring() {
            return buttons[4];
        }
    }

    private boolean[] buttons = new boolean[5];
    private float sumDelta;

    public AITank() {
        super(null);
        userInterface = new Controls();
    }

    @Override
    public void tick(IWorld world, float d) {
        sumDelta += d;

        if (sumDelta >= 0.5) {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i] = Math.random() < 0.5;
            }
            sumDelta = 0;
        }

        super.tick(world, d);
    }
}
