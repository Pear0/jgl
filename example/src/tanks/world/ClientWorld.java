package tanks.world;

import tanks.entity.Entity;
import tanks.entity.MovingEntity;
import tanks.entity.UserTank;
import tanks.packet.world.*;
import tanks.proxy.ServerProxy;
import tanks.world.IWorld;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by william on 10/31/16.
 */
public class ClientWorld implements IWorld {


    private ArrayList<Entity> entities = new ArrayList<>();
    private WeakReference<UserTank> userTank;
    private ServerProxy proxy;
    private UUID uuid; // Server World's uuid
    private int width;
    private int height;

    public ClientWorld(ServerProxy proxy, UUID uuid, int width, int height) {
        this.proxy = proxy;
        this.uuid = uuid;
        this.width = width;
        this.height = height;
    }

    private float computeDelta(WorldPacket packet) {
        return (System.currentTimeMillis() - packet.sentTime) / 1000f;
    }

    public void handlePacket(WorldPacket p) {
        if (p instanceof UpdateEntityPacket) {
            UpdateEntityPacket packet = (UpdateEntityPacket) p;

            Entity entity = getEntity(packet.entityUUID);

            if (entity == null) {
                // TODO server should not send updates after entities are removed, but currently it does
                //throw new IllegalStateException("Invalid UUID, " + packet.entityUUID + " does not match any entities");
                return;
            }

            if (packet.updateMoving && !(entity instanceof MovingEntity)) {
                throw new IllegalStateException("Cannot update velocities of a non-moving entity: " + entity);
            }

            if (packet.updateMoving) {
                MovingEntity movingEntity = (MovingEntity) entity;

                float delta = computeDelta(packet);

                movingEntity.setPosition(packet.position.add(packet.velocity.mul(delta)));
                movingEntity.setRotation(packet.rotation + packet.rotationalVelocity * delta);

                movingEntity.setVelocity(packet.velocity);
                movingEntity.setRotationalVelocity(packet.rotationalVelocity);
            }else {
                entity.setPosition(packet.position);
                entity.setRotation(packet.rotation);
            }

            return;
        }

        if (p instanceof EntityAddedPacket) {
            EntityAddedPacket packet = (EntityAddedPacket) p;

            entities.add(packet.entity);
            return;
        }

        if (p instanceof EntityRemovedPacket) {
            EntityRemovedPacket packet = (EntityRemovedPacket) p;
            for (int i = 0; i < entities.size();) {
                if (packet.entityUUID.equals(entities.get(i).getUUID())) {
                    entities.remove(i);
                }else {
                    i++;
                }
            }
            return;
        }

        throw new IllegalStateException("Unknown packet: " + (p != null ? p.getClass().getName() : null));
    }

    @Override
    public void add(Entity.Builder builder) {
        proxy.send(new NewEntityPacket(getUUID(), builder));
    }

    @Override
    public void registerUserTank(UserTank tank) {
        userTank = new WeakReference<>(tank);
        entities.add(tank);

        proxy.send(new RegisterUserTankPacket(getUUID(), tank.getUUID()));
    }

    @Override
    public boolean remove(Entity e) {
        proxy.send(new RemoveEntityPacket(getUUID(), e.getUUID()));
        return true;
    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public boolean isServer() {
        return false;
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
