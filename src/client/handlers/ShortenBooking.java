package client.handlers;

import utils.DateUtils;
import utils.UserInputValidator;
import constants.Constants;
import entity.BookingInfo;
import entity.ClientQuery;
import entity.DateTime;
import entity.ServerResponse;
import network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShortenBooking {
    private static ClientQuery query;

    /**
     * creates and sends view all facilities query to the server then waits
     * and handles the response. If no response is received before the timeout
     * send timeout message to the client
     *
     * @param network - udp communicator
     */
    public static void createAndSendMessage(Network network, Scanner scanner) {
        BookingInfo booking = getUserInputs(scanner);
        List<BookingInfo> payload = new ArrayList<>();
        payload.add(booking);

        query = new ClientQuery(Constants.SHORTEN_BOOKING, payload);
        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printChangeBookingSuccess(response);
            } else {
                Constants.PrintErrorMessage(response);
            }
        }, false, 5);
    }

    /**
     * parses users inputs and returns the BookingInfo requested
     */
    public static BookingInfo getUserInputs(Scanner scanner) {
        System.out.println(Constants.SEPARATOR);
        System.out.println(Constants.SHORTEN_BOOKING_HEADER);
        System.out.println(Constants.SEPARATOR);

        // Enter booking id
        System.out.println(Constants.ENTER_UUID);
        String UUID = scanner.nextLine();

        // Enter offset
        System.out.println(Constants.SHORTEN_BOOKING_PROMPT);
        System.out.println(Constants.ENTER_OFFSET);
        String[] date = scanner.nextLine().split(" ");

        if (date.length != 3 || !DateUtils.isValidDateTimeFormat(date[0], date[1], date[2])) {
            while (date.length != 3 || !DateUtils.isValidDateTimeFormat(date[0], date[1], date[2])) {
                System.out.println("Invalid date format");
                date = scanner.nextLine().split(" ");
            }
        }

        DateTime offset = new DateTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));

        System.out.println("your information is as follows");
        System.out.println("Booking id: " + UUID);
        System.out.println("Offset: " + offset.toNiceString());

        BookingInfo payload = new BookingInfo();
        payload.setUuid(UUID);
        payload.setOffset(offset);

        return payload;
    }

    /**
     * parses the server response and prints the relevant information
     *
     * @param response - response from the server
     */
    public static void printChangeBookingSuccess(ServerResponse response) {
        System.out.println(Constants.SEPARATOR);
        System.out.println("Booking successfully changed");
        BookingInfo booking = response.getInfos().get(0);
        String UUID = booking.getUuid();
        DateTime newStart = booking.getStartTime();
        DateTime newEnd = booking.getEndTime();
        System.out.println("New booking details:");
        System.out.println("Booking ID: " + UUID);
        System.out.println("Start: " + newStart.toNiceString());
        System.out.println("End: " + newEnd.toNiceString());
        System.out.println(Constants.SEPARATOR);
    }
}
