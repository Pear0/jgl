package tanks.proxy;

import jgl.MassLogger;
import tanks.Server;
import tanks.entity.DummyTank;
import tanks.entity.Entity;
import tanks.entity.MovingEntity;
import tanks.entity.UserTank;
import tanks.packet.AbstractPacket;
import tanks.packet.world.NewEntityPacket;
import tanks.packet.world.RegisterUserTankPacket;
import tanks.packet.world.RemoveEntityPacket;
import tanks.packet.world.UpdateEntityPacket;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public class ClientProxy extends Proxy {

    private Server server;
    private UUID userTankUUID;

    public ClientProxy(Socket socket, Server server) throws IOException {
        super(socket);
        this.server = server;
    }

    @Override
    protected void handlePacket(AbstractPacket packet) {
        if (packet instanceof RegisterUserTankPacket) {
            RegisterUserTankPacket tankPacket = (RegisterUserTankPacket) packet;

            userTankUUID = tankPacket.tankUUID;

            DummyTank dummy = new DummyTank(tankPacket.tankUUID);
            server.getWorld().add(dummy);

            return;
        }

        if (packet instanceof UpdateEntityPacket) {
            UpdateEntityPacket entityPacket = (UpdateEntityPacket) packet;

            if (userTankUUID != null && userTankUUID.equals(entityPacket.entityUUID)) {
                // client can only update its own tank's position
                Entity entity = server.getWorld().getEntity(entityPacket.entityUUID);
                if (!(entity instanceof DummyTank)) {
                    throw new IllegalStateException("Client's UserTank registered UUID corresponds to non-DummyTank object");
                }

                entity.setPosition(entityPacket.position);
                entity.setRotation(entityPacket.rotation);
                if (entityPacket.updateMoving) {
                    MovingEntity movingEntity = (MovingEntity) entity;
                    movingEntity.setVelocity(entityPacket.velocity);
                    movingEntity.setRotationalVelocity(entityPacket.rotationalVelocity);
                }

                return; // normal exit
            }else {
                return; // erroneous UpdateEntityPacket
            }
        }

        if (packet instanceof NewEntityPacket) {
            NewEntityPacket entityPacket = (NewEntityPacket) packet;

            //TODO more rigorously check validity here
            server.getWorld().add(entityPacket.builder.build());
            return;
        }

        if (packet instanceof RemoveEntityPacket) {
            RemoveEntityPacket entityPacket = (RemoveEntityPacket) packet;

            //TODO some verification about client requested entity removals


            server.getWorld().remove(server.getWorld().getEntity(entityPacket.entityUUID));
            return;
        }


        super.handlePacket(packet);
    }

    @Override
    public void send(AbstractPacket packet) {
        super.send(packet);
        MassLogger.trigger("Server -> Client " + packet.getClass().getSimpleName());
    }

    public UUID getUserTankUUID() {
        return userTankUUID;
    }
}
