package jgl.path;

import jgl.math.Vec2;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by william on 10/21/16.
 */
public class PathUtil {

    public static void fill(Graphics2D g, Path path, double radius, double step) {
        int diameter = (int) Math.round(radius * 2);

        for (double distance = 0; distance < path.getLength(); distance += step ) {
            Vec2 current = path.interpolate(path.isNormalized() ? distance / path.getLength() : distance);
            int x = (int) Math.round(current.x - radius);
            int y = (int) Math.round(current.y - radius);
            g.fillArc(x, y, diameter, diameter, 0, 360);
        }

    }

    public static void fill(Graphics2D g, Path path, double radius) {
        fill(g, path, radius, radius * 0.01);
    }

}
