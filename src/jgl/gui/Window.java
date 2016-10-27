package jgl.gui;

import jgl.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

/**
 * Created by william on 10/26/16.
 */
public class Window {

    public static abstract class WindowImpl {

        private Window window;

        public void render(Renderer r) {
        }

        public void tick(float d) {
        }

        public void onClose() {
        }

        public Window getWindow() {
            return window;
        }

        public void setWindow(Window window) {
            this.window = window;
        }
    }

    private WindowImpl windowImpl;

    private JFrame frame;
    private Canvas canvas;
    private volatile boolean isClosed;
    private long lastTime = -1;

    private boolean[] isKeyPressed = new boolean[65536];
    private boolean[] lastKeyPressed = new boolean[65536];

    public Window(String title, int width, int height, WindowImpl windowImpl) {
        this.windowImpl = windowImpl;
        this.windowImpl.setWindow(this);

        frame = new JFrame(title);
        canvas = new Canvas();
        frame.add(canvas);

        canvas.setSize(width, height);
        canvas.setFocusable(false);
        frame.pack();

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isClosed = true;
                windowImpl.onClose();
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                isKeyPressed[e.getKeyCode()] = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                isKeyPressed[e.getKeyCode()] = false;
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public boolean update() {
        if (lastTime == -1) {
            lastTime = System.currentTimeMillis();
        }

        long currentTime;
        do {
            currentTime = System.currentTimeMillis();
        } while (currentTime == lastTime);

        float delta = (currentTime - lastTime) / 1000f;
        lastTime = currentTime;

        if (isClosed) return false;

        System.arraycopy(isKeyPressed, 0, lastKeyPressed, 0, isKeyPressed.length);

        windowImpl.tick(delta);

        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(2);
            bs = canvas.getBufferStrategy();
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        windowImpl.render(new Renderer(g));

        g.dispose();
        bs.show();

        return !isClosed;
    }

    public void updateLoop() {
        //noinspection StatementWithEmptyBody
        while (update()) ;
    }

    public boolean isKeyPressed(int keyCode) {
        return isKeyPressed[keyCode];
    }

    public boolean keyJustPressed(int keyCode) {
        return isKeyPressed[keyCode] && !lastKeyPressed[keyCode];
    }

}
