package main.java.remote_objects.Client;

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

    public SocketAddress getSocket() {
        return socket;
    }

    public long getExpire() {
        return expire;
    }
}
