package tanks;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import tanks.entity.Tank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by william on 11/2/16.
 */
public class TankServerBroadcast {

    public static void encode(TankServerBroadcast b, OutputStream out1) {
        Output out = new Output(out1);
        out.writeLong(b.serverUUID.getMostSignificantBits());
        out.writeLong(b.serverUUID.getLeastSignificantBits());
        out.writeString(b.hoster);
        out.flush();
    }

    public static TankServerBroadcast decode(InputStream in1) {
        TankServerBroadcast b = new TankServerBroadcast();
        Input in = new Input(in1);
        long highBits = in.readLong();
        long lowBits = in.readLong();
        b.serverUUID = new UUID(highBits, lowBits);
        b.hoster = in.readString();
        return b;
    }

    public static byte[] encode(TankServerBroadcast b) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        encode(b, out);
        return out.toByteArray();
    }

    public static TankServerBroadcast decode(byte[] data, int offset, int length) {
        ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length);
        return decode(in);
    }

    public static TankServerBroadcast decode(byte[] data) {
        return decode(data, 0, data.length);
    }

    private UUID serverUUID;
    private String hoster;

    public TankServerBroadcast(UUID serverUUID, String hoster) {
        this.serverUUID = serverUUID;
        this.hoster = hoster;
    }

    private TankServerBroadcast() {
    }

    public UUID getServerUUID() {
        return serverUUID;
    }

    public String getHoster() {
        return hoster;
    }
}
