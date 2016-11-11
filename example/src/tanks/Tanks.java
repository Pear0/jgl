package tanks;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import jgl.Renderer;
import jgl.gui.Window;
import jgl.math.Vec2;
import jgl.maze.Maze;
import tanks.entity.AITank;
import tanks.entity.Boundary;
import tanks.entity.Tank;
import tanks.entity.UserTank;
import tanks.packet.world.UpdateEntityPacket;
import tanks.proxy.ServerProxy;
import tanks.world.IWorld;
import tanks.world.LocalWorld;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by william on 10/26/16.
 */
public class Tanks extends Window.WindowImpl {

    private static class TankState {

        public final Vec2 position;
        public final Vec2 velocity;
        public final double rotation;
        public final double rotationalVelocity;

        public TankState(Tank tank) {
            this.position = tank.getPosition();
            this.velocity = tank.getVelocity();
            this.rotation = tank.getRotation();
            this.rotationalVelocity = tank.getRotationalVelocity();
        }

        public boolean doesEqual(TankState state) {
            return state != null &&
                    Objects.equals(this.position, state.position) &&
                    Objects.equals(this.velocity, state.velocity) &&
                    this.rotation == state.rotation &&
                    this.rotationalVelocity == state.rotationalVelocity;
        }

        public boolean doesEqual(Tank tank) {
            return doesEqual(new TankState(tank));
        }

    }

    public static boolean DRAW_BOUNDING_BOXES = false;

    private static Tanks instance;
    private static ThreadLocal<Kryo> kryoPool = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            //noinspection UnnecessaryLocalVariable
            Kryo kryo = new Kryo();
            kryo.addDefaultSerializer(UUID.class, new Serializer() {
                @Override
                public void write(Kryo kryo, Output output, Object object) {
                    UUID uuid = (UUID) object;
                    output.writeLong(uuid.getMostSignificantBits());
                    output.writeLong(uuid.getLeastSignificantBits());
                }

                @Override
                public Object read(Kryo kryo, Input input, Class type) {
                    long most = input.readLong();
                    long least = input.readLong();
                    return new UUID(most, least);
                }
            });

            return kryo;
        }
    };

    public static Tanks getInstance() {
        return instance;
    }

    public static Kryo getKryo() {
        return kryoPool.get();
    }

    private ServerProxy proxy;
    private IWorld world;
    private UserTank tank = new UserTank(getDefaultUserInterface());
    private TankState prevTankState = null;

    public Socket tryConnect(InetAddress address) throws IOException {
        Socket socket = null;
        IOException lastException = null;
        for (int i = 0; socket == null && i < 5; i++) {
            if (i > 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
            try {
                socket = new Socket(address, 56700);
            }catch (IOException e) {
                socket = null;
                lastException = e;
                System.err.println(e.getMessage());
            }
        }

        if (lastException != null) {
            throw lastException;
        }

        return socket;
    }

    public void init(String serverHostname) throws IOException {
        Socket socket = tryConnect(InetAddress.getByName(serverHostname));

        if (socket == null) {
            throw new IllegalStateException("Failed to connect to server");
        }

        proxy = new ServerProxy(socket);
        proxy.setOnNewWorldListener((world) -> {
            this.world = world;

            tank.setPosition(new Vec2(300, 400));
            tank.setRotation(0);

            this.world.registerUserTank(tank);



        });

    }

    @Override
    public void render(Renderer r, int width, int height) {
        if (world != null) {
            UserTank user = world.getEntity(UserTank.class);

            if (user != null) {
                Vec2 shift = user.getPosition().sub(new Vec2(width, height).mul(0.5));

                r.translate(shift.mul(-1));
            }

            world.render(r);
        }else {
            r.getGraphics().drawString("world is null", 50, 50);
        }
    }

    @Override
    public void tick(float d) {
        proxy.update();

        if (world != null) {
            world.tick(d);

            TankState tankState = new TankState(tank);
            if (!tankState.doesEqual(prevTankState)) {
                proxy.send(new UpdateEntityPacket(world.getUUID(), tank.getUUID(),
                        tank.getPosition(), tank.getRotation(),
                        tank.getVelocity(), tank.getRotationalVelocity()));

                prevTankState = tankState;
            }
        }
    }

    @Override
    public void onClose() {
    }

    public UserInterface getDefaultUserInterface() {
        return new UserInterface() {
            @Override
            public boolean isForward() {
                return getWindow().isKeyPressed(KeyEvent.VK_W);
            }

            @Override
            public boolean isBackward() {
                return getWindow().isKeyPressed(KeyEvent.VK_S);
            }

            @Override
            public boolean isTurnRight() {
                return getWindow().isKeyPressed(KeyEvent.VK_D);
            }

            @Override
            public boolean isTurnLeft() {
                return getWindow().isKeyPressed(KeyEvent.VK_A);
            }

            @Override
            public boolean isFiring() {
                return getWindow().isKeyPressed(KeyEvent.VK_SPACE);
            }
        };
    }

    public IWorld getWorld() {
        return world;
    }

    public static void main(String... args) throws IOException {
        boolean integratedServer = false;
        String host = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--integrated-server")) {
                integratedServer = true;
            }
            if (arg.equals("--host") && args.length - i > 1) {
                host = args[i + 1];
            }
        }

        if (integratedServer && host == null) {
            host = "localhost";
        }

        if (host == null) {
            new TanksLauncher();
            return;
        }

        if (integratedServer) {
            Thread server = new Thread(() -> {
                try {
                    TanksServer.main("--no-display");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            server.setDaemon(true);
            server.start();
        }

        instance = new Tanks();
        Window window = new Window("Tanks", 1280, 720, instance);
        instance.init(host);
        window.updateLoop();


    }

}
