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

public class HandleBookFacility {
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

        // appending bookings
        getUserInputs(scanner, bookings);

        query = new ClientQuery();
        query.setType(Constants.BOOK_FACILITY);
        query.setBookings(bookings);

        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printBookingResponse(response);
            } else {
                Constants.PrintErrorMessage(response);
            }
        }, false, 5);
    }

    /**
     * parses the users inputs and retrieves the user's desired booking times
     *
     * @param bookings - BookingInfo list to be sent to the server
     */
    public static void getUserInputs(Scanner scanner, List<BookingInfo> bookings) {
        BookingInfo booking;

        System.out.println(Constants.SEPARATOR);
        System.out.println(Constants.BOOKING_FACILITY);
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

        // Enter Start Day
        System.out.print(Constants.ENTER_START_DAY);
        System.out.println();

        String startDay = scanner.nextLine();
        while (startDay.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_START_DAY);
            System.out.println();

            startDay = scanner.nextLine();
        }

        // Enter Start Time
        System.out.print(Constants.ENTER_START_TIME);
        System.out.println();

        String startTime = scanner.nextLine();
        while (startTime.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_START_TIME);
            System.out.println();

            startTime = scanner.nextLine();
        }

        // Enter End Day
        System.out.print(Constants.ENTER_END_DAY);
        System.out.println();

        String endDay = scanner.nextLine();
        while (endDay.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_END_DAY);
            System.out.println();

            endDay = scanner.nextLine();
        }

        // Enter End Time
        System.out.print(Constants.ENTER_END_TIME);
        System.out.println();

        String endTime = scanner.nextLine();
        while (endTime.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_END_TIME);
            System.out.println();

            endTime = scanner.nextLine();
        }

        DateTime d1 = new DateTime(Integer.parseInt(startDay), Integer.parseInt(startTime.substring(0, 2)), Integer.parseInt(startTime.substring(2, 4)));
        DateTime d2 = new DateTime(Integer.parseInt(endDay), Integer.parseInt(endTime.substring(0, 2)), Integer.parseInt(endTime.substring(2, 4)));
        booking = new BookingInfo(name.toUpperCase(), d1, d2);
        bookings.add(booking);
    }

    /**
     * parses the server response and prints the relevant information
     *
     * @param response - response from the server
     */
    public static void printBookingResponse(ServerResponse response) {
        Constants.PrintServerResponse();
        System.out.println("QUERY:");
//        String format = "%-40s%s%n";
//        System.out.printf(format, "Source:", query.getBooking().getName());
        System.out.println("=================================================");
        System.out.println("Booking Successful:");
        System.out.println(response.getInfos().get(0).getName() + ": " + response.getInfos().get(0).getStartTime().toNiceString() + " - " + response.getInfos().get(0).getEndTime().toNiceString());
        System.out.println("Booking ID: " + response.getInfos().get(0).getUuid());

        System.out.println("=================================================");
    }
}
