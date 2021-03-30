package main.java.remote_objects.Common;

import java.net.InetSocketAddress;

public class AddressAndData {
    InetSocketAddress origin;
    Marshal data;

    public AddressAndData(InetSocketAddress origin, Marshal data) {
        this.origin = origin;
        this.data = data;
    }

    public InetSocketAddress getOrigin() {
        return origin;
    }

    public Marshal getData() {
        return data;
    }
}
