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

        public void render(Renderer r, int width, int height) {
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

    private String title;
    private WindowImpl windowImpl;

    private JFrame frame;
    private Canvas canvas;
    private volatile boolean isClosed;
    private long lastTime = -1;
    private long lastFpsUpdate;
    private int frameCount;
    private boolean shouldRender = true;

    private final boolean[] isKeyPressed = new boolean[65536];
    private final boolean[] lastKeyPressed = new boolean[65536];

    public Window(String title, int width, int height, WindowImpl windowImpl) {
        this.title = title;
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
                synchronized (isKeyPressed) {
                    isKeyPressed[e.getKeyCode()] = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                synchronized (isKeyPressed) {
                    isKeyPressed[e.getKeyCode()] = false;
                }
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

        if (currentTime - lastFpsUpdate > 500) {
            float fps = frameCount * 1000f / (float) (currentTime - lastFpsUpdate);
            frame.setTitle(title + " | " + Math.round(fps) + (shouldRender ? "fps" : " ticks/second"));
            frameCount = 0;
            lastFpsUpdate = currentTime;
        }

        if (isClosed) return false;

        windowImpl.tick(delta);

        synchronized (isKeyPressed) {
            System.arraycopy(isKeyPressed, 0, lastKeyPressed, 0, isKeyPressed.length);
        }

        if (shouldRender) {
            BufferStrategy bs = canvas.getBufferStrategy();
            if (bs == null) {
                canvas.createBufferStrategy(2);
                bs = canvas.getBufferStrategy();
            }

            Graphics2D g = (Graphics2D) bs.getDrawGraphics();
            g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            windowImpl.render(new Renderer(g), canvas.getWidth(), canvas.getHeight());

            g.dispose();
            if (isClosed)
                return false;
            bs.show();
        }

        frameCount++;

        return !isClosed;
    }

    public void updateLoop() {
        //noinspection StatementWithEmptyBody
        while (update()) ;
    }

    public boolean isKeyPressed(int keyCode) {
        synchronized (isKeyPressed) {
            return isKeyPressed[keyCode];
        }
    }

    public boolean keyJustPressed(int keyCode) {
        synchronized (isKeyPressed) {
            return isKeyPressed[keyCode] && !lastKeyPressed[keyCode];
        }
    }

    public boolean shouldRender() {
        return shouldRender;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }
}
