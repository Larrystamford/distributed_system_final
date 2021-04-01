package client.handlers;

import client.ClientUI;
import utils.DateUtils;
import utils.UserInputValidator;
import utils.Constants;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.DayAndTime;
import semantics.Semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OffsetBooking {
    private static ClientRequest query;

    public static void createAndSendMessage(Semantics semInvo, Scanner scanner) {
        Booking booking = getUserInputs(scanner);
        List<Booking> payload = new ArrayList<>();
        payload.add(booking);

        query = new ClientRequest(Constants.OFFSET_BOOKING, payload);
        int id = semInvo.requestServer(query);
        semInvo.receiveResponse(id, (response) -> {
            if (response.getServerStatus() == 200) {
                ClientUI.changeBookingResponse(query, response);
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
}
