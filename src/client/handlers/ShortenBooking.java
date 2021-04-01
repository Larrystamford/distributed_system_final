package client.handlers;

import client.ClientUI;
import utils.DateUtils;
import utils.Constants;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.DayAndTime;
import semantics.Semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * NON-IDEMPOTENT EXAMPLE
 * Allows user to shorten their existing booking via their unique booking ID
 */
public class ShortenBooking {
    private static ClientRequest query;

    public static void createAndSendMessage(Semantics semInvo, Scanner scanner) {
        Booking booking = ClientUI.getShortenBookingInput(scanner);
        List<Booking> payload = new ArrayList<>();
        payload.add(booking);

        query = new ClientRequest(Constants.SHORTEN_BOOKING, payload);
        int id = semInvo.requestServer(query);
        semInvo.receiveResponse(id, (response) -> {
            if (response.getServerStatus() == 200) {
                ClientUI.changeBookingResponse(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }
}
