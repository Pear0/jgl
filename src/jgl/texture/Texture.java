package jgl.texture;

import java.awt.image.BufferedImage;

/**
 * Created by william on 10/21/16.
 */
public class Texture {

    private final BufferedImage image;

    public Texture(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

}
