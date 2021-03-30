package server;

import entity.BookingInfo;
import entity.ClientQuery;
import entity.ServerResponse;
import network.Network;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


public class HandleCancelBooking {
    private static ServerResponse response;

    public static void handleRequest(Network network, InetSocketAddress origin, ServerDB database, ClientQuery query) {
        ServerResponse response;
        List<BookingInfo> cancelledBooking = new ArrayList<BookingInfo>();

        BookingInfo cancelInfo = query.getBookings().get(0);
        String UUID = cancelInfo.getUuid();

        BookingInfo bookingToCancel = database.returnBookingIfExists(UUID);
        if (bookingToCancel != null) {
            BookingInfo bookingDeleted = database.deleteBooking(UUID);
            cancelledBooking.add(bookingDeleted);
            response = new ServerResponse(query.getId(), 200, cancelledBooking);
            HandleMonitorFacility.informRegisteredClients(network, response, query.getType());
            HandleBookOnVacancy.informRegisteredClients(network, response, database);
        } else {
            response = new ServerResponse(query.getId(), 404, cancelledBooking);
        }

        network.send(response, origin);
    }
}