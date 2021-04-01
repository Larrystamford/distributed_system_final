package client.handlers;

import client.ClientUI;
import utils.Constants;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import semantics.Semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FacilitiesAvailability {
    public static void createAndSendMessage(Semantics semInvo, Scanner scanner) {
        ClientRequest query;
        List<Booking> bookings = new ArrayList<>();

        // appending bookings
        ClientUI.getListFacilitiesInput(scanner, bookings);

        query = new ClientRequest();
        query.setRequestChoice(Constants.FACILITY_AVAILABILITY);
        query.setBookings(bookings);

        int id = semInvo.requestServer(query);
        semInvo.receiveResponse(id, (response) -> {
            if (response.getServerStatus() == 200) {
                ClientUI.showFacilityAvailabilityResponse(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }
}
