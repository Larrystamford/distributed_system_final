package utils;

import remote_objects.Common.DayAndTime;

public class DateUtils {

    static int secsHour = 3600;
    static int secsMin = 60;
    static int secondsInDay = 24 * 3600;

    public static int convertSecondsToHour(int seconds) {
        return Math.floorDiv(seconds, secsHour);
    }

    public static int convertSecondsToMinutes(int seconds) {
        int hours = Math.floorDiv(seconds, secsHour);
        seconds -= hours * secsHour;
        return seconds / secsMin;
    }

    public static DayAndTime convertSecondsToDate(int seconds) {
        int day = seconds / (secondsInDay);
        seconds -= day * secondsInDay;
        int hour = seconds / secsHour;
        seconds -= hour * secsHour;
        int minute = seconds / secsMin;

        if (checkDate(day, hour, minute)) {
            return new DayAndTime(day, hour, minute);
        }
        return null;
    }

    public static boolean checkDate(int day, int hour, int minute) {
        if (day < 1 || day > 7) {
            return false;
        } else if (hour < 0 || hour > 23) {
            return false;
        } else if (minute < 0 || minute > 59) {
            return false;
        }
        return true;
    }

    public static boolean checkDate(String day, String hour, String minute) {
        boolean validDay = EntryChecker.isAppropriateInteger(day, 0, 7);
        boolean validHour = EntryChecker.isAppropriateInteger(hour, 0, 23);
        boolean validMinute = EntryChecker.isAppropriateInteger(minute, 0, 59);
        return validDay && validHour && validMinute;
    }
}
