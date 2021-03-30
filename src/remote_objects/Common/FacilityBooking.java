package remote_objects.Common;

import java.util.UUID;


public class FacilityBooking implements Cloneable {
    String name;
    String uuid;
    DayAndTime startTime;
    DayAndTime endTime;
    DayAndTime offset;

    public FacilityBooking() {
        this.uuid = UUID.randomUUID().toString();
    }

    public FacilityBooking(String name,
                           DayAndTime bookStart,
                           DayAndTime bookEnd
    ) {
        this.name = name;
        this.startTime = bookStart;
        this.endTime = bookEnd;
        this.uuid = UUID.randomUUID().toString().replace("-", "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DayAndTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DayAndTime startTime) {
        this.startTime = startTime;
    }

    public DayAndTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DayAndTime endTime) {
        this.endTime = endTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public DayAndTime getOffset() {
        return this.offset;
    }

    public void setOffset(DayAndTime offset) {
        this.offset = offset;
    }

    public FacilityBooking clone() {
        try {
            return (FacilityBooking) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
