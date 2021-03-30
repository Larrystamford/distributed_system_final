package main.java.remote_objects.Client;

import main.java.remote_objects.Common.Marshal;
import main.java.remote_objects.Common.Booking;

import java.util.ArrayList;
import java.util.List;

public class ClientQuery extends Marshal {
    // type is switch case number
    int type;
    int monitoringDuration;
    List<Booking> bookings = new ArrayList<Booking>();


    public ClientQuery() {
        super();
    }

    public ClientQuery(int type, List<Booking> bookings) {
        super();
        this.type = type;
        this.bookings = bookings;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
