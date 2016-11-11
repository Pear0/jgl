package tanks.entity;

import jgl.Renderer;
import jgl.math.Vec2;
import tanks.world.IWorld;

import java.awt.*;

/**
 * Created by william on 10/26/16.
 */
public class Boundary extends Entity {

    public static class Builder extends Entity.Builder {

        private Vec2 position;
        private Vec2 size;

        public Builder(Vec2 position, Vec2 size) {
            this.position = position;
            this.size = size;
        }

        public Builder() {
        }

        @Override
        public Boundary build() {
            return new Boundary(position, size);
        }
    }

    private Vec2 size;

    public Boundary(Vec2 position, Vec2 size) {
        setPosition(position);
        this.size = size;
    }

    public Boundary() {
    }

    @Override
    public void render(Renderer r) {
        super.render(r);
        r.setColor(Color.black);
        r.fillRect(0, 0, size.x, size.y);
    }

    @Override
    public void tick(IWorld world, float d) {
    }

    @Override
    public Vec2 getSize() {
        return size;
    }

    @Override
    public void setRotation(double rotation) {
        if (rotation != 0) {
            throw new UnsupportedOperationException("Boundaries cannot be rotated");
        }
    }
}
