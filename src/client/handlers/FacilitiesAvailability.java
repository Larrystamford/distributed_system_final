package client.handlers;

import client.ClientUI;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.Booking;
import semantics.Semantics;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Allows user to check a facility's availability via its name and interested day/s
 */
public class FacilitiesAvailability {
    public static void createAndSendMessage(Semantics semInvo, Scanner scanner) {
        ClientRequest query;
        List<Booking> bookings = new ArrayList<>();

        ClientUI.getFacilitiesAvailability(scanner, bookings);

        query = new ClientRequest();
        query.setRequestChoice(Constants.FACILITY_AVAILABILITY);
        query.setBookings(bookings);

        int id = semInvo.requestServer(query);
        semInvo.receiveResponse(id, (response) -> {
            if (response.getServerStatus() == 200) {
                ClientUI.facilitiesAvailabilityResponse(query, response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }
}
