package tanks.entity.projectile;

import jgl.math.Vec2;
import tanks.world.IWorld;
import tanks.entity.Boundary;
import tanks.entity.Entity;
import tanks.entity.MovingEntity;
import tanks.entity.Tank;

import java.util.UUID;

/**
 * Created by william on 10/26/16.
 */
public abstract class Projectile extends MovingEntity {

    public abstract static class Builder extends MovingEntity.Builder {
        @Override
        public abstract Projectile build();
    }

    private UUID shooterUUID;
    private float armedTime;

    public Projectile() {
        this.collisionMethod = CollisionMethod.BOUNCE;
    }
    public Projectile(UUID shooterUUID) {
        this();
        this.shooterUUID = shooterUUID;
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return other instanceof Boundary;
    }

    public boolean doesTrigger(Entity that) {
        return that instanceof Tank;
    }

    public void trigger(IWorld world, Entity target) {
        world.remove(this);
        // such a hack - the server trusts the client completely on the client's tank location therefore respawning
        // which should happen on the server, needs to happen on the client
        if (world.isClient() && target instanceof Tank) {
            ((Tank) target).respawn(world);
        }
    }
    public boolean isArmed() {
        return armedTime > 0.01f;
    }

    @Override
    public void tick(IWorld world, float d) {

        {
            int tolerance = 50;
            Vec2 position = getPosition();

            if (position.x < -tolerance || position.x > world.getWidth() + tolerance ||
                    position.y < -tolerance || position.y > world.getHeight() + tolerance) {
                world.remove(this);
                return;
            }
        }

        super.tick(world, d);

        if (isArmed()) {
            for (Entity e : world.getEntities()) {
                if (doesTrigger(e) && doesIntersect(e)) {
                    trigger(world, e);
                    break;
                }
            }

            if (armedTime > 10) {
                world.remove(this);
            }

            armedTime += d;
        }else {
            Entity shooter = world.getEntity(shooterUUID);
            if ((shooter != null && doesIntersect(shooter))) {
                armedTime = 0;
            }else{
                armedTime += d;
            }
        }

    }

}
