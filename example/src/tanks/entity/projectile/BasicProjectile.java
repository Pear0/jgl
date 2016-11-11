package tanks.entity.projectile;

import jgl.Renderer;
import jgl.math.Vec2;
import tanks.entity.Tank;
import tanks.world.IWorld;
import tanks.entity.Entity;

import java.awt.*;
import java.util.UUID;

/**
 * Created by william on 10/30/16.
 */
public class BasicProjectile extends Projectile {

    public static class Builder extends Projectile.Builder {

        private UUID shooterUUID;
        private Vec2 position;
        private double rotation;

        public Builder(UUID shooterUUID, Vec2 position, double rotation) {
            this.shooterUUID = shooterUUID;
            this.position = position;
            this.rotation = rotation;
        }

        public Builder() {
        }

        @Override
        public Projectile build() {
            return new BasicProjectile(shooterUUID, position, rotation);
        }
    }

    public BasicProjectile(UUID shooterUUID, Vec2 position, double rotation) {
        super(shooterUUID);
        setPosition(position);
        setVelocity(new Vec2(0, -200).rotate(rotation));
    }

    public BasicProjectile() {
    }

    @Override
    public void render(Renderer r) {
        super.render(r);
        r.setColor(Color.black);
        r.getGraphics().fillOval(0, 0, 10, 10);
    }

    @Override
    public Vec2 getSize() {
        return new Vec2(10, 10);
    }

}
