package server.handlers;

import remote_objects.Common.FacilityBooking;
import remote_objects.Client.ClientQuery;
import remote_objects.Server.ServerResponse;
import network.Network;
import server.ServerDB;

import java.net.InetSocketAddress;
import java.util.List;


public class ViewAllFacilities {
    private static ServerResponse response;
    private static List<FacilityBooking> bookings;

    public static void handleRequest(Network network, InetSocketAddress origin, ServerDB database, ClientQuery query) {
        bookings = database.getAllBookings();
        if (bookings.isEmpty()) {
            response = new ServerResponse(query.getId(), 404, bookings);
        } else {
            response = new ServerResponse(query.getId(), 200, bookings);
        }
        network.send(response, origin);
    }
}
