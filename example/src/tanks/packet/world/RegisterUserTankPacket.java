package tanks.packet.world;

import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public class RegisterUserTankPacket extends WorldPacket {

    public UUID tankUUID;

    public RegisterUserTankPacket(UUID worldUUID, UUID tankUUID) {
        super(worldUUID);
        this.tankUUID = tankUUID;
    }

    public RegisterUserTankPacket() {
    }

}
