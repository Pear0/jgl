package tanks.entity;

import jgl.Renderer;
import jgl.math.Vec2;
import jgl.shape.Polygon;
import jgl.shape.Shape;
import tanks.world.IWorld;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by william on 10/26/16.
 */
public abstract class Entity {

    public static abstract class Builder {

        public abstract Entity build();

    }

    private UUID uuid = UUID.randomUUID();
    private Vec2 position = Vec2.ZERO;
    private double rotation = 0;
    private transient Polygon boundingBoxCache;
    private boolean isDirty;

    public void render(Renderer r) {
        r.translate(position);
        r.rotate(rotation);
        r.translate(getSize().mul(-0.5));
    }

    public abstract void tick(IWorld world, float d);

    /**
     *
     * @param world
     * @return whether this entity is dirty and should be synced
     */
    public boolean tickCompleted(IWorld world) {
        boolean dirty = isDirty();
        setDirty(false);
        return dirty;
    }

    public abstract Vec2 getSize();

    public Shape getBoundingBox() {
        if (boundingBoxCache == null) {
            Vec2 hs = getSize().mul(0.5);

            Polygon s = new Polygon(null,
                    hs,
                    new Vec2(hs.x, -hs.y),
                    new Vec2(-hs.x, -hs.y),
                    new Vec2(-hs.x, hs.y));

            s = s.rotate(Vec2.ZERO, rotation);
            boundingBoxCache = s.translate(position);
        }
        return boundingBoxCache;
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setPosition(Vec2 position) {
        if (!Objects.equals(this.position, position)) {
            setDirty();
            boundingBoxCache = null;
        }
        this.position = position;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        if (this.rotation != rotation) {
            setDirty();
            boundingBoxCache = null;
        }
        this.rotation = rotation;
    }

    public UUID getUUID() {
        return uuid;
    }

    protected void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    protected void setDirty() {
        setDirty(true);
    }

}
