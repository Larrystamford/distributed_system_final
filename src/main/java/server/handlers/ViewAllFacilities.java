package main.java.server.handlers;

import main.java.remote_objects.Common.Booking;
import main.java.remote_objects.Client.ClientQuery;
import main.java.remote_objects.Server.ServerResponse;
import main.java.network.Network;
import main.java.database.database;

import java.net.InetSocketAddress;
import java.util.List;


public class ViewAllFacilities {
    private static ServerResponse response;
    private static List<Booking> bookings;

    public static void handleRequest(Network network, InetSocketAddress origin, database database, ClientQuery query) {
        bookings = database.getAllBookings();
        if (bookings.isEmpty()) {
            response = new ServerResponse(query.getId(), 404, bookings);
        } else {
            response = new ServerResponse(query.getId(), 200, bookings);
        }
        network.send(response, origin);
    }
}
