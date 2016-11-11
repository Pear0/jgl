package tanks.world;

import tanks.entity.Entity;
import tanks.entity.UserTank;
import tanks.world.IWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by william on 10/26/16.
 */
public class LocalWorld implements IWorld {

    public interface Listener {

        void onEntityAdded(Entity entity);

        void onEntityRemoved(Entity entity);

        void onEntityChanged(Entity entity);

    }

    private final UUID uuid = UUID.randomUUID();
    private int width;
    private int height;
    private Listener listener;

    public LocalWorld(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<Entity> entityTempCache = new ArrayList<>();

    @Override
    public void tick(float d) {
        entityTempCache.addAll(entities);
        for (Entity e : entityTempCache) {
            e.tick(this, d);
        }
        for (Entity e : entityTempCache) {
            boolean isDirty = e.tickCompleted(this);

            if (isDirty && listener != null) {
                listener.onEntityChanged(e);
            }
        }
        entityTempCache.clear();
    }

    public void add(Entity entity) {
        entities.add(entity);
        if (listener != null) {
            listener.onEntityAdded(entity);
            listener.onEntityChanged(entity);
        }
    }

    @Override
    public void add(Entity.Builder builder) {
        add(builder.build());
    }

    @Override
    public void registerUserTank(UserTank tank) {
        add(tank);
    }

    @Override
    public boolean remove(Entity e) {
        int index = entities.indexOf(e);
        if (index < 0) return false;
        else if (index == entities.size() - 1) {
            entities.remove(index);
        } else {
            int last = entities.size() - 1;
            entities.set(index, entities.remove(last));
        }

        if (listener != null) {
            listener.onEntityRemoved(e);
        }

        return true;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public boolean isServer() {
        return true;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }
}
