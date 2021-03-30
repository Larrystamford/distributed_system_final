package entity;

import marshaller.Marshallable;

import java.util.ArrayList;
import java.util.List;

public class ClientQuery extends Marshallable {
    // type is switch case number
    int type;
    int monitoringDuration;
    List<BookingInfo> bookings = new ArrayList<BookingInfo>();


    public ClientQuery() {
        super();
    }

    public ClientQuery(int type, List<BookingInfo> bookings) {
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

    public List<BookingInfo> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingInfo> bookings) {
        this.bookings = bookings;
    }
}
