package tanks.packet;

import com.esotericsoftware.kryo.KryoSerializable;

/**
 * Created by william on 10/31/16.
 */
public class AbstractPacket {

    public long sentTime;

    public AbstractPacket() {
        this.sentTime = System.currentTimeMillis();
    }

}
