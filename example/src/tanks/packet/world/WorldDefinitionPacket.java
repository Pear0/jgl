package tanks.packet.world;

import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public class WorldDefinitionPacket extends WorldPacket {

    public int width;
    public int height;

    public WorldDefinitionPacket(UUID worldUUID, int width, int height) {
        super(worldUUID);
        this.width = width;
        this.height = height;
    }

    public WorldDefinitionPacket() {
    }
}
