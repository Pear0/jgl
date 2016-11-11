package tanks;

import jgl.Renderer;
import jgl.gui.Window;
import jgl.network.Broadcaster;
import tanks.world.LocalWorld;

import java.io.IOException;

/**
 * Created by william on 10/31/16.
 */
public class TanksServer extends Window.WindowImpl {

    private static boolean disableServerWindow = false;

    public Server server;
    public volatile Broadcaster networkBroadcaster;
    private Thread networkBroadcasterThread;

    public void init() throws IOException {

        server = new Server(56700, () -> {
            LocalWorld world = new LocalWorld(960, 960);
            TanksUtil.fillMaze(world, 0, 0, 120, 120, 8, 8, 6);
            return world;
        });

        networkBroadcaster = Broadcaster.bind(/* this port doesn't matter because we only send */ 56748, (packet) -> {});
        networkBroadcasterThread = new Thread(() -> {
            while (networkBroadcaster != null && !networkBroadcaster.isClosed()) {

                TankServerBroadcast b = new TankServerBroadcast(server.getServerUUID(), System.getProperty("user.name"));

                try {
                    networkBroadcaster.broadcast(56701, TankServerBroadcast.encode(b));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        });
        networkBroadcasterThread.setDaemon(true);
        networkBroadcasterThread.start();

    }

    @Override
    public void render(Renderer r, int width, int height) {
        if (!disableServerWindow) {
            server.getWorld().render(r);
        }
    }

    @Override
    public void tick(float d) {
        server.update();
        server.getWorld().tick(d);
    }

    public void shutdown() {
        server.shutdown();
        networkBroadcaster.shutdown();
        networkBroadcaster = null;
    }

    public static void main(String... args) throws IOException {

        for (String arg : args) {
            if (arg.equals("--no-display")) {
                disableServerWindow = true;
            }
        }

        TanksServer instance = null;
        try {
            instance = new TanksServer();
            Window window = new Window("Tanks Server", disableServerWindow ? 400 : 1280, disableServerWindow ? 100 : 720, instance);
            window.setShouldRender(false);
            instance.init();
            window.updateLoop();
        }finally {
            if (instance != null) {
                instance.shutdown();
            }
        }
    }

}
