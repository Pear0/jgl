package tanks.entity;

import jgl.math.Vec2;
import jgl.shape.Polygon;
import jgl.shape.Shape;
import tanks.world.IWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by william on 10/26/16.
 */
public abstract class MovingEntity extends Entity {

    protected enum CollisionMethod {
        STOP,
        BOUNCE
    }

    public static abstract class Builder extends Entity.Builder {
        @Override
        public abstract MovingEntity build();
    }

    private Vec2 velocity = Vec2.ZERO;
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

    protected boolean doesIntersect(IWorld world) {
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

    protected Entity findIntersected(IWorld world) {
        Shape bb = getBoundingBox();

        for (Entity e : world.getEntities()) {
            if (canCollideWith(e)) {
                Shape otherBB = e.getBoundingBox();
                if (bb.intersects(otherBB)) {
                    return e;
                }
            }
        }
        return null;
    }

    protected List<Entity> findAllIntersected(IWorld world) {
        List<Entity> intersected = new ArrayList<>();
        Shape bb = getBoundingBox();
        //noinspection Convert2streamapi
        for (Entity e : world.getEntities()) {
            if (canCollideWith(e)) {
                Shape otherBB = e.getBoundingBox();
                if (bb.intersects(otherBB)) {
                    intersected.add(e);
                }
            }
        }
        return intersected;
    }

    private void doTick(float d) {
        //rotation += rotationalVelocity * d;
        setPosition(getPosition().add(velocity.mul(d)));
    }

    @Override
    public void tick(IWorld world, float d) {

        {
            List<Entity> intersectedList = findAllIntersected(world);
            if (intersectedList.size() > 0) {
                Vec2 acc = Vec2.ZERO;
                for (Entity e : intersectedList) {
                    Vec2 diff = getPosition().sub(e.getPosition());
                    diff = diff.normalized().mul(100);

                    acc = acc.add(diff);

                }
                setPosition(getPosition().add(acc.mul(d)));
            }
        }

        switch (collisionMethod) {
            case STOP:
                doTick(d);
                List<Entity> intersectedList = findAllIntersected(world);
                if (intersectedList.size() != 0) {
                    doTick(-d);

                    for (Entity intersected : intersectedList) {
                        Shape bb = intersected.getBoundingBox();
                        if (!(bb instanceof Polygon)) {
                            throw new IllegalStateException("BOUNCE only supported for Polygon shapes");
                        }

                        Vec2 sideNormal = ((Polygon) bb).findClosestSideNormal(getPosition());
                        setVelocity(getVelocity().projectedOn(sideNormal.orthogonal()));
                    }

                    doTick(d);

                    for (Entity intersected : intersectedList) {
                        if (doesIntersect(intersected)) {
                            doTick(-d);
                            break;
                        }
                    }

                }

                double prevRotation = getRotation();
                setRotation(prevRotation + rotationalVelocity * d);
                if (doesIntersect(world)) {
                    setRotation(prevRotation);
                }

                break;
            case BOUNCE:
                doTick(d);
                Entity intersected2 = findIntersected(world);
                if (intersected2 != null) {
                    //if (!isIntersecting) {
                    //    isIntersecting = true;
                        Shape bb = intersected2.getBoundingBox();
                        if (!(bb instanceof Polygon)) {
                            throw new IllegalStateException("BOUNCE only supported for Polygon shapes");
                        }

                        Vec2 sideNormal = ((Polygon) bb).findClosestSideNormal(getPosition());
                    setVelocity(getVelocity().reflectOver(sideNormal));
                    //    intersectingVelocity = velocity;

                        doTick(d);
                    }
                //}else {
                //    isIntersecting = false;
                //}
                break;
        }


    }

    public Vec2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vec2 velocity) {
        if (!Objects.equals(this.velocity, velocity)) {
            setDirty();
        }
        this.velocity = velocity;
    }

    public double getRotationalVelocity() {
        return rotationalVelocity;
    }

    public void setRotationalVelocity(double rotationalVelocity) {
        if (this.rotationalVelocity != rotationalVelocity) {
            setDirty();
        }
        this.rotationalVelocity = rotationalVelocity;
    }
}
