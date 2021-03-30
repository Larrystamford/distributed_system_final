package remote_objects.Client;

import java.net.SocketAddress;

// Client Info objects are used for server callbacks
public class ClientCallback {
    int queryId;
    SocketAddress socket;
    long expire;

    public ClientCallback() {
    }

    public ClientCallback(int queryId, SocketAddress socket, int timeout) {
        this.queryId = queryId;
        this.socket = socket;
        this.expire = System.currentTimeMillis() + timeout * 1000;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public SocketAddress getSocket() {
        return socket;
    }

    public void setSocket(SocketAddress socket) {
        this.socket = socket;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}
