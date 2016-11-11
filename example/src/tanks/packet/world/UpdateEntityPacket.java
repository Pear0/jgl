package tanks.packet.world;

import jgl.math.Vec2;

import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public class UpdateEntityPacket extends WorldPacket {

    public UUID entityUUID;
    public Vec2 position;
    public double rotation;
    public boolean updateMoving;
    public Vec2 velocity;
    public double rotationalVelocity;

    public UpdateEntityPacket(UUID worldUUID, UUID entityUUID, Vec2 position, double rotation) {
        super(worldUUID);
        this.entityUUID = entityUUID;
        this.updateMoving = false;
        this.position = position;
        this.rotation = rotation;
    }

    public UpdateEntityPacket(UUID worldUUID, UUID entityUUID, Vec2 position, double rotation, Vec2 velocity, double rotationalVelocity) {
        super(worldUUID);
        this.entityUUID = entityUUID;
        this.updateMoving = true;
        this.position = position;
        this.rotation = rotation;
        this.velocity = velocity;
        this.rotationalVelocity = rotationalVelocity;
    }

    public UpdateEntityPacket() {
    }
}
