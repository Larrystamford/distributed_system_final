package main.java.client.handlers;

import main.java.client.ClientUI;
import main.java.utils.Constants;
import main.java.remote_objects.Common.Booking;
import main.java.remote_objects.Client.ClientQuery;
import main.java.remote_objects.Common.DayAndTime;
import main.java.remote_objects.Server.ServerResponse;
import main.java.network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FacilitiesAvailability {
    public static void createAndSendMessage(Network network, Scanner scanner) {
        ClientQuery query;
        List<Booking> bookings = new ArrayList<>();

        // appending bookings
        getUserInputs(scanner, bookings);

        query = new ClientQuery();
        query.setType(Constants.FACILITY_AVAILABILITY);
        query.setBookings(bookings);

        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printFacilitiesAvailability(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }

    public static void getUserInputs(Scanner scanner, List<Booking> bookings) {
        Booking booking;

        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println(ClientUI.FACILITIES_AVAILABILITY);
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

        boolean continueAdding = true;
        while (continueAdding) {
            System.out.print(ClientUI.ENTER_DAYS);
            System.out.println();

            String day = scanner.nextLine();

            while (day.length() == 0) {
                System.out.println(ClientUI.INVALID_INPUT);
                System.out.println();
                System.out.print(ClientUI.ENTER_DAYS);
                System.out.println();

                name = scanner.nextLine();
            }

            DayAndTime d1 = new DayAndTime(Integer.parseInt(day), 0, 0);
            DayAndTime d2 = new DayAndTime(Integer.parseInt(day), 23, 59);
            booking = new Booking(name.toUpperCase(), d1, d2);
            bookings.add(booking);

            System.out.println(ClientUI.CONTINUE_ENTER_DAYS);
            System.out.println(ClientUI.YES_1);
            System.out.println(ClientUI.NO_2);

            System.out.println();
            String continueAddingDays = scanner.nextLine();
            int continueAddingDaysValue = Integer.parseInt(continueAddingDays);

            if (continueAddingDaysValue != 1) {
                continueAdding = false;
            }
        }
    }

    public static void printFacilitiesAvailability(ServerResponse response) {
        ClientUI.ServerSuccessStatus();

        System.out.println("Facilities Available for Booking:");
        for (int i = 0; i < response.getInfos().size(); i++) {
            System.out.println(response.getInfos().get(i).getName() + ": " + response.getInfos().get(i).getStartTime().toNiceString() + " - " + response.getInfos().get(i).getEndTime().toNiceString());
        }
        if (response.getInfos().size() == 0) {
            System.out.println("No slots available on selected booking day/s");
        }
        System.out.println("=================================================");
    }
}
