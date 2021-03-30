package entity;

import java.util.UUID;


public class BookingInfo implements Cloneable {
    String name;
    String uuid;
    DateTime startTime;
    DateTime endTime;
    DateTime offset;

    public BookingInfo() {
        this.uuid = UUID.randomUUID().toString().replace("-", "");
    }

    public BookingInfo(String name,
                       DateTime bookStart,
                       DateTime bookEnd
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

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public DateTime getOffset() {
        return this.offset;
    }

    public void setOffset(DateTime offset) {
        this.offset = offset;
    }

    public BookingInfo clone() {
        try {
            return (BookingInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
