package server.handlers;

import entity.BookingInfo;
import entity.ClientQuery;
import entity.DateTime;
import entity.ServerResponse;
import network.Network;
import server.ServerDB;
import utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


public class FacilityBooking {
    private static ServerResponse response;

    public static void handleRequest(Network network, InetSocketAddress origin, ServerDB database, ClientQuery query) {
        List<BookingInfo> bookings;
        List<BookingInfo> bookingsFiltered;
        List<BookingInfo> confirmedBooking = new ArrayList<BookingInfo>();

        bookings = query.getBookings();
        boolean bookingNameExist = database.bookingNameExist(bookings.get(0).getName());
        List<BookingInfo> bookingsFilteredByName = database.getBookingsByName(bookings.get(0).getName());

        if (bookingNameExist) {
            for (BookingInfo info : bookings) {
                String bookingName = info.getName();
                int startDay = info.getStartTime().getDay();
                int startHour = info.getStartTime().getHour();
                int startMinute = info.getStartTime().getMinute();
                int endDay = info.getEndTime().getDay();
                int endHour = info.getEndTime().getHour();
                int endMinute = info.getEndTime().getMinute();

                DateTime startTime = new DateTime(startDay, startHour, startMinute);
                DateTime endTime = new DateTime(endDay, endHour, endMinute);

                if (VacancyChecker.isVacant(bookingsFilteredByName, startTime, endTime)) {
                    // book here
                    BookingInfo newBooking = ServerDB.createBooking(bookingName, startTime, endTime);
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