

package server.handlers;

import database.database;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.Booking;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import semantics.Semantics;

import server.ServerUI;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows user to check a facility's availability via its name and interested day/s
 */
public class FacilitiesAvailability {
    private static ServerResponse response;

    public static void handleRequest(Semantics semInvo, InetSocketAddress origin, database database, ClientRequest query) {
        List<Booking> infos;
        List<Booking> unavailableBookingSlots = new ArrayList<>();
        List<Booking> totalUnavailableBookingSlots = new ArrayList<>();

        infos = query.getBookings();
        boolean facilityNameExist = database.facilityNameExist(infos.get(0).getName());

        if (facilityNameExist) {
            for (Booking info : infos) {
                String queriedName = info.getName();

                int queriedDay = info.getStartTime().getDay();
                unavailableBookingSlots = database.getBookings(queriedName, queriedDay);
                unavailableBookingSlots = confineToEachDay(unavailableBookingSlots, queriedDay);
                totalUnavailableBookingSlots.addAll(unavailableBookingSlots);
            }
            response = new ServerResponse(query.getId(), 200, totalUnavailableBookingSlots);
        } else {
            response = new ServerResponse(query.getId(), 404, totalUnavailableBookingSlots);
        }

        semInvo.replyClient(response, origin);
        ServerUI.printServerResponse(query, response);
    }

    public static List<Booking> confineToEachDay(List<Booking> unavailableBookingSlots, int queriedDay) {
        List<Booking> confinedUnavailableBookingSlots = new ArrayList<>();
        for (Booking booking : unavailableBookingSlots) {
            Booking newBooking = booking.clone();
            if (newBooking.getStartTime().getDay() != queriedDay) {
                newBooking.setStartTime(new DayAndTime(queriedDay, 0, 0));
            }

            if (newBooking.getEndTime().getDay() != queriedDay) {
                newBooking.setEndTime(new DayAndTime(queriedDay, 23, 59));
            }

            confinedUnavailableBookingSlots.add(newBooking);
        }
        return confinedUnavailableBookingSlots;
    }
}