package client;

import constants.Constants;
import entity.BookingInfo;
import entity.ClientQuery;
import entity.DateTime;
import entity.ServerResponse;
import network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HandleCheckFacilitiesAvailability {
    /**
     * creates and sends view all facilities query to the server then waits
     * and handles the response. If no response is received before the timeout
     * send timeout message to the client
     *
     * @param network - udp communicator
     */
    public static void createAndSendMessage(Network network, Scanner scanner) {
        ClientQuery query;
        List<BookingInfo> bookings = new ArrayList<>();

        // appending bookings
        getUserInputs(scanner, bookings);

        query = new ClientQuery();
        query.setType(Constants.CHECK_FACILITIES_AVAILABILITY);
        query.setBookings(bookings);

        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printFacilitiesAvailability(response);
            } else {
                Constants.PrintErrorMessage(response);
            }
        }, false, 5);
    }

    /**
     * parses the users inputs and retrieves facilities and days that the user is interested in
     *
     * @param bookings - BookingInfo list to be sent to the server
     */
    public static void getUserInputs(Scanner scanner, List<BookingInfo> bookings) {
        BookingInfo booking;

        System.out.println(Constants.SEPARATOR);
        System.out.println(Constants.FACILITIES_AVAILABILITY);
        System.out.println(Constants.SEPARATOR);

        // Enter Facility Name
        System.out.print(Constants.ENTER_FACILITIES_NAME);
        System.out.println();

        String name = scanner.nextLine();
        while (name.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_FACILITIES_NAME);
            System.out.println();

            name = scanner.nextLine();
        }

        // Enter Interested Days
        boolean continueAdding = true;
        while (continueAdding) {
            System.out.print(Constants.ENTER_DAYS);
            System.out.println();

            String day = scanner.nextLine();

            while (day.length() == 0) {
                System.out.println(Constants.ERR_INPUT);
                System.out.println();
                System.out.print(Constants.ENTER_DAYS);
                System.out.println();

                name = scanner.nextLine();
            }

            DateTime d1 = new DateTime(Integer.parseInt(day), 0, 0);
            DateTime d2 = new DateTime(Integer.parseInt(day), 23, 59);
            booking = new BookingInfo(name.toUpperCase(), d1, d2);
            bookings.add(booking);

            System.out.println(Constants.CONTINUE_ENTER_DAYS);
            System.out.println(Constants.YES_OPTION);
            System.out.println(Constants.NO_OPTION);

            System.out.println();
            String continueAddingDays = scanner.nextLine();
            int continueAddingDaysValue = Integer.parseInt(continueAddingDays);

            if (continueAddingDaysValue != 1) {
                continueAdding = false;
            }
        }
    }

    /**
     * parses the server response and prints the relevant information
     *
     * @param response - response from the server
     */
    public static void printFacilitiesAvailability(ServerResponse response) {
        Constants.PrintServerResponse();
        System.out.println("QUERY:");
//        String format = "%-40s%s%n";
//        System.out.printf(format, "Source:", query.getBooking().getName());
        System.out.println("=================================================");
        System.out.println("Facilities Availability:");
        for (int i = 0; i < response.getInfos().size(); i++) {
            System.out.println(response.getInfos().get(i).getName() + ": " + response.getInfos().get(i).getStartTime().toNiceString() + " - " + response.getInfos().get(i).getEndTime().toNiceString());
        }
        if (response.getInfos().size() == 0) {
            System.out.println("No slots available on selected booking day/s");
        }
        System.out.println("=================================================");
    }
}
