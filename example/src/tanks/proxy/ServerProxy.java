package tanks.proxy;

import jgl.MassLogger;
import tanks.world.ClientWorld;
import tanks.packet.AbstractPacket;
import tanks.packet.world.WorldDefinitionPacket;
import tanks.packet.world.WorldPacket;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by william on 10/31/16.
 */
public class ServerProxy extends Proxy {

    private Map<UUID, ClientWorld> clientWorlds = new HashMap<>();
    private Consumer<ClientWorld> newWorldListener;

    public ServerProxy(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    protected void handlePacket(AbstractPacket rawPacket) {
        if (rawPacket instanceof WorldDefinitionPacket) {
            WorldDefinitionPacket packet = (WorldDefinitionPacket) rawPacket;

            ClientWorld world = new ClientWorld(this, packet.worldUUID, packet.width, packet.height);
            clientWorlds.put(packet.worldUUID, world);

            if (newWorldListener != null) {
                newWorldListener.accept(world);
            }
        }else if (rawPacket instanceof WorldPacket) {
            WorldPacket packet = (WorldPacket) rawPacket;

            ClientWorld world = clientWorlds.get(packet.worldUUID);
            if (world == null) {
                System.err.println("Received packet: " + packet + ", for unknown world: " + packet.worldUUID);
                return;
            }

            world.handlePacket(packet);
        } else {
            super.handlePacket(rawPacket);
        }
    }

    @Override
    public void send(AbstractPacket packet) {
        super.send(packet);
        MassLogger.trigger("Client -> Server packet");
    }

    public void setOnNewWorldListener(Consumer<ClientWorld> listener) {
        newWorldListener = listener;
    }

}
