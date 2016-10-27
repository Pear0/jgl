package tanks.entity;

import tanks.World;

/**
 * Created by william on 10/26/16.
 */
public abstract class Projectile extends MovingEntity {

    private Entity shooter;
    private boolean isArmed;

    public Projectile(Entity shooter) {
        this.shooter = shooter;
    }

    public boolean doesTrigger(Entity that) {
        return that instanceof Tank;
    }

    public abstract void trigger(World world, Entity target);

    @Override
    public void tick(World world, float d) {


        super.tick(world, d);

        if (isArmed) {
            for (Entity e : world.getEntities()) {
                if (doesTrigger(e)) {
                    trigger(world, e);
                    break;
                }
            }
        }

        if (!isArmed && (shooter == null || !doesIntersect(shooter))) {
            isArmed = true;
        }

    }
}
