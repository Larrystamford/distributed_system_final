package client.handlers;

import client.ClientUI;
import remote_objects.Common.Booking;
import utils.Constants;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FacilityBooking {

    public static void createAndSendMessage(Network network, Scanner scanner) {
        ClientRequest query;
        List<Booking> bookings = new ArrayList<Booking>();

        // appending bookings
        getUserInputs(scanner, bookings);

        query = new ClientRequest();
        query.setRequestChoice(Constants.FACILITY_BOOKING);
        query.setBookings(bookings);

        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getServerStatus() == 200) {
                printBookingResponse(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }

    public static void getUserInputs(Scanner scanner, List<Booking> bookings) {
        Booking booking;

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
        booking = new Booking(name.toUpperCase(), d1, d2);
        bookings.add(booking);
    }

    public static void printBookingResponse(ServerResponse response) {
        ClientUI.ServerSuccessStatus();

        System.out.println("Booking Successful:");
        System.out.println(response.getBookings().get(0).getName() + ": " + response.getBookings().get(0).getStartTime().toNiceString() + " - " + response.getBookings().get(0).getEndTime().toNiceString());
        System.out.println("Booking ID: " + response.getBookings().get(0).getUuid());

        System.out.println("=================================================");
    }
}
