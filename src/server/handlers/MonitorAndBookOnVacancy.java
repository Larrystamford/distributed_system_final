package server.handlers;

import javacutils.Pair;
import semantics.Semantics;
import remote_objects.Client.ClientCallback;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.Booking;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import database.database;
import utils.BookingManager;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * IDEMPOTENT EXAMPLE
 * Allows user to indicate their interest for a booking slot that is currently unavailable
 * If the unavailable booking slot opens up, the callback will automatically book it for the user
 */
public class MonitorAndBookOnVacancy {

    public static void handleRequest(Semantics semInvo, InetSocketAddress origin, database database, ClientRequest query) {
        // check if the booking is already vacant
        Booking booking = query.getBookings().get(0);
        String name = booking.getName();
        DayAndTime start = booking.getStartTime();
        DayAndTime end = booking.getEndTime();

        if (BookingManager.hasVacancy(database.getBookings(name), start, end)) {

            server.handlers.FacilityBooking.handleRequest(semInvo, origin, database, query);
            return;
        }

        // register clients callback
        ClientCallback cInfo = new ClientCallback(query.getId(), origin, query.getMonitoringDuration() * 1000);
        database.registerBookOnVacancy(query.getBookings().get(0).getName(), cInfo, query);
    }

    public static void informRegisteredClients(Semantics semInvo, ServerResponse response, database database) {
        String facilityName = response.getBookings().get(0).getName();
        List<Pair<ClientCallback, ClientRequest>> addresses = database.getBookOnVacancyRequest(facilityName);
        if (addresses == null || addresses.isEmpty()) {
            return;
        }

        // send to client first in queue to book facility
        ClientCallback cInfo = addresses.get(0).first;
        ClientRequest registeredQuery = addresses.get(0).second;

        // make new client query to be handled by HandleBooking
        ClientRequest cQuery = new ClientRequest();
        cQuery.setBookings(registeredQuery.getBookings());
        cQuery.setId(cInfo.getRequestId());
        server.handlers.FacilityBooking.handleRequest(semInvo, (InetSocketAddress) cInfo.getSocket(), database, cQuery);
    }

}
