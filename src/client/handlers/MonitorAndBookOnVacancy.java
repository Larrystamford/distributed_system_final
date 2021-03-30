package client.handlers;

import constants.Constants;
import entity.BookingInfo;
import entity.ClientQuery;
import entity.DateTime;
import entity.ServerResponse;
import network.Network;
import utils.UserInputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MonitorAndBookOnVacancy {
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

        query.setType(Constants.BOOK_ON_VACANCY);
        query.setBookings(bookings);

        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printMonitoringResults(response);
            } else {
                Constants.PrintErrorMessage(response);
            }
        }, false, query.getMonitoringDuration());

        System.out.println(Constants.SEPARATOR);
        System.out.println("MONITORING COMPLETE");
        System.out.println(Constants.SEPARATOR);
    }

    /**
     * parses the users inputs and retrieves the user's desired booking times and monitoring duration
     *
     * @param bookings - BookingInfo list to be sent to the server
     */
    public static void getUserInputs(Scanner scanner, List<BookingInfo> bookings, ClientQuery query) {
        System.out.println(Constants.SEPARATOR);
        System.out.println(Constants.BOOK_ON_VACANCY_HEADER);
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

        // Enter Start Day
        System.out.println(Constants.ENTER_START_DAY);

        String startDay = scanner.nextLine();
        while (startDay.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_START_DAY);
            System.out.println();

            startDay = scanner.nextLine();
        }

        // Enter Start Time
        System.out.println(Constants.ENTER_START_TIME);

        String startTime = scanner.nextLine();
        while (startTime.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_START_TIME);
            System.out.println();

            startTime = scanner.nextLine();
        }

        // Enter End Day
        System.out.println(Constants.ENTER_END_DAY);

        String endDay = scanner.nextLine();
        while (endDay.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_END_DAY);
            System.out.println();

            endDay = scanner.nextLine();
        }

        // Enter End Time
        System.out.println(Constants.ENTER_END_TIME);

        String endTime = scanner.nextLine();
        while (endTime.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_END_TIME);
            System.out.println();

            endTime = scanner.nextLine();
        }

        // Enter Monitor Duration
        System.out.println(Constants.ENTER_MONITOR_DURATION);

        String duration = scanner.nextLine();
        while (!UserInputValidator.isNumericAndWithinRange(duration, 0, (int) Double.POSITIVE_INFINITY)) {
            System.out.println("invalid input");
            duration = scanner.nextLine();
        }
        // Create Potential Booking And Set Monitor Duration
        System.out.println();
        DateTime d1 = new DateTime(Integer.parseInt(startDay), Integer.parseInt(startTime.substring(0, 2)), Integer.parseInt(startTime.substring(2, 4)));
        DateTime d2 = new DateTime(Integer.parseInt(endDay), Integer.parseInt(endTime.substring(0, 2)), Integer.parseInt(endTime.substring(2, 4)));
        BookingInfo booking = new BookingInfo(name.toUpperCase(), d1, d2);
        bookings.add(booking);
        query.setMonitoringDuration(Integer.parseInt(duration));
    }

    /**
     * parses the server response and prints the relevant information
     *
     * @param response - response from the server
     */
    public static void printMonitoringResults(ServerResponse response) {
        Constants.PrintServerResponse();
        System.out.println("QUERY:");
//        String format = "%-40s%s%n";
//        System.out.printf(format, "Source:", query.getBooking().getName());
        System.out.println("=================================================");
        System.out.println("VACANCY FOUND");
        System.out.println("BOOKING MADE:");
        System.out.println(response.getInfos().get(0).getName() + ": " + response.getInfos().get(0).getStartTime().toNiceString() + " - " + response.getInfos().get(0).getEndTime().toNiceString());
        System.out.println("Booking ID: " + response.getInfos().get(0).getUuid());
        System.out.println("=================================================\n");
    }
}
