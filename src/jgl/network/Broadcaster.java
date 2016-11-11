package jgl.network;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by william on 11/2/16.
 */
public class Broadcaster {

    private static final int DEFAULT_DATAGRAM_SIZE = 512;

    public static Broadcaster bind(int port, int datagramSize, Consumer<DatagramPacket> consumer) throws SocketException {
        return new Broadcaster(port, datagramSize, consumer);
    }

    public static Broadcaster bind(int port, Consumer<DatagramPacket> consumer) throws SocketException {
        return bind(port, DEFAULT_DATAGRAM_SIZE, consumer);
    }

    private static List<InetAddress> getBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastAddresses = new ArrayList<>();

        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface ni = en.nextElement();

            List<InterfaceAddress> list = ni.getInterfaceAddresses();

            for (InterfaceAddress ia : list) {
                InetAddress broadcast = ia.getBroadcast();
                if (broadcast != null) {
                    broadcastAddresses.add(broadcast);
                }
            }
        }

        return broadcastAddresses;
    }

    private static List<InetAddress> getLoopbackAddresses() throws SocketException {
        List<InetAddress> addresses = new ArrayList<>();

        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface ni = en.nextElement();

            if (ni.isLoopback()) {
                List<InterfaceAddress> list = ni.getInterfaceAddresses();

                for (InterfaceAddress ia : list) {
                    InetAddress address = ia.getAddress();
                    if (address != null) {
                        addresses.add(address);
                    }
                }
            }
        }

        return addresses;
    }

    private DatagramSocket socket;
    private int datagramSize;
    private volatile Consumer<DatagramPacket> consumer;
    private Thread thread;
    private volatile boolean isClosed;

    Broadcaster(int port, int datagramSize, Consumer<DatagramPacket> consumer) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.datagramSize = datagramSize;
        this.socket.setBroadcast(true);
        this.consumer = consumer;

        this.thread = new Thread(() -> {
            while (!isClosed && !Thread.currentThread().isInterrupted()) {
                try {
                    byte[] data = new byte[this.datagramSize];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    this.socket.receive(packet);

                    onReceive(packet);
                } catch (IOException e) {
                    if (!isClosed) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        });
        this.thread.setDaemon(true);
        this.thread.start();

    }

    public void onReceive(DatagramPacket packet) {
        if (consumer != null) {

            try {
                for (InetAddress loopback : getLoopbackAddresses()) {
                    if (loopback.equals(packet.getAddress())) {
                        return;
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }

            consumer.accept(packet);
        }
    }

    public void broadcast(int port, byte[] data, int offset, int length, boolean ignoreMaxReceiveLength) throws IOException {
        if (!ignoreMaxReceiveLength && data.length > datagramSize) {
            throw new IllegalArgumentException("data.length is greater than max receivable size datagramSize (" + data.length + " > " + datagramSize + ")");
        }
        if (this.socket == null || isClosed) {
            throw new IllegalStateException("socket closed");
        }

        DatagramPacket packet = new DatagramPacket(data, offset, length);
        packet.setPort(port);
        for (InetAddress broadcastAddress : getBroadcastAddresses()) {
            packet.setAddress(broadcastAddress);
            this.socket.send(packet);
        }
    }

    public void broadcast(int port, byte[] data, int length) throws IOException {
        broadcast(port, data, 0, length, false);
    }

    public void broadcast(int port, byte[] data) throws IOException {
        broadcast(port, data, data.length);
    }

    public void shutdown() {
        isClosed = true;
        consumer = null;
        socket.close();
        thread.interrupt();
    }

    public boolean isClosed() {
        return isClosed;
    }

}
