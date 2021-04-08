package client.handlers;

import client.ClientUI;
import utils.Constants;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import semantics.Semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * IDEMPOTENT EXAMPLE
 * Allows user to indicate their interest for a booking slot that is currently unavailable
 * If the unavailable booking slot opens up, the callback will automatically book it for the user
 */
public class MonitorAndBookOnVacancy {

    public static void createAndSendMessage(Semantics semInvo, Scanner scanner) {
        ClientRequest query;
        List<Booking> bookings = new ArrayList<Booking>();

        query = new ClientRequest();
        ClientUI.getBookOnVacancyInput(scanner, bookings, query);

        query.setRequestChoice(Constants.MONITOR_AND_BOOK_ON_AVAILABLE);
        query.setBookings(bookings);

        int id = semInvo.requestServer(query);
        semInvo.monitorServer((response) -> {
            if (response.getServerStatus() == 200) {
                ClientUI.bookingIfVacancyAppearsResponse(query, response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, query.getMonitoringDuration() * 1000);

        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println("MONITORING COMPLETE");
        System.out.println(ClientUI.LINE_SEPARATOR);
    }
}
