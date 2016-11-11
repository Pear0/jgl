package tanks.entity;

import jgl.Renderer;
import jgl.math.Vec2;
import tanks.Tanks;
import tanks.world.IWorld;

import java.awt.*;

/**
 * Created by william on 10/26/16.
 */
public class Tank extends MovingEntity {

    public static class Builder extends MovingEntity.Builder {

        public Builder() {
        }

        @Override
        public Tank build() {
            return new Tank();
        }
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return other instanceof Boundary;
    }

    @Override
    public void render(Renderer r) {
        super.render(r);

        r.setColor(Color.green);
        r.fillRect(0, 0, 30, 40);

    }

    public void respawn(IWorld world) {
        double padding = 50;
        double x = padding + (world.getWidth() - 2 * padding) * Math.random();
        double y = padding + (world.getHeight() - 2 * padding) * Math.random();

        setPosition(new Vec2(x, y));
        setRotation(0);
        setVelocity(Vec2.ZERO);
        setRotationalVelocity(0);
    }

    @Override
    public Vec2 getSize() {
        return new Vec2(30, 40);
    }
}
