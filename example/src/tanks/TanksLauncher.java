package tanks;

import com.sun.jmx.remote.security.JMXPluggableAuthenticator;
import jgl.network.Broadcaster;
import oracle.jrockit.jfr.JFR;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.RunnableFuture;

/**
 * Created by william on 11/2/16.
 */
public class TanksLauncher {

    private static class ServerEntry extends JPanel {

        private UUID serverUUID;
        private String hoster;
        private InetAddress serverAddress;
        private Runnable onClickRunnable;
        private JButton joinButton;

        public ServerEntry(UUID serverUUID, String hoster, InetAddress serverAddress) {
            super(new BorderLayout());
            this.serverUUID = serverUUID;
            this.hoster = hoster;
            this.serverAddress = serverAddress;

            JTextField hosterField = new JTextField(this.hoster + " (" + serverAddress.getHostAddress() + ")");
            hosterField.setEditable(false);
            add(hosterField, BorderLayout.CENTER);

            joinButton = new JButton("Join");
            joinButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (onClickRunnable != null) {
                        onClickRunnable.run();
                    }
                }
            });
            add(joinButton, BorderLayout.LINE_END);

            setPreferredSize(new Dimension(200, 50));
        }

        public void setOnClick(Runnable runnable) {
            onClickRunnable = runnable;
        }

        public void setButtonsEnabled(boolean enabled) {
            joinButton.setEnabled(enabled);
        }

        public UUID getServerUUID() {
            return serverUUID;
        }

        public String getHoster() {
            return hoster;
        }

        public InetAddress getServerAddress() {
            return serverAddress;
        }
    }

    private JFrame frame;
    private JPanel entriesView;
    private java.util.List<ServerEntry> entries = new ArrayList<>();

    private boolean buttonsEnabled = true;

    private Broadcaster networkBroadcaster;

    public TanksLauncher() throws SocketException {
        this.frame = new JFrame("Tanks Launcher");
        this.frame.setLayout(new BorderLayout());

        this.entriesView = new JPanel(); //new JScrollPane();
        this.entriesView.setLayout(new BoxLayout(entriesView, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(this.entriesView,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.frame.add(scrollPane, BorderLayout.CENTER);

        JPanel navView = new JPanel();
        navView.setLayout(new FlowLayout());

        JButton startServerButton = new JButton("Host Server");

        startServerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                startServerButton.setEnabled(false);

                Thread t = new Thread(() -> {
                    try {

                        System.out.println("Launching Server");
                        try {
                            TanksServer.main("--no-display");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }


                    } finally {
                        SwingUtilities.invokeLater(() -> startServerButton.setEnabled(true));
                    }
                });
                t.setDaemon(true);
                t.start();


            }
        });

        navView.add(startServerButton);

        this.frame.add(navView, BorderLayout.PAGE_START);

        this.frame.setSize(600, 300);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);

        this.networkBroadcaster = Broadcaster.bind(56701, (packet) -> SwingUtilities.invokeLater(() -> {
            TankServerBroadcast b = TankServerBroadcast.decode(packet.getData(), packet.getOffset(), packet.getLength());
            for (ServerEntry entry : entries) {
                if (entry.getServerAddress().equals(packet.getAddress())) {
                    return;
                }
            }

            System.out.println("Found: " + b.getHoster());
            ServerEntry entry = new ServerEntry(b.getServerUUID(), b.getHoster(), packet.getAddress());
            entry.setButtonsEnabled(buttonsEnabled);
            entry.setOnClick(() -> onButtonClicked(entry));

            entries.add(entry);
            entriesView.add(entry);
            entriesView.revalidate();
            entriesView.repaint();
            System.out.println(entries);
        }));
    }

    private void setButtonsEnabled(boolean enabled) {
        buttonsEnabled = enabled;
        for (ServerEntry e : entries) {
            e.setButtonsEnabled(enabled);
        }
        // a bit of a hack but what ev's
        frame.setState(enabled ? JFrame.NORMAL : JFrame.ICONIFIED);
    }

    private void removeEntry(ServerEntry entry) {
        entries.remove(entry);
        entriesView.remove(entry);
        entriesView.revalidate();
        entriesView.repaint();
    }

    private void onButtonClicked(ServerEntry entry) {
        setButtonsEnabled(false);

        Thread t = new Thread(() -> {
            try {

                try {
                    System.out.println("Launching: " + entry.getServerAddress());
                    Tanks.main("--host", entry.getServerAddress().getHostAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> removeEntry(entry));
                }

            } finally {
                SwingUtilities.invokeLater(() -> setButtonsEnabled(true));
            }
        });
        t.setDaemon(true);
        t.start();

    }


}
