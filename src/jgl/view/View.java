package jgl.view;

import jgl.*;
import jgl.Renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by william on 10/20/16.
 */
public class View {

    private IRenderable renderable;
    private int width;
    private int height;

    private JFrame frame;

    public View(IRenderable renderable, int width, int height) {
        this.renderable = renderable;
        this.width = width;
        this.height = height;

        frame = new JFrame("View");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(width, height);
        frame.add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                long startTime = System.currentTimeMillis();
                jgl.Renderer r = new Renderer((Graphics2D) g);
                int s = r.pushMatrix();
                r.setColor(new Color(0, 0, 0, 10));
                View.this.renderable.render(r);
                r.popMatrix(s);
                int duration = (int) (System.currentTimeMillis() - startTime);
                frame.setTitle("View | Rendering took " + duration + "ms");
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public View(IRenderable renderable) {
        this(renderable, 512, 512);
    }

}
