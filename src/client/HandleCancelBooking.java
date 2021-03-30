package client;

import constants.Constants;
import entity.BookingInfo;
import entity.ClientQuery;
import entity.DateTime;
import entity.ServerResponse;
import network.Network;
import utils.DateUtils;
import utils.UserInputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class HandleCancelBooking {
    /**
     * creates and sends cancel booking query to the server
     *
     * @param network - udp communicator
     * @param scanner - scanner
     */
    public static void createAndSendMessage(Network network, Scanner scanner) {
        ClientQuery query;

        BookingInfo booking = getUserInputs(scanner);
        List<BookingInfo> payload = new ArrayList<>();
        payload.add(booking);

        query = new ClientQuery(Constants.CANCEL_BOOKING, payload);
        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printCancelBookingSuccess(response);
            } else {
                Constants.PrintErrorMessage(response);
            }
        }, false, 5);
    }

    /**
     * parses the users inputs, and retrieves the UUID
     */
    public static BookingInfo getUserInputs(Scanner scanner) {
        System.out.println(Constants.SEPARATOR);
        System.out.println(Constants.CANCEL_BOOKING_HEADER);
        System.out.println(Constants.SEPARATOR);

        // Enter booking id
        System.out.println(Constants.ENTER_UUID);
        String UUID = scanner.nextLine();

        while (UUID.length() == 0) {
            System.out.println(Constants.ERR_INPUT);
            System.out.println();
            System.out.print(Constants.ENTER_UUID);
            System.out.println();

            UUID = scanner.nextLine();
        }

        BookingInfo payload = new BookingInfo();
        payload.setUuid(UUID);
        return payload;
    }

    /**
     * parses the server response and prints the relevant information
     *
     * @param response - response from the server
     */
    public static void printCancelBookingSuccess(ServerResponse response) {
        Constants.PrintServerResponse();
        System.out.println("QUERY:");
//        String format = "%-40s%s%n";
//        System.out.printf(format, "Source:", query.getBooking().getName());
        System.out.println("=================================================");
        System.out.println("Booking cancelled:");
        System.out.println(response.getInfos().get(0).getName() + ": " + response.getInfos().get(0).getStartTime().toNiceString() + " - " + response.getInfos().get(0).getEndTime().toNiceString());
        System.out.println("Booking ID: " + response.getInfos().get(0).getUuid());

        System.out.println("=================================================");
    }
}
