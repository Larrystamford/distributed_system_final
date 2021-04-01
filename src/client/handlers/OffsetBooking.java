package client.handlers;

import client.ClientUI;
import utils.DateUtils;
import utils.UserInputValidator;
import utils.Constants;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.DayAndTime;
import semantics.Semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Allows user to offset their existing booking via their unique booking ID
 */
public class OffsetBooking {
    private static ClientRequest query;

    public static void createAndSendMessage(Semantics semInvo, Scanner scanner) {
        Booking booking = ClientUI.getOffsetBookingInput(scanner);
        List<Booking> payload = new ArrayList<>();
        payload.add(booking);

        query = new ClientRequest(Constants.OFFSET_BOOKING, payload);
        int id = semInvo.requestServer(query);
        semInvo.receiveResponse(id, (response) -> {
            if (response.getServerStatus() == 200) {
                ClientUI.changeBookingResponse(query, response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }
}
