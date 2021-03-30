package entity;

import java.io.File;

import marshaller.Marshallable;

public class Ack extends Marshallable {
    int ackId;

    public Ack() {
        super();
    }

    public Ack(int ackId) {
        super();
        this.ackId = ackId;
    }

    public int getAckId() {
        return ackId;
    }
    public void setAckId(int ackId) {
        this.ackId = ackId;
    }
}
