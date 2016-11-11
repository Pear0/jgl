package tanks.proxy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.KryoDataInput;
import com.esotericsoftware.kryo.io.Output;
import tanks.Tanks;
import tanks.packet.AbstractPacket;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by william on 10/31/16.
 */
public class Proxy {

    @SuppressWarnings("FieldCanBeLocal")
    private Socket socket;
    private Thread workerInputThread;
    private Thread workerOutputThread;
    private BlockingQueue<AbstractPacket> packetInQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<AbstractPacket> packetOutQueue = new LinkedBlockingQueue<>();


    public Proxy(Socket socket) throws IOException {
        this.socket = socket;

        this.workerInputThread = new Thread(() -> {
            try {
                Kryo kryo = Tanks.getKryo();
                Input input = new Input(socket.getInputStream());

                Object rawPacket;
                while ((rawPacket = kryo.readClassAndObject(input)) != null) {
                    if (!(rawPacket instanceof AbstractPacket)) {
                        System.err.println("Unknown packet decoded: " + rawPacket);
                        continue;
                    }
                    try {
                        packetInQueue.put((AbstractPacket) rawPacket);
                    }catch (InterruptedException e) {
                        return;
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.workerOutputThread = new Thread(() -> {
            try {
                Kryo kryo = Tanks.getKryo();
                Output output = new Output(socket.getOutputStream());

                while (true) {
                    AbstractPacket packet;
                    try {
                        packet = packetOutQueue.take();
                    } catch (InterruptedException e) {
                        return;
                    }

                    kryo.writeClassAndObject(output, packet);
                    output.flush();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.workerInputThread.setDaemon(true);
        this.workerInputThread.start();
        this.workerOutputThread.setDaemon(true);
        this.workerOutputThread.start();

    }

    protected void handlePacket(AbstractPacket packet) {
        throw new IllegalStateException("Unhandled packet: " + packet);
    }

    public void update() {

        AbstractPacket packet;
        while ((packet = packetInQueue.poll()) != null) {
            handlePacket(packet);
        }

    }

    public void send(AbstractPacket packet) {
        try {
            packetOutQueue.put(packet);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public void shutdown() {
        workerInputThread.interrupt();
        workerOutputThread.interrupt();
    }

}
