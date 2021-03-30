package main.java.server.handlers;

import main.java.remote_objects.Client.ClientQuery;
import main.java.remote_objects.Common.Booking;
import main.java.remote_objects.Common.DayAndTime;
import main.java.remote_objects.Server.ServerResponse;
import main.java.network.Network;
import main.java.database.database;
import main.java.utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


public class FacilityBooking {
    private static ServerResponse response;

    public static void handleRequest(Network network, InetSocketAddress origin, database database, ClientQuery query) {
        List<Booking> bookings;
        List<Booking> bookingsFiltered;
        List<Booking> confirmedBooking = new ArrayList<Booking>();

        bookings = query.getBookings();
        boolean facilityNameExist = database.facilityNameExist(bookings.get(0).getName());
        List<Booking> bookingsFilteredByName = database.getBookingsByName(bookings.get(0).getName());

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
                    // book here
                    Booking newBooking = database.createBooking(bookingName, startTime, endTime);
                    confirmedBooking.add(newBooking);
                    response = new ServerResponse(query.getId(), 200, confirmedBooking);
                    FacilityMonitoring.informRegisteredClients(network, response, query.getType());
                } else {
                    // status conflict
                    response = new ServerResponse(query.getId(), 409, confirmedBooking);
                }
            }
        } else {
            response = new ServerResponse(query.getId(), 404, confirmedBooking);
        }
        network.send(response, origin);
    }
}