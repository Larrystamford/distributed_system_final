package server.handlers;

import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import remote_objects.Server.ServerResponse;
import network.Network;
import database.Database;

import java.net.InetSocketAddress;
import java.util.List;


public class ViewAllFacilities {
    private static ServerResponse response;
    private static List<Booking> bookings;

    public static void handleRequest(Network network, InetSocketAddress origin, Database database, ClientRequest query) {
        bookings = database.getAllBookings();
        if (bookings.isEmpty()) {
            response = new ServerResponse(query.getId(), 404, bookings);
        } else {
            response = new ServerResponse(query.getId(), 200, bookings);
        }
        network.replyClient(response, origin);
    }
}
