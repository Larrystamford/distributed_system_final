package main.java.utils;

import main.java.remote_objects.Common.Booking;
import main.java.remote_objects.Common.DayAndTime;

import java.util.Comparator;
import java.util.List;

public class VacancyChecker {

    public static boolean isVacant(List<Booking> bookingsFilteredByName, DayAndTime start, DayAndTime end) {
        if (bookingsFilteredByName.isEmpty()) {
            return true;
        }

        bookingsFilteredByName.sort(BookingComparator);

        int chosenStart = start.convSecs();
        int chosenEnd = end.convSecs();

        // if chosenStart < 1st booking
        if (chosenStart < bookingsFilteredByName.get(0).getStartTime().convSecs()) {
            return true;
        }

        for (int i = 0; i < bookingsFilteredByName.size(); i++) {
            Booking booking = bookingsFilteredByName.get(i);

            // debug
            System.out.println(booking.getStartTime().convSecs() + " " + booking.getEndTime().convSecs());

            int bookingEnd = booking.getEndTime().convSecs();

            if (chosenStart >= bookingEnd) {
                // if chosen booking start > last booking end in sorted list
                // or if chosen booking end < next booking start
                if (i == bookingsFilteredByName.size() - 1 || chosenEnd <= bookingsFilteredByName.get(i + 1).getStartTime().convSecs()) {
                    return true;
                }

                // if chosen start is larger than the next booking's start
                if (chosenStart > bookingsFilteredByName.get(i + 1).getStartTime().convSecs())
                    continue;
                break;
            }
        }

        return false;
    }

    public static Comparator<Booking> BookingComparator = Comparator
            .comparing(l -> l.getStartTime().convSecs());

}
