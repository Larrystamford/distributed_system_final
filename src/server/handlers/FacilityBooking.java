package server.handlers;

import remote_objects.Client.ClientQuery;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import network.Network;
import server.ServerDB;
import utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


public class FacilityBooking {
    private static ServerResponse response;

    public static void handleRequest(Network network, InetSocketAddress origin, ServerDB database, ClientQuery query) {
        List<remote_objects.Common.FacilityBooking> bookings;
        List<remote_objects.Common.FacilityBooking> bookingsFiltered;
        List<remote_objects.Common.FacilityBooking> confirmedBooking = new ArrayList<remote_objects.Common.FacilityBooking>();

        bookings = query.getBookings();
        boolean bookingNameExist = database.bookingNameExist(bookings.get(0).getName());
        List<remote_objects.Common.FacilityBooking> bookingsFilteredByName = database.getBookingsByName(bookings.get(0).getName());

        if (bookingNameExist) {
            for (remote_objects.Common.FacilityBooking info : bookings) {
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
                    remote_objects.Common.FacilityBooking newBooking = ServerDB.createBooking(bookingName, startTime, endTime);
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