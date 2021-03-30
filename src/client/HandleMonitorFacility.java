package client;

import constants.Constants;
import entity.BookingInfo;
import entity.ClientQuery;
import entity.ServerResponse;
import network.Network;
import utils.UserInputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HandleMonitorFacility {

    /**
     * creates and sends view all facilities query to the server then waits
     * and handles the response. If no response is received before the timeout
     * send timeout message to the client
     *
     * @param network - udp communicator
     */
    public static void createAndSendMessage(Network network, Scanner scanner) {
        ClientQuery query;
        List<BookingInfo> bookings = new ArrayList<BookingInfo>();

        // get monitor duration
        query = new ClientQuery();
        getUserInputs(scanner, bookings, query);

        query.setType(Constants.MONITOR_BOOKING);
        query.setBookings(bookings);

        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printMonitoringResults(query, response);
            } else {
                Constants.PrintErrorMessage(response);
            }
        }, true, query.getMonitoringDuration());

        System.out.println(Constants.SEPARATOR);
        System.out.println("MONITORING COMPLETE");
        System.out.println(Constants.SEPARATOR);
    }

    public static void getUserInputs(Scanner scanner, List<BookingInfo> bookings, ClientQuery query) {
        BookingInfo booking = new BookingInfo();

        System.out.println(Constants.SEPARATOR);
        System.out.println(Constants.MONITOR_FACILITY_HEADER);
        System.out.println(Constants.SEPARATOR);

        // Enter Facility Name
        System.out.println(Constants.ENTER_FACILITIES_NAME);

        String name = scanner.nextLine();
        while (name.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_FACILITIES_NAME);
            System.out.println();

            name = scanner.nextLine();
        }

        // Enter monitor duration
        System.out.println(Constants.ENTER_MONITOR_DURATION);

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

    /**
     * parses the server response and prints the relevant information
     *
     * @param response - response from the server
     */
    public static void printMonitoringResults(ClientQuery query, ServerResponse response) {
        Constants.PrintServerResponse();
        System.out.println("QUERY:");
//        String format = "%-40s%s%n";
//        System.out.printf(format, "Source:", query.getBooking().getName());
        System.out.println("=================================================");
        System.out.println("NEW MONITORING UPDATE:\n");
        System.out.println(Constants.SERVICES_MAP.get(response.getType()));
        System.out.println(response.getInfos().get(0).getName() + ": " + response.getInfos().get(0).getStartTime().toNiceString() + " - " + response.getInfos().get(0).getEndTime().toNiceString());
        System.out.println("=================================================");
    }
}
