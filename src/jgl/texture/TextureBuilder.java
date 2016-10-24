package jgl.texture;

import jgl.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by william on 10/21/16.
 */
public class TextureBuilder {

    private final int width;
    private final int height;

    private BufferedImage buildCache;
    private boolean isDirty;

    public TextureBuilder(int width, int height) {
        this.width = width;
        this.height = height;
        this.buildCache = ImageUtil.createOptimizedImage(width, height, Transparency.TRANSLUCENT);
        this.isDirty = true;
    }



}
