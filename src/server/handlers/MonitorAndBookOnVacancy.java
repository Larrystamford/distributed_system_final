package server.handlers;

import javacutils.Pair;
import network.Network;
import remote_objects.Client.ClientCallback;
import remote_objects.Client.ClientQuery;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import server.ServerDB;
import utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.List;

public class MonitorAndBookOnVacancy {

    public static void handleRequest(Network network, InetSocketAddress origin, ServerDB database, ClientQuery query) {
        // check if the booking is already vacant
        remote_objects.Common.FacilityBooking booking = query.getBookings().get(0);
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

    public static void informRegisteredClients(Network network, ServerResponse response, ServerDB database) {
        String facilityName = response.getInfos().get(0).getName();
        List<Pair<ClientCallback, ClientQuery>> addresses = ServerDB.getValidBookOnVacancyRequest(facilityName);
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
        server.handlers.FacilityBooking.handleRequest(network, (InetSocketAddress) cInfo.getSocket(), database, cQuery);
    }

}
