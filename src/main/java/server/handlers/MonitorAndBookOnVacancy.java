package main.java.server.handlers;

import javacutils.Pair;
import main.java.network.Network;
import main.java.remote_objects.Client.ClientCallback;
import main.java.remote_objects.Client.ClientQuery;
import main.java.remote_objects.Common.Booking;
import main.java.remote_objects.Common.DayAndTime;
import main.java.remote_objects.Server.ServerResponse;
import main.java.database.database;
import main.java.utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.List;

public class MonitorAndBookOnVacancy {

    public static void handleRequest(Network network, InetSocketAddress origin, database database, ClientQuery query) {
        // check if the booking is already vacant
        Booking booking = query.getBookings().get(0);
        String name = booking.getName();
        DayAndTime start = booking.getStartTime();
        DayAndTime end = booking.getEndTime();

        if (VacancyChecker.isVacant(database.getBookingsByName(name), start, end)) {
            FacilityBooking.handleRequest(network, origin, database, query);
            return;
        }

        // register clients interest
        ClientCallback cInfo = new ClientCallback(query.getId(), origin, query.getMonitoringDuration());
        database.registerBookOnVacancy(query.getBookings().get(0).getName(), cInfo, query);
    }

    public static void informRegisteredClients(Network network, ServerResponse response, database database) {
        String facilityName = response.getInfos().get(0).getName();
        List<Pair<ClientCallback, ClientQuery>> addresses = database.getValidBookOnVacancyRequest(facilityName);
        if (addresses == null || addresses.isEmpty()) {
            return;
        }

        // send to client first in queue to book facility
        ClientCallback cInfo = addresses.get(0).first;
        ClientQuery registeredQuery = addresses.get(0).second;

        // make new client query to be handled by HandleBooking
        ClientQuery cQuery = new ClientQuery();
        cQuery.setBookings(registeredQuery.getBookings());
        cQuery.setId(cInfo.getQueryId());
        FacilityBooking.handleRequest(network, (InetSocketAddress) cInfo.getSocket(), database, cQuery);
    }

}
