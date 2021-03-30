package server.handlers;

import entity.*;
import javacutils.Pair;
import network.Network;
import server.ServerDB;
import utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.List;

public class MonitorAndBookOnVacancy {

    public static void handleRequest(Network network, InetSocketAddress origin, ServerDB database, ClientQuery query) {
        // check if the booking is already vacant
        BookingInfo booking = query.getBookings().get(0);
        String name = booking.getName();
        DateTime start = booking.getStartTime();
        DateTime end = booking.getEndTime();

        if (VacancyChecker.isVacant(database.getBookingsByName(name), start, end)) {
            FacilityBooking.handleRequest(network, origin, database, query);
            return;
        }

        // register clients interest
        ClientCallbackInfo cInfo = new ClientCallbackInfo(query.getId(), origin, query.getMonitoringDuration());
        database.registerBookOnVacancy(query.getBookings().get(0).getName(), cInfo, query);
    }

    public static void informRegisteredClients(Network network, ServerResponse response, ServerDB database) {
        String facilityName = response.getInfos().get(0).getName();
        List<Pair<ClientCallbackInfo, ClientQuery>> addresses = ServerDB.getValidBookOnVacancyRequest(facilityName);
        if (addresses == null || addresses.isEmpty()) {
            return;
        }

        // send to client first in queue to book facility
        ClientCallbackInfo cInfo = addresses.get(0).first;
        ClientQuery registeredQuery = addresses.get(0).second;

        // make new client query to be handled by HandleBooking
        ClientQuery cQuery = new ClientQuery();
        cQuery.setBookings(registeredQuery.getBookings());
        cQuery.setId(cInfo.getQueryId());
        FacilityBooking.handleRequest(network, (InetSocketAddress) cInfo.getSocket(), database, cQuery);
    }

}
