package jgl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created by william on 10/17/16.
 */
public class ImageUtil {

    public static BufferedImage loadInternal(String path) {
        URL url = ImageUtil.class.getResource(path);
        if (url == null) {
            System.err.println("Failed to resolve internal path: " + path);
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage createOptimizedImage(int width, int height, int transparency) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        return gc.createCompatibleImage(width, height, transparency);
    }

    public static BufferedImage optimizeImage(BufferedImage original) {
        BufferedImage converted = createOptimizedImage(original.getWidth(), original.getHeight(), original.getTransparency());
        {
            Graphics2D g = converted.createGraphics();
            g.drawImage(original, 0, 0, original.getWidth(), original.getHeight(), null);
            g.dispose();
        }
        return converted;
    }

}
