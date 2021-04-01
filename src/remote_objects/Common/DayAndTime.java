package remote_objects.Common;

public class DayAndTime {

    private int day;
    private int hour;
    private int minute;
    private int equivalentSeconds;


    public DayAndTime() {
    }

    public DayAndTime(int day, int hour, int minute) {
        setDate(day, hour, minute);
    }


    public void setDate(int day, int hour, int minute) {
        int secsMin = 60;
        int secsHour = 3600;
        int secsOneDay = 24 * secsHour;

        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.equivalentSeconds = day * secsOneDay + hour * secsHour + minute * secsMin;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getEquivalentSeconds() {
        return equivalentSeconds;
    }

    public int getEquivalentSecondsWithoutDays() {
        int secsHour = 3600;
        int secsOneDay = 24 * secsHour;

        return equivalentSeconds % secsOneDay;
    }


    public int convertDateIntoSeconds() {
        return equivalentSeconds;
    }

    public String dayToName() {
        if (day == 1) {
            return "MONDAY";
        } else if (day == 2) {
            return "TUESDAY";
        } else if (day == 3) {
            return "WEDNESDAY";
        } else if (day == 4) {
            return "THURSDAY";
        } else if (day == 5) {
            return "FRIDAY";
        } else if (day == 6) {
            return "SATURDAY";
        }

        return "SUNDAY";
    }

    public String getReadableFormat() {
        return String.format(dayToName() + ", %02d:%02d", hour, minute);
    }
}