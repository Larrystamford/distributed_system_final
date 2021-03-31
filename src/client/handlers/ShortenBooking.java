package client.handlers;

import client.ClientUI;
import utils.DateUtils;
import utils.Constants;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import semantics.Semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShortenBooking {
    private static ClientRequest query;

    public static void createAndSendMessage(Semantics semInvo, Scanner scanner) {
        Booking booking = getUserInputs(scanner);
        List<Booking> payload = new ArrayList<>();
        payload.add(booking);

        query = new ClientRequest(Constants.SHORTEN_BOOKING, payload);
        int id = semInvo.requestServer(query);
        semInvo.receiveResponse(id, (response) -> {
            if (response.getServerStatus() == 200) {
                printChangeBookingSuccess(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }


    public static Booking getUserInputs(Scanner scanner) {
        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println(ClientUI.SHORTEN_BOOKING_HEADER);
        System.out.println(ClientUI.LINE_SEPARATOR);

        // Enter booking id
        System.out.println(ClientUI.ENTER_UUID);
        String UUID = scanner.nextLine();

        // Enter offset
        System.out.println(ClientUI.SHORTEN_BOOKING_PROMPT);
        System.out.println(ClientUI.ENTER_OFFSET);
        String[] date = scanner.nextLine().split(" ");

        if (date.length != 3 || !DateUtils.isValidDateTimeFormat(date[0], date[1], date[2])) {
            while (date.length != 3 || !DateUtils.isValidDateTimeFormat(date[0], date[1], date[2])) {
                System.out.println("Invalid date format");
                date = scanner.nextLine().split(" ");
            }
        }

        DayAndTime offset = new DayAndTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));

        System.out.println("your information is as follows");
        System.out.println("Booking id: " + UUID);
        System.out.println("Offset: " + offset.toNiceString());

        Booking payload = new Booking();
        payload.setUuid(UUID);
        payload.setOffset(offset);

        return payload;
    }


    public static void printChangeBookingSuccess(ServerResponse response) {
        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println("Booking successfully changed");
        Booking booking = response.getBookings().get(0);
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
