package main.java.remote_objects.Common;

public class Ack extends Marshal {
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
}
