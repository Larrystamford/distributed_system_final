package client.handlers;

import client.ClientUI;
import utils.Constants;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import semantics.Semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
