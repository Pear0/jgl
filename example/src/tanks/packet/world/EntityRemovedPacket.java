package tanks.packet.world;

import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public class EntityRemovedPacket extends WorldPacket {

    public UUID entityUUID;

    public EntityRemovedPacket(UUID worldUUID, UUID entityUUID) {
        super(worldUUID);
        this.entityUUID = entityUUID;
    }

    public EntityRemovedPacket() {
    }
}
