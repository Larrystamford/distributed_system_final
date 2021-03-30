package remote_objects.Client;

import java.net.SocketAddress;

// Client Info objects are used for server callbacks
public class ClientCallback {
    int requestId;
    SocketAddress socket;
    long expire;


    public ClientCallback(int requestId, SocketAddress socket, int timeout) {
        this.requestId = requestId;
        this.socket = socket;
        this.expire = System.currentTimeMillis() + timeout * 1000;
    }

    public int getRequestId() {
        return requestId;
    }

    public SocketAddress getSocket() {
        return socket;
    }

    public long getExpire() {
        return expire;
    }
}
