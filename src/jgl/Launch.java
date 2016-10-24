package jgl;

import jgl.math.Vec2;
import jgl.math.interpolator.AcceleratingInterpolator;
import jgl.math.interpolator.BezierInterpolator;
import jgl.path.Path;
import jgl.path.PathUtil;
import jgl.view.View;

import java.awt.*;

/**
 * Created by william on 10/20/16.
 */
public class Launch {

    public static void main(String... args) {

        BezierInterpolator b2 = new BezierInterpolator(
                new Vec2(100, 300),
                new Vec2(300, 200),
                new Vec2(480, 500),
                new Vec2(450, 50)
        );

        AcceleratingInterpolator a2 = new AcceleratingInterpolator(b2);
        a2.setAcceleration(3);
        a2.setDeceleration(3);

        Path path = Path.from(
                new BezierInterpolator(
                        new Vec2(5, 5),
                        new Vec2(300, 200),
                        new Vec2(100, 300)
                ),
                a2
        );

        new View(r -> {
            r.getGraphics().drawString("Test", 20, 20);

            r.setColor(new Color(0, 0, 0, 1));
            PathUtil.fill(r.getGraphics(), path, 10);
        });

    }

}
