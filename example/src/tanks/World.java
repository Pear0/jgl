package tanks;

import jgl.Renderer;
import jgl.shape.*;
import jgl.shape.Polygon;
import tanks.entity.Entity;

import java.awt.*;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by william on 10/26/16.
 */
public class World {

    private ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<Entity> entityTempCache = new ArrayList<>();

    public void render(Renderer r) {
        for (Entity e : getEntities()) {
            int l = r.pushMatrix();
            e.render(r);
            r.popMatrix(l);
            r.setColor(Color.red);

            jgl.shape.Shape bb = e.getBoundingBox();
            if (bb instanceof jgl.shape.Polygon) {
                r.drawPolygon((Polygon) bb);
            }
        }
    }

    public void tick(float d) {
        entityTempCache.addAll(entities);
        for (Entity e : entityTempCache) {
            e.tick(this, d);
        }
        entityTempCache.clear();
    }

    public void add(Entity e) {
        entities.add(e);
    }

    public boolean remove(Entity e) {
        return entities.remove(e);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public <T extends Entity> List<T> getEntities(Class<T> clazz) {
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

    public <T extends Entity> T getEntity(Class<T> clazz) {
        for (Entity e : getEntities()) {
            if (clazz.isInstance(e)) {
                //noinspection unchecked
                return (T) e;
            }
        }
        return null;
    }

}
