package tanks.world;

import jgl.Renderer;
import tanks.Tanks;
import tanks.entity.Entity;
import tanks.entity.UserTank;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public interface IWorld {

    default void render(Renderer r) {
        for (Entity e : getEntities()) {
            int l = r.pushMatrix();
            e.render(r);
            r.popMatrix(l);
            r.setColor(Color.red);

            if (Tanks.DRAW_BOUNDING_BOXES) {
                jgl.shape.Shape bb = e.getBoundingBox();
                if (bb instanceof jgl.shape.Polygon) {
                    r.drawPolygon((jgl.shape.Polygon) bb);
                }
            }
        }
    }

    default void tick(float d) {
        for (Entity e : getEntities()) {
            e.tick(this, d);
        }
        for (Entity e : getEntities()) {
            e.tickCompleted(this);
        }
    }

    void add(Entity.Builder builder);

    void registerUserTank(UserTank tank);

    boolean remove(Entity e);

    List<Entity> getEntities();

    default <T extends Entity> List<T> getEntities(Class<T> clazz) {
        ArrayList<T> selected = new ArrayList<>();
        //noinspection Convert2streamapi
        for (Entity e : getEntities()) {
            if (clazz.isInstance(e)) {
                //noinspection unchecked
                selected.add((T) e);
            }
        }
        return selected;
    }

    default <T extends Entity> T getEntity(Class<T> clazz) {
        for (Entity e : getEntities()) {
            if (clazz.isInstance(e)) {
                //noinspection unchecked
                return (T) e;
            }
        }
        return null;
    }

    default Entity getEntity(UUID uuid) {
        for (Entity e : getEntities()) {
            if (uuid.equals(e.getUUID())) {
                return e;
            }
        }
        return null;
    }

    int getWidth();

    int getHeight();

    boolean isServer();

    default boolean isClient() {
        return !isServer();
    }

    UUID getUUID();
}
