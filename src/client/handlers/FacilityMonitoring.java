package client.handlers;

import client.ClientUI;
import utils.Constants;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import remote_objects.Server.ServerResponse;
import semantics.Semantics;
import utils.UserInputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FacilityMonitoring {

    public static void createAndSendMessage(Semantics semInvo, Scanner scanner) {
        ClientRequest query;
        List<Booking> bookings = new ArrayList<Booking>();

        // get monitor duration
        query = new ClientRequest();
        getUserInputs(scanner, bookings, query);

        query.setRequestChoice(Constants.FACILITY_MONITORING);
        query.setBookings(bookings);

        int id = semInvo.requestServer(query);
        semInvo.monitorServer((response) -> {
            if (response.getServerStatus() == 200) {
                printMonitoringResults(query, response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, true, query.getMonitoringDuration() * 1000);

        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println("MONITORING COMPLETE");
        System.out.println(ClientUI.LINE_SEPARATOR);
    }

    public static void getUserInputs(Scanner scanner, List<Booking> bookings, ClientRequest query) {
        Booking booking = new Booking();

        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println(ClientUI.MONITOR_FACILITY_HEADER);
        System.out.println(ClientUI.LINE_SEPARATOR);

        // Enter Facility Name
        System.out.println(ClientUI.ENTER_FACILITIES_NAME);

        String name = scanner.nextLine();
        while (name.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_FACILITIES_NAME);
            System.out.println();

            name = scanner.nextLine();
        }

        // Enter monitor duration
        System.out.println(ClientUI.ENTER_MONITOR_DURATION);

        String duration = scanner.nextLine();
        while (!UserInputValidator.isNumericAndWithinRange(duration, 0, (int) Double.POSITIVE_INFINITY)) {
            System.out.println("invalid input");
            duration = scanner.nextLine();
        }

        System.out.println();
        query.setMonitoringDuration(Integer.parseInt(duration));
        booking.setName(name.toUpperCase());
        bookings.add(booking);
    }

    public static void printMonitoringResults(ClientRequest query, ServerResponse response) {
        ClientUI.ServerSuccessStatus();

        System.out.println("=================================================");
        System.out.println("NEW MONITORING UPDATE:\n");
        System.out.println(Constants.SERVICES_MAP.get(response.getRequestChoice()));
        System.out.println(response.getBookings().get(0).getName() + ": " + response.getBookings().get(0).getStartTime().toNiceString() + " - " + response.getBookings().get(0).getEndTime().toNiceString());
        System.out.println("=================================================");
    }
}
