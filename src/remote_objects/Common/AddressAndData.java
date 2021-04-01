package remote_objects.Common;

import java.net.InetSocketAddress;

/**
 * When data is received via the UDP, we will use this object to store the socket address and the client/server Marshal data
 */
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
