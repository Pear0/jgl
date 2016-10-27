package tanks.entity;

import jgl.Renderer;
import jgl.math.Vec2;

import java.awt.*;

/**
 * Created by william on 10/26/16.
 */
public class Tank extends MovingEntity {

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

    @Override
    public Vec2 getSize() {
        return new Vec2(30, 40);
    }
}
