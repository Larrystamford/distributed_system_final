package utils;

import entity.DateTime;

public class DateUtils {

    public static int getHourWithSeconds(int seconds) {
        int secsHour = 3600;
        return Math.floorDiv(seconds, secsHour);
    }

    public static int getMinuteWithSeconds(int seconds) {
        int secsHour = 3600;
        int secsMin = 60;

        int hours = Math.floorDiv(seconds, secsHour);
        seconds -= hours * secsHour;
        return seconds / secsMin;
    }

    public static DateTime convSecondsToDateTime(int seconds) {
        int day = seconds / (24 * 3600);
        seconds -= day * 24 * 3600;
        int hour = seconds / 3600;
        seconds -= hour * 3600;
        int minute = seconds / 60;

        if (isValidDateTimeFormat(day, hour, minute)) {
            return new DateTime(day, hour, minute);
        }
        return null;
    }

    public static boolean isValidDateTimeFormat(int day, int hour, int minute) {
       return day > 0 && day <= 7 && hour >= 0 && hour < 24 && minute >= 0 && minute < 60;
    }

    public static boolean isValidDateTimeFormat(String day, String hour, String minute) {
        boolean validDay = UserInputValidator.isNumericAndWithinRange(day, 0, 7);
        boolean validHour = UserInputValidator.isNumericAndWithinRange(hour, 0, 23);
        boolean validMinute = UserInputValidator.isNumericAndWithinRange(minute, 0, 59);
        return validDay && validHour && validMinute;
    }
}
