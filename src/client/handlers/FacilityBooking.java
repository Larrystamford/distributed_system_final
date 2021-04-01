package client.handlers;

import client.ClientUI;
import remote_objects.Common.Booking;
import utils.Constants;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.DayAndTime;
import semantics.Semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FacilityBooking {

    public static void createAndSendMessage(Semantics semInvo, Scanner scanner) {
        ClientRequest query;
        List<Booking> bookings = new ArrayList<Booking>();

        // appending bookings
        ClientUI.getMakeBookingInput(scanner, bookings);

        query = new ClientRequest();
        query.setRequestChoice(Constants.FACILITY_BOOKING);
        query.setBookings(bookings);

        int id = semInvo.requestServer(query);
        semInvo.receiveResponse(id, (response) -> {
            if (response.getServerStatus() == 200) {
                ClientUI.bookingResponse(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }
}
