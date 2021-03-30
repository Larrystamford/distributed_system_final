package main.java.client.handlers;

import main.java.client.ClientUI;
import main.java.utils.DateUtils;
import main.java.utils.UserInputValidator;
import main.java.utils.Constants;
import main.java.remote_objects.Common.Booking;
import main.java.remote_objects.Client.ClientQuery;
import main.java.remote_objects.Common.DayAndTime;
import main.java.remote_objects.Server.ServerResponse;
import main.java.network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OffsetBooking {
    private static ClientQuery query;

    public static void createAndSendMessage(Network network, Scanner scanner) {
        Booking booking = getUserInputs(scanner);
        List<Booking> payload = new ArrayList<>();
        payload.add(booking);

        query = new ClientQuery(Constants.OFFSET_BOOKING, payload);
        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printChangeBookingSuccess(query, response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }


    public static Booking getUserInputs(Scanner scanner) {
        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println(ClientUI.CHANGE_BOOKING_HEADER);
        System.out.println(ClientUI.LINE_SEPARATOR);

        // Enter booking id
        System.out.println(ClientUI.ENTER_UUID);
        String UUID = scanner.nextLine();

        // Enter advance or postpone choice
        System.out.println(ClientUI.ADVANCE_OR_POSTPONE);
        String choice = scanner.nextLine();
        if (!UserInputValidator.isNumericAndWithinRange(choice, 1, 2)) {
            while (!UserInputValidator.isNumericAndWithinRange(choice, 1, 2)) {
                System.out.println("Invalid input, choice can only be 1 or 2");
                choice = scanner.nextLine();
            }
        }

        // Enter offset
        System.out.println(ClientUI.ENTER_OFFSET);
        String[] date = scanner.nextLine().split(" ");

        if (date.length != 3 || !DateUtils.isValidDateTimeFormat(date[0], date[1], date[2])) {
            while (date.length != 3 || !DateUtils.isValidDateTimeFormat(date[0], date[1], date[2])) {
                System.out.println("Invalid date format");
                date = scanner.nextLine().split(" ");
            }
        }

        DayAndTime offset;
        if (choice.equals("2")) {
            offset = new DayAndTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
        } else {
            offset = new DayAndTime(-Integer.parseInt(date[0]), -Integer.parseInt(date[1]), -Integer.parseInt(date[2]));
        }

        System.out.println("your information is as follows");
        System.out.println("Booking id: " + UUID);
        System.out.println("Choice: " + choice);
        System.out.println("Offset: " + offset.toNiceString());

        Booking payload = new Booking();
        payload.setUuid(UUID);
        payload.setOffset(offset);

        return payload;
    }


    public static void printChangeBookingSuccess(ClientQuery query, ServerResponse response) {
        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println("Booking successfully changed");
        Booking booking = response.getInfos().get(0);
        String UUID = booking.getUuid();
        DayAndTime newStart = booking.getStartTime();
        DayAndTime newEnd = booking.getEndTime();
        System.out.println("New booking details:");
        System.out.println("Booking ID: " + UUID);
        System.out.println("Start: " + newStart.toNiceString());
        System.out.println("End: " + newEnd.toNiceString());
        System.out.println(ClientUI.LINE_SEPARATOR);
    }
}
