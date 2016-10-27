package tanks.entity;

import jgl.Renderer;
import jgl.math.Vec2;
import jgl.shape.Polygon;
import jgl.shape.Shape;
import tanks.World;

/**
 * Created by william on 10/26/16.
 */
public abstract class Entity {

    protected Vec2 position = Vec2.ZERO;
    protected double rotation = 0;

    public void render(Renderer r) {
        r.translate(position);
        r.rotate(rotation);
        r.translate(getSize().mul(-0.5));
    }

    public abstract void tick(World world, float d);

    public abstract Vec2 getSize();

    public Shape getBoundingBox() {
        Vec2 hs = getSize().mul(0.5);

        Polygon s = new Polygon(
                hs,
                new Vec2(hs.x, -hs.y),
                new Vec2(-hs.x, -hs.y),
                new Vec2(-hs.x, hs.y));

        s = s.rotate(Vec2.ZERO, rotation);
        s = s.translate(position);
        return s;
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}
