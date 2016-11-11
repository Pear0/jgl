package tanks.packet.world;

import tanks.packet.AbstractPacket;

import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public class WorldPacket extends AbstractPacket {

    public UUID worldUUID;

    public WorldPacket(UUID worldUUID) {
        this.worldUUID = worldUUID;
    }

    public WorldPacket() {
    }
}
