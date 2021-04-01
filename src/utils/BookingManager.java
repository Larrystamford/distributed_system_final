package utils;

import remote_objects.Common.Booking;
import remote_objects.Common.DayAndTime;

import java.util.Comparator;
import java.util.List;

public class BookingManager {
    public static Comparator<Booking> dateSorter = Comparator
            .comparing(l -> l.getStartTime().convertDateIntoSeconds());

    public static boolean hasVacancy(List<Booking> bookings, DayAndTime start, DayAndTime end) {
        if (bookings.isEmpty()) {
            return true;
        }

        bookings.sort(dateSorter);
        int startSeconds = start.convertDateIntoSeconds();
        int endSeconds = end.convertDateIntoSeconds();
        if (startSeconds < bookings.get(0).getStartTime().convertDateIntoSeconds()) {
            return true;
        }

        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            int bookingEnd = booking.getEndTime().convertDateIntoSeconds();
            if (startSeconds >= bookingEnd) {
                if (i == bookings.size() - 1 || endSeconds <= bookings.get(i + 1).getStartTime().convertDateIntoSeconds()) {
                    return true;
                }
                if (startSeconds > bookings.get(i + 1).getStartTime().convertDateIntoSeconds())
                    continue;
                break;
            }
        }
        return false;
    }
}
