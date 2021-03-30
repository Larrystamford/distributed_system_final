package remote_objects.Client;

import remote_objects.Common.Marshal;
import remote_objects.Common.FacilityBooking;

import java.util.ArrayList;
import java.util.List;

public class ClientQuery extends Marshal {
    // type is switch case number
    int type;
    int monitoringDuration;
    List<FacilityBooking> bookings = new ArrayList<FacilityBooking>();


    public ClientQuery() {
        super();
    }

    public ClientQuery(int type, List<FacilityBooking> bookings) {
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

    public List<FacilityBooking> getBookings() {
        return bookings;
    }

    public void setBookings(List<FacilityBooking> bookings) {
        this.bookings = bookings;
    }
}
