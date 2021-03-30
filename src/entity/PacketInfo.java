package entity;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.io.File;

public class PacketInfo {
    int id;
    InetSocketAddress origin;

    public PacketInfo(int id, InetSocketAddress origin) {
        this.id = id;
        this.origin = origin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacketInfo that = (PacketInfo) o;
        return id == that.id && origin.equals(that.origin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, origin);
    }

}
