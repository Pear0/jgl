package tanks.packet.world;

import tanks.entity.Entity;

import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public class RemoveEntityPacket extends WorldPacket {

    public UUID entityUUID;

    public RemoveEntityPacket(UUID worldUUID, UUID entityUUID) {
        super(worldUUID);
        this.entityUUID = entityUUID;
    }

    public RemoveEntityPacket() {
    }
}