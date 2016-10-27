package tanks.entity;

import jgl.Renderer;
import jgl.math.Vec2;
import tanks.World;

import java.awt.*;

/**
 * Created by william on 10/26/16.
 */
public class Boundary extends Entity {

    private Vec2 size;

    public Boundary(Vec2 position, Vec2 size) {
        this.position = position;
        this.size = size;
    }

    @Override
    public void render(Renderer r) {
        super.render(r);
        r.setColor(Color.black);
        r.fillRect(0, 0, size.x, size.y);
    }

    @Override
    public void tick(World world, float d) {
    }

    @Override
    public Vec2 getSize() {
        return size;
    }

    @Override
    public void setRotation(double rotation) {
        throw new UnsupportedOperationException("Boundaries cannot be rotated");
    }
}
