

package server.handlers;

import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import semantics.Semantics;
import database.database;
import server.ServerUI;
import utils.DateUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FacilitiesAvailability {
    private static ServerResponse response;

    public static void handleRequest(Semantics semInvo, InetSocketAddress origin, database database, ClientRequest query) {
        List<Booking> bookings;
        List<Booking> bookingsFiltered;
        List<Booking> availableBookings = new ArrayList<Booking>();

        bookings = query.getBookings();
        boolean facilityNameExist = database.hasFacility(bookings.get(0).getName());

        if (facilityNameExist) {
            for (Booking info : bookings) {
                String interestedName = info.getName();

                int interestedDay = info.getStartTime().getDay();
                bookingsFiltered = database.getBookingsByNameAndDay(interestedName, interestedDay);
                bookingsFiltered = getFreeBookingSlots(bookingsFiltered, interestedName, interestedDay);
                availableBookings.addAll(bookingsFiltered);
            }
            response = new ServerResponse(query.getId(), 200, availableBookings);
        } else {
            response = new ServerResponse(query.getId(), 404, availableBookings);
        }

        semInvo.replyClient(response, origin);
        ServerUI.printServerResponse(query, response);
    }

    public static List<Booking> getFreeBookingSlots(List<Booking> bookingsFiltered, String interestedName, int interestedDay) {
        int secondsInADay = 86400;
        List<Booking> freeBookings = new ArrayList<Booking>();
        Booking booking;

        // whole day is available
        if (bookingsFiltered.isEmpty()) {
            DayAndTime d1 = new DayAndTime(interestedDay, 0, 0);
            DayAndTime d2 = new DayAndTime(interestedDay, 23, 59);
            booking = new Booking(interestedName.toUpperCase(), d1, d2);
            freeBookings.add(booking);

            return freeBookings;
        }

        // 1 is booked, 0 is not booked
        List<Integer> freeSlotsInSeconds = new ArrayList<>(Collections.nCopies(secondsInADay, 0));
        for (int i = 0; i < bookingsFiltered.size(); i++) {
            booking = bookingsFiltered.get(i);
            int bookingStartDay = booking.getStartTime().getDay();
            int bookingEndDay = booking.getEndTime().getDay();

            int bookingStartSeconds = booking.getStartTime().getEquivalentSecondsWithoutDays();
            int bookingEndSeconds = booking.getEndTime().getEquivalentSecondsWithoutDays();

            if (bookingStartDay == bookingEndDay) {
                for (int j = bookingStartSeconds; j < bookingEndSeconds + 1; j++) {
                    freeSlotsInSeconds.add(j, 1);
                }
            } else if (bookingStartDay < interestedDay && interestedDay < bookingEndDay) {
                // the whole day is booked
                for (int k = 0; k < secondsInADay; k++) {
                    freeSlotsInSeconds.add(k, 1);
                }
            } else if (bookingStartDay == interestedDay && interestedDay < bookingEndDay) {
                for (int n = bookingStartSeconds; n < secondsInADay; n++) {
                    freeSlotsInSeconds.add(n, 1);
                }
            } else if (bookingEndDay == interestedDay && bookingStartDay < interestedDay) {
                for (int m = 0; m < bookingEndSeconds + 1; m++) {
                    freeSlotsInSeconds.add(m, 1);
                }
            }
        }

        int freeStart = -1;
        int freeEnd = -1;
        DayAndTime d1, d2;

        if (Collections.frequency(freeSlotsInSeconds, 1) != secondsInADay) {
            for (int x = 0; x < secondsInADay; x++) {
                int slotValue = freeSlotsInSeconds.get(x);
                int nextSlotValue;

                if (x == secondsInADay - 1) {
                    // this ensures that the end time will be at most 86399 which represents 23:59
                    nextSlotValue = 1;
                } else {
                    nextSlotValue = freeSlotsInSeconds.get(x + 1);
                }

                if (slotValue == 0 && freeStart == -1) {
                    freeStart = x;
                } else if (nextSlotValue == 1 && freeStart != -1 && freeEnd == -1) {
                    // when the next second is booked, save the current free slot
                    freeEnd = x;

                    int freeStartHour = DateUtils.convertSecondsToHour(freeStart);
                    int freeStartMinutes = DateUtils.convertSecondsToMinutes(freeStart);
                    int freeEndHour = DateUtils.convertSecondsToHour(freeEnd);
                    int freeEndMinutes = DateUtils.convertSecondsToMinutes(freeEnd);

                    // avoid cases like start 23:59 end 23:59
                    if (!(freeStartHour == freeEndHour && freeStartMinutes == freeEndMinutes)) {
                        d1 = new DayAndTime(interestedDay, freeStartHour, freeStartMinutes);
                        d2 = new DayAndTime(interestedDay, freeEndHour, freeEndMinutes);
                        booking = new Booking(interestedName.toUpperCase(), d1, d2);
                        freeBookings.add(booking);
                    }

                    freeStart = -1;
                    freeEnd = -1;
                }
            }
        }

        return freeBookings;
    }
}