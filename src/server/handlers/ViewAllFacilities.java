package server.handlers;

import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import remote_objects.Server.ServerResponse;
import semantics.Semantics;
import database.database;
import server.ServerUI;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Additional query that allows user to view all the facilities names that are available for booking
 */
public class ViewAllFacilities {
    private static ServerResponse response;
    private static List<Booking> bookings;

    public static void handleRequest(Semantics semInvo, InetSocketAddress origin, database database, ClientRequest query) {
        bookings = database.getAllBookings();
        if (bookings.isEmpty()) {
            response = new ServerResponse(query.getId(), 404, bookings);
        } else {
            response = new ServerResponse(query.getId(), 200, bookings);
        }
        semInvo.replyClient(response, origin);
        ServerUI.printServerResponse(query, response);
    }
}
