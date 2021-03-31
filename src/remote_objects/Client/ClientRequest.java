package remote_objects.Client;

import remote_objects.Common.Marshal;
import remote_objects.Common.Booking;

import java.util.ArrayList;
import java.util.List;

public class ClientRequest extends Marshal {
    int requestChoice;
    int monitoringDuration;
    List<Booking> bookings = new ArrayList<Booking>();


    public ClientRequest() {
        super();
    }

    public ClientRequest(int requestChoice, List<Booking> bookings) {
        super();
        this.requestChoice = requestChoice;
        this.bookings = bookings;
    }

    public int getRequestChoice() {
        return requestChoice;
    }

    public void setRequestChoice(int requestChoice) {
        this.requestChoice = requestChoice;
    }

    public void setMonitoringDuration(int monitoringDuration) {
        this.monitoringDuration = monitoringDuration;
    }

    public int getMonitoringDuration() {
        return monitoringDuration;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
