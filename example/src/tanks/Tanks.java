package tanks;

import jgl.Renderer;
import jgl.gui.Window;
import jgl.math.Vec2;
import tanks.entity.Boundary;
import tanks.entity.Tank;
import tanks.entity.UserTank;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by william on 10/26/16.
 */
public class Tanks extends Window.WindowImpl {

    private static Tanks instance;

    public static Tanks getInstance() {
        return instance;
    }

    private World world;

    public void init() {
        world = new World();
        world.add(new UserTank(getDefaultUserInterface()));
        world.getEntity(UserTank.class).setPosition(new Vec2(300, 400));

        world.add(new Boundary(new Vec2(100, 400), new Vec2(8, 100)));

    }

    @Override
    public void render(Renderer r) {
        r.setColor(Color.red);
        r.drawPoint(new Vec2(50, 50));

        world.render(r);

    }

    @Override
    public void tick(float d) {
        world.tick(d);
    }

    @Override
    public void onClose() {
        //System.exit(0);
    }

    public UserInterface getDefaultUserInterface() {
        return new UserInterface() {
            @Override
            public boolean isForward() {
                return getWindow().isKeyPressed(KeyEvent.VK_W);
            }

            @Override
            public boolean isBackward() {
                return getWindow().isKeyPressed(KeyEvent.VK_S);
            }

            @Override
            public boolean isTurnRight() {
                return getWindow().isKeyPressed(KeyEvent.VK_D);
            }

            @Override
            public boolean isTurnLeft() {
                return getWindow().isKeyPressed(KeyEvent.VK_A);
            }

            @Override
            public boolean isFiring() {
                return getWindow().isKeyPressed(KeyEvent.VK_SPACE);
            }
        };
    }

    public World getWorld() {
        return world;
    }

    public static void main(String... args) {
        instance = new Tanks();
        Window window = new Window("Tanks", 1280, 720, instance);
        instance.init();
        window.updateLoop();
    }

}
