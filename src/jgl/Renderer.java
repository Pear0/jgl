package jgl;

import jgl.math.Vec2;
import jgl.shape.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by william on 10/17/16.
 */
public class Renderer {

    private Graphics2D g;
    private ArrayList<AffineTransform> matrixStack = new ArrayList<>(20);

    public Renderer(Graphics2D g) {
        if (g == null) {
            throw new IllegalArgumentException("Argument g cannot be null.");
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        this.g = g;
    }

    public Graphics2D getGraphics() {
        return g;
    }

    public int pushMatrix() {
        int index = matrixStack.size();
        matrixStack.add(g.getTransform());
        return index;
    }

    public void popMatrix() {
        popMatrix(matrixStack.size() - 1);
    }

    public void popMatrix(int index) {
        if (index >= matrixStack.size()) {
            throw new IllegalArgumentException("Invalid index: " + index + " >= " + matrixStack.size() + " (matrixStack.size())");
        }
        for (int i = matrixStack.size() - 1; i > index; i--) {
            matrixStack.remove(i);
        }
        g.setTransform(matrixStack.remove(index));
    }

    public void drawImage(BufferedImage image, double x, double y) {
        g.translate(x, y);
        g.drawImage(image, 0, 0, null);
        g.translate(-x, -y);
    }

    public void drawImage(BufferedImage image, Vec2 location) {
        drawImage(image, location.x, location.y);
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        g.translate(x1, y1);
        int x = (int) Math.round(x2 - x1);
        int y = (int) Math.round(y2 - y1);
        g.drawLine(0, 0, x, y);
        g.translate(-x1, -y1);
    }

    public void drawLine(Vec2 a, Vec2 b) {
        drawLine(a.x, a.y, b.x, b.y);
    }

    public void drawPoint(Vec2 vec) {
        drawLine(vec, vec);
    }

    public void setColor(Color color) {
        g.setColor(color);
    }

    public void translate(double x, double y) {
        g.translate(x, y);
    }

    public void translate(Vec2 v) {
        translate(v.x, v.y);
    }

    public void rotate(double theta) {
        g.rotate(theta);
    }

    public void fillRect(double x, double y, double w, double h) {
        g.translate(x, y);
        g.fillRect(0, 0, (int) (w - x), (int) (h - y));
        g.translate(-x, -y);
    }

    public void drawPolygon(jgl.shape.Polygon poly) {
        Vec2[] points = poly.getPoints();
        for (int i = 0; i < points.length; i++) {
            int j = (i + 1) % points.length;
            drawLine(points[i], points[j]);
        }
    }

}
