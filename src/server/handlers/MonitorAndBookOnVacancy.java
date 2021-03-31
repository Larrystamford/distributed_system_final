package server.handlers;

import javacutils.Pair;
import network.Network;
import remote_objects.Client.ClientCallback;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.Booking;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import database.database;
import utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.List;

public class MonitorAndBookOnVacancy {

    public static void handleRequest(Network network, InetSocketAddress origin, database database, ClientRequest query) {
        // check if the booking is already vacant
        Booking booking = query.getBookings().get(0);
        String name = booking.getName();
        DayAndTime start = booking.getStartTime();
        DayAndTime end = booking.getEndTime();

        if (VacancyChecker.isVacant(database.getBookingsByName(name), start, end)) {
            server.handlers.FacilityBooking.handleRequest(network, origin, database, query);
            return;
        }

        // register clients interest
        ClientCallback cInfo = new ClientCallback(query.getId(), origin, query.getMonitoringDuration());
        database.registerBookOnVacancy(query.getBookings().get(0).getName(), cInfo, query);
    }

    public static void informRegisteredClients(Network network, ServerResponse response, database database) {
        String facilityName = response.getBookings().get(0).getName();
        List<Pair<ClientCallback, ClientRequest>> addresses = database.getValidBookOnVacancyRequest(facilityName);
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
        server.handlers.FacilityBooking.handleRequest(network, (InetSocketAddress) cInfo.getSocket(), database, cQuery);
    }

}
