package client.handlers;

import client.ClientUI;
import utils.Constants;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientQuery;
import remote_objects.Server.ServerResponse;
import network.Network;
import utils.UserInputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FacilityMonitoring {

    public static void createAndSendMessage(Network network, Scanner scanner) {
        ClientQuery query;
        List<Booking> bookings = new ArrayList<Booking>();

        // get monitor duration
        query = new ClientQuery();
        getUserInputs(scanner, bookings, query);

        query.setType(Constants.FACILITY_MONITORING);
        query.setBookings(bookings);

        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printMonitoringResults(query, response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, true, query.getMonitoringDuration());

        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println("MONITORING COMPLETE");
        System.out.println(ClientUI.LINE_SEPARATOR);
    }

    public static void getUserInputs(Scanner scanner, List<Booking> bookings, ClientQuery query) {
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
        booking.setName(name);
        bookings.add(booking);
    }

    public static void printMonitoringResults(ClientQuery query, ServerResponse response) {
        ClientUI.ServerSuccessStatus();

        System.out.println("=================================================");
        System.out.println("NEW MONITORING UPDATE:\n");
        System.out.println(Constants.SERVICES_MAP.get(response.getType()));
        System.out.println(response.getInfos().get(0).getName() + ": " + response.getInfos().get(0).getStartTime().toNiceString() + " - " + response.getInfos().get(0).getEndTime().toNiceString());
        System.out.println("=================================================");
    }
}
