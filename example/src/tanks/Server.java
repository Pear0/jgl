package tanks;

import tanks.entity.Entity;
import tanks.entity.MovingEntity;
import tanks.packet.AbstractPacket;
import tanks.packet.world.EntityAddedPacket;
import tanks.packet.world.EntityRemovedPacket;
import tanks.packet.world.UpdateEntityPacket;
import tanks.packet.world.WorldDefinitionPacket;
import tanks.proxy.ClientProxy;
import tanks.world.LocalWorld;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

/**
 * Created by william on 10/31/16.
 */
public class Server {

    private UUID serverUUID = UUID.randomUUID();
    private List<ClientProxy> clients = new ArrayList<>();
    private LocalWorld world;
    private ServerSocket serverSocket;
    private Thread workerThread;
    private BlockingQueue<ClientProxy> newClients = new LinkedBlockingQueue<>();

    public Server(int port, Supplier<LocalWorld> worldSupplier) throws IOException {
        setWorld(worldSupplier.get());

        this.serverSocket = new ServerSocket(port);

        this.workerThread = new Thread(() -> {
            while (true) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    if (!"socket closed".equalsIgnoreCase(e.getMessage())) {
                        e.printStackTrace();
                    }
                    break;
                }


                try {
                    newClients.put(new ClientProxy(socket, this));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        this.workerThread.setDaemon(true);
        this.workerThread.start();
    }

    public void update() {

        {
            ClientProxy client;
            while ((client = newClients.poll()) != null) {
                client.send(new WorldDefinitionPacket(world.getUUID(), world.getWidth(), world.getHeight()));

                for (Entity e : world.getEntities()) {
                    client.send(new EntityAddedPacket(world.getUUID(), e));
                }

                clients.add(client);
            }
        }


        for(ClientProxy client : clients) {
            client.update();
        }

    }

    private void sendAll(AbstractPacket packet) {
        for (ClientProxy proxy : clients) {
            proxy.send(packet);
        }
    }

    public void setWorld(LocalWorld world) {
        if (this.world != null) {
            this.world.setListener(null);
        }
        this.world = world;
        this.world.setListener(new LocalWorld.Listener() {
            @Override
            public void onEntityAdded(Entity entity) {
                EntityAddedPacket packet = new EntityAddedPacket(world.getUUID(), entity);

                for (ClientProxy proxy : clients) {
                    if (proxy.getUserTankUUID() != null && proxy.getUserTankUUID().equals(entity.getUUID())) {
                        // we need to skip the inclusion of the client's own corresponding dummy tank
                        continue;
                    }

                    proxy.send(packet);
                }

            }

            @Override
            public void onEntityRemoved(Entity entity) {
                sendAll(new EntityRemovedPacket(world.getUUID(), entity.getUUID()));
            }

            @Override
            public void onEntityChanged(Entity entity) {
                UpdateEntityPacket packet;
                if (entity instanceof MovingEntity) {
                    MovingEntity m = (MovingEntity) entity;
                    packet = new UpdateEntityPacket(world.getUUID(), m.getUUID(), m.getPosition(), m.getRotation(), m.getVelocity(), m.getRotationalVelocity());
                }else {
                    packet = new UpdateEntityPacket(world.getUUID(), entity.getUUID(), entity.getPosition(), entity.getRotation());
                }

                for (ClientProxy proxy : clients) {
                    if (proxy.getUserTankUUID() != null && proxy.getUserTankUUID().equals(entity.getUUID())) {
                        // use client's own tank data
                        continue;
                    }

                    proxy.send(packet);
                }


            }
        });
    }

    public LocalWorld getWorld() {
        return world;
    }

    public void shutdown() {
        workerThread.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID getServerUUID() {
        return serverUUID;
    }
}
