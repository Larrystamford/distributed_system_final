package server.handlers;

import remote_objects.Client.ClientRequest;
import remote_objects.Common.Booking;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import semantics.Semantics;
import database.database;
import server.ServerUI;
import utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


/**
 * Allows user to book a facility
 */
public class FacilityBooking {
    private static ServerResponse response;

    public static void handleRequest(Semantics semInvo, InetSocketAddress origin, database database, ClientRequest query) {
        List<Booking> bookings;
        List<Booking> confirmedBooking = new ArrayList<>();

        bookings = query.getBookings();
        boolean facilityNameExist = database.facilityNameExist(bookings.get(0).getName());
        List<Booking> bookingsFilteredByName = database.getBookings(bookings.get(0).getName());

        if (facilityNameExist) {
            for (Booking info : bookings) {
                String bookingName = info.getName();
                int startDay = info.getStartTime().getDay();
                int startHour = info.getStartTime().getHour();
                int startMinute = info.getStartTime().getMinute();
                int endDay = info.getEndTime().getDay();
                int endHour = info.getEndTime().getHour();
                int endMinute = info.getEndTime().getMinute();

                DayAndTime startTime = new DayAndTime(startDay, startHour, startMinute);
                DayAndTime endTime = new DayAndTime(endDay, endHour, endMinute);

                if (VacancyChecker.isVacant(bookingsFilteredByName, startTime, endTime)) {
                    Booking newBooking = database.createBooking(bookingName, startTime, endTime);
                    confirmedBooking.add(newBooking);
                    response = new ServerResponse(query.getId(), 200, confirmedBooking);
                    FacilityMonitoring.informRegisteredClients(semInvo, response, query.getRequestChoice());
                } else {
                    response = new ServerResponse(query.getId(), 406, confirmedBooking);
                }
            }
        } else {
            response = new ServerResponse(query.getId(), 404, confirmedBooking);
        }
        semInvo.replyClient(response, origin);
        ServerUI.printServerResponse(query, response);
    }
}