package entity;

import marshaller.Marshallable;
import java.io.File;

import java.net.InetSocketAddress;

public class Response {
    InetSocketAddress origin;
    Marshallable data;

    public Response(InetSocketAddress origin, Marshallable data) {
        this.origin = origin;
        this.data = data;
    }

    public InetSocketAddress getOrigin() {
        return origin;
    }

    public void setOrigin(InetSocketAddress origin) {
        this.origin = origin;
    }

    public Marshallable getData() {
        return data;
    }

    public void setData(Marshallable data) {
        this.data = data;
    }
}
