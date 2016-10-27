package tanks.entity;

import jgl.math.Vec2;
import jgl.shape.Shape;
import tanks.World;

/**
 * Created by william on 10/26/16.
 */
public abstract class MovingEntity extends Entity {

    enum CollisionMethod {
        STOP,
        BOUNCE;
    }

    protected Vec2 velocity = Vec2.ZERO;
    protected double rotationalVelocity = 0;
    protected CollisionMethod collisionMethod = CollisionMethod.STOP;

    public boolean canCollideWith(Entity other) {
        return false;
    }

    protected boolean doesIntersect(Entity other) {
        Shape bb = getBoundingBox();
        Shape otherBB = other.getBoundingBox();
        return bb.intersects(otherBB);
    }

    protected boolean doesIntersect(World world) {
        Shape bb = getBoundingBox();

        for (Entity e : world.getEntities()) {
            if (canCollideWith(e)) {
                Shape otherBB = e.getBoundingBox();
                if (bb.intersects(otherBB)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void doTick(float d) {
        rotation += rotationalVelocity * d;
        position = position.add(velocity.rotate(rotation).mul(d));
    }

    @Override
    public void tick(World world, float d) {

        switch (collisionMethod) {
            case STOP:
                doTick(d);
                while (doesIntersect(world)) {
                    doTick(-d);
                }
                break;
            case BOUNCE:

                // TODO bounce implementation

                break;
        }


    }

    public Vec2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vec2 velocity) {
        this.velocity = velocity;
    }
}
