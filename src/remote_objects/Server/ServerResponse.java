package remote_objects.Server;

import remote_objects.Common.Marshal;
import remote_objects.Common.Booking;

import java.util.List;

public class ServerResponse extends Marshal implements Cloneable {
    int requestId;
    int serverStatus;
    int requestChoice;
    List<Booking> bookings;

    public ServerResponse() {
        super();
    }

    public ServerResponse(int requestId, int serverStatus, List<Booking> bookings) {
        super();
        this.requestId = requestId;
        this.serverStatus = serverStatus;
        this.bookings = bookings;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getServerStatus() {
        return serverStatus;
    }

    public void setRequestChoice(int requestChoice) {
        this.requestChoice = requestChoice;
    }

    public int getRequestChoice() {
        return requestChoice;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public ServerResponse clone() {
        try {
            return (ServerResponse) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
