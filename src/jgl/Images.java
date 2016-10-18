package jgl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by william on 10/17/16.
 */
public class Images {

    public static BufferedImage loadInternal(String path) {
        URL url = Images.class.getResource(path);
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

    public static BufferedImage optimizeImage(BufferedImage original) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        BufferedImage converted = gc.createCompatibleImage(original.getWidth(), original.getHeight(), original.getTransparency());
        {
            Graphics2D g = converted.createGraphics();
            g.drawImage(original, 0, 0, original.getWidth(), original.getHeight(), null);
            g.dispose();
        }
        return converted;
    }

}
