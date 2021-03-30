package client.handlers;

import client.ClientUI;
import utils.Constants;
import remote_objects.Client.ClientQuery;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FacilityBooking {
    /**
     * creates and sends view all facilities query to the server then waits
     * and handles the response. If no response is received before the timeout
     * send timeout message to the client
     *
     * @param network - udp communicator
     */
    public static void createAndSendMessage(Network network, Scanner scanner) {
        ClientQuery query;
        List<remote_objects.Common.FacilityBooking> bookings = new ArrayList<remote_objects.Common.FacilityBooking>();

        // appending bookings
        getUserInputs(scanner, bookings);

        query = new ClientQuery();
        query.setType(Constants.FACILITY_BOOKING);
        query.setBookings(bookings);

        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printBookingResponse(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }

    /**
     * parses the users inputs and retrieves the user's desired booking times
     *
     * @param bookings - BookingInfo list to be sent to the server
     */
    public static void getUserInputs(Scanner scanner, List<remote_objects.Common.FacilityBooking> bookings) {
        remote_objects.Common.FacilityBooking booking;

        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println(ClientUI.BOOKING_FACILITY);
        System.out.println(ClientUI.LINE_SEPARATOR);

        // Enter Facility Name
        System.out.print(ClientUI.ENTER_FACILITIES_NAME);
        System.out.println();

        String name = scanner.nextLine();
        while (name.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_FACILITIES_NAME);
            System.out.println();

            name = scanner.nextLine();
        }

        // Enter Start Day
        System.out.print(ClientUI.ENTER_START_DAY);
        System.out.println();

        String startDay = scanner.nextLine();
        while (startDay.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_START_DAY);
            System.out.println();

            startDay = scanner.nextLine();
        }

        // Enter Start Time
        System.out.print(ClientUI.ENTER_START_TIME);
        System.out.println();

        String startTime = scanner.nextLine();
        while (startTime.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_START_TIME);
            System.out.println();

            startTime = scanner.nextLine();
        }

        // Enter End Day
        System.out.print(ClientUI.ENTER_END_DAY);
        System.out.println();

        String endDay = scanner.nextLine();
        while (endDay.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_END_DAY);
            System.out.println();

            endDay = scanner.nextLine();
        }

        // Enter End Time
        System.out.print(ClientUI.ENTER_END_TIME);
        System.out.println();

        String endTime = scanner.nextLine();
        while (endTime.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_END_TIME);
            System.out.println();

            endTime = scanner.nextLine();
        }

        DayAndTime d1 = new DayAndTime(Integer.parseInt(startDay), Integer.parseInt(startTime.substring(0, 2)), Integer.parseInt(startTime.substring(2, 4)));
        DayAndTime d2 = new DayAndTime(Integer.parseInt(endDay), Integer.parseInt(endTime.substring(0, 2)), Integer.parseInt(endTime.substring(2, 4)));
        booking = new remote_objects.Common.FacilityBooking(name.toUpperCase(), d1, d2);
        bookings.add(booking);
    }

    /**
     * parses the server response and prints the relevant information
     *
     * @param response - response from the server
     */
    public static void printBookingResponse(ServerResponse response) {
        ClientUI.ServerSuccessStatus();
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
