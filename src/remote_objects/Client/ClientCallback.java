package remote_objects.Client;

import java.net.SocketAddress;

/**
 * The object that is used to send a callback to the client
 */
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
