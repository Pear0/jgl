package tanks.packet.world;

import tanks.entity.Entity;

import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public class EntityAddedPacket extends WorldPacket {

    public Entity entity;

    public EntityAddedPacket(UUID worldUUID, Entity entity) {
        super(worldUUID);
        this.entity = entity;
    }

    public EntityAddedPacket() {
    }

}
