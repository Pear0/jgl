package tanks.packet.world;

import tanks.entity.Entity;

import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public class NewEntityPacket extends WorldPacket {

    public Entity.Builder builder;

    public NewEntityPacket(UUID worldUUID, Entity.Builder builder) {
        super(worldUUID);
        this.builder = builder;
    }

    public NewEntityPacket() {
    }
}
