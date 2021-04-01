package client;

import remote_objects.Client.ClientRequest;
import remote_objects.Common.Booking;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import utils.Constants;
import utils.DateUtils;
import utils.EntryChecker;

import java.util.List;
import java.util.Scanner;

public class ClientUI {

    // Main UI Constant
    public static final String LINE_SEPARATOR = "=====================================================================\n";
    public static final String STARTING_MESSAGE = "Distributed Facility Booking System";
    public static final String EXIT_SYSTEM_MESSAGE = "Good Bye!";
    public static final String SELECTIONS_MESSAGE = "Select service to execute:";
    public static final String INPUT_MESSAGE = "Your selection: ";
    public static final String UNKNOWN_INPUT_MESSAGE = "Invalid choice!";
    public static final String ERROR_MESSAGE = "Error: %s\n";
    public static final String VIEW_ALL_FACILITIES_MESSAGE = "1. View All Facilities";
    public static final String FACILITY_AVAILABILITY_MESSAGE = "2. Check Facility Availability";
    public static final String FACILITY_BOOKING_MESSAGE = "3. Book a Facility";
    public static final String OFFSET_BOOKING_MESSAGE = "4. Offset Booking";
    public static final String FACILITY_MONITORING_MESSAGE = "5. Facility Monitoring";
    public static final String SHORTEN_BOOKING_MESSAGE = "6. Shorten Booking";
    public static final String MONITOR_AND_BOOK_ON_VACANT = "7. Monitor and Book on Vacant";
    public static final String EXIT_MESSAGE = "8. Exit.";


    // CHECK FACILITIES CASE
    public static final String FACILITIES_AVAILABILITY = "FACILITIES AVAILABILITY";
    public static final String ENTER_FACILITIES_NAME = "ENTER THE FACILITY NAME";
    public static final String ENTER_DAYS = "ENTER DAY (1-7: Monday-Sunday)";
    public static final String CONTINUE_ENTER_DAYS = "CONTINUE ADDING MORE DAYS?";
    public static final String YES_1 = "1. Yes";
    public static final String NO_2 = "2. No";

    // BOOK CASE
    public static final String BOOKING_FACILITY = "BOOKING FACILITY";
    public static final String ENTER_START_DAY = "ENTER START DAY (1-7: Monday-Sunday)";
    public static final String ENTER_START_TIME = "ENTER START TIME (0000 - 2359)";
    public static final String ENTER_END_DAY = "ENTER END DAY (1-7: Monday-Sunday)";
    public static final String ENTER_END_TIME = "ENTER END TIME (0000 - 2359)";


    // OFFSET CASE
    public static final String CHANGE_BOOKING_HEADER = "CHANGE BOOKING";
    public static final String ENTER_UUID = "ENTER BOOKING ID";
    public static final String ADVANCE_OR_POSTPONE = "HOW WOULD YOU LIKE TO CHANGE YOUR BOOKING\n1. BRING FORWARD\n2. PUSH BACKWARD";
    public static final String ENTER_OFFSET = "ENTER OFFSET - format(day hour minute)";

    // MONITOR CASE
    public static final String MONITOR_FACILITY_HEADER = "MONITOR FACILITY";
    public static final String ENTER_MONITOR_DURATION = "HOW LONG WOULD YOU LIKE TO MONITOR THE FACILITY? (SECONDS)";

    // SHORTEN CASE
    public static final String SHORTEN_BOOKING_HEADER = "SHORTEN BOOKING";
    public static final String SHORTEN_BOOKING_PROMPT = "BY HOW MUCH WOULD YOU LIKE TO SHORTEN YOUR BOOKING?";

    // VACANCY CASE
    public static final String BOOK_ON_VACANCY_HEADER = "BOOK ON VACANCY";
    public static final String INVALID_INPUT = "Invalid input, must be non-empty!";


    public static void MenuMessage() {
        System.out.println(ClientUI.SELECTIONS_MESSAGE);
        System.out.println(ClientUI.VIEW_ALL_FACILITIES_MESSAGE);
        System.out.println(ClientUI.FACILITY_AVAILABILITY_MESSAGE);
        System.out.println(ClientUI.FACILITY_BOOKING_MESSAGE);
        System.out.println(ClientUI.OFFSET_BOOKING_MESSAGE);
        System.out.println(ClientUI.FACILITY_MONITORING_MESSAGE);
        System.out.println(ClientUI.SHORTEN_BOOKING_MESSAGE);
        System.out.println(ClientUI.MONITOR_AND_BOOK_ON_VACANT);
        System.out.println(ClientUI.EXIT_MESSAGE);
        System.out.println();
        System.out.print(ClientUI.INPUT_MESSAGE);
    }


    // Details of UI
    public static void ServerSuccessStatus() {
        System.out.println("\n==================================================");
        System.out.println("==================== Success =====================");
        System.out.println("==================================================");
    }


    public static void ServerErrorUI(ServerResponse response) {
        System.out.println("\n=================================================");
        System.out.println("=================== " + response.getServerStatus() + " ERROR ==================");
        System.out.println("=================================================");
        switch (response.getServerStatus()) {
            case 500:
                System.out.println("Server Error Try Again");
                System.out.println("\n=================================================");
                break;
            case 404:
                System.out.println("User provided input was not found in our system");
                System.out.println("\n=================================================");
                break;
            case 405:
                System.out.println("Changes clashes with existing bookings");
                System.out.println("\n=================================================");
            case 409:
                System.out.println("Time slot selected is unavailable, please try another time slot");
                System.out.println("\n=================================================");
                break;
            default:
                System.out.println("Unknown Error");
                System.out.println("\n=================================================");
                break;
        }

    }


    // client handlers
    public static void showFacilityAvailabilityResponse(ServerResponse response) {
        ClientUI.ServerSuccessStatus();

        System.out.println("Facilities Available for Booking:");
        for (int i = 0; i < response.getBookings().size(); i++) {
            System.out.println(response.getBookings().get(i).getName() + ": " + response.getBookings().get(i).getStartTime().getReadableFormat() + " - " + response.getBookings().get(i).getEndTime().getReadableFormat());
        }
        if (response.getBookings().size() == 0) {
            System.out.println("No slots available on selected booking day/s");
        }
        System.out.println("=================================================");
    }

    public static void bookingResponse(ServerResponse response) {
        ClientUI.ServerSuccessStatus();

        System.out.println("Booking Successful:");
        System.out.println(response.getBookings().get(0).getName() + ": " + response.getBookings().get(0).getStartTime().getReadableFormat() + " - " + response.getBookings().get(0).getEndTime().getReadableFormat());
        System.out.println("Booking ID: " + response.getBookings().get(0).getUuid());

        System.out.println("=================================================");
    }

    public static void bookingIfVacancyAppearsResponse(ServerResponse response) {
        ClientUI.ServerSuccessStatus();

        System.out.println("=================================================");
        System.out.println("VACANCY FOUND");
        System.out.println("BOOKING MADE:");
        System.out.println(response.getBookings().get(0).getName() + ": " + response.getBookings().get(0).getStartTime().getReadableFormat() + " - " + response.getBookings().get(0).getEndTime().getReadableFormat());
        System.out.println("Booking ID: " + response.getBookings().get(0).getUuid());
        System.out.println("=================================================\n");
    }

    public static void changeBookingResponse(ClientRequest query, ServerResponse response) {
        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println("Booking successfully changed");
        Booking booking = response.getBookings().get(0);
        String UUID = booking.getUuid();
        DayAndTime newStart = booking.getStartTime();
        DayAndTime newEnd = booking.getEndTime();
        System.out.println("New booking details:");
        System.out.println("Booking ID: " + UUID);
        System.out.println("Start: " + newStart.getReadableFormat());
        System.out.println("End: " + newEnd.getReadableFormat());
        System.out.println(ClientUI.LINE_SEPARATOR);
    }

    public static void changeBookingResponse(ServerResponse response) {
        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println("Booking successfully changed");
        Booking booking = response.getBookings().get(0);
        String UUID = booking.getUuid();
        DayAndTime newStart = booking.getStartTime();
        DayAndTime newEnd = booking.getEndTime();
        System.out.println("New booking details:");
        System.out.println("Booking ID: " + UUID);
        System.out.println("Start: " + newStart.getReadableFormat());
        System.out.println("End: " + newEnd.getReadableFormat());
        System.out.println(ClientUI.LINE_SEPARATOR);
    }

    public static void monitoringResponse(ServerResponse response) {
        ClientUI.ServerSuccessStatus();

        System.out.println("=================================================");
        System.out.println("NEW MONITORING UPDATE:\n");
        System.out.println(Constants.SERVICES_MAP.get(response.getRequestChoice()));
        System.out.println(response.getBookings().get(0).getName() + ": " + response.getBookings().get(0).getStartTime().getReadableFormat() + " - " + response.getBookings().get(0).getEndTime().getReadableFormat());
        System.out.println("=================================================");
    }


    public static void listFacilitiesResponse(ServerResponse response) {
        ClientUI.ServerSuccessStatus();
        System.out.println("Facilities Availability:");
        for (int i = 0; i < response.getBookings().size(); i++) {
            System.out.println("[" + i + "] " + response.getBookings().get(i).getName());
        }
        System.out.println("=================================================");

    }

    // get inputs from user
    public static void getListFacilitiesInput(Scanner scanner, List<Booking> bookings) {
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

    public static void getMakeBookingInput(Scanner scanner, List<Booking> bookings) {
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

    public static void getMonitorFacilityInput(Scanner scanner, List<Booking> bookings, ClientRequest query) {
        Booking booking = new Booking();

        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println(ClientUI.MONITOR_FACILITY_HEADER);
        System.out.println(ClientUI.LINE_SEPARATOR);

        // Enter Facility Name
        System.out.println(ClientUI.ENTER_FACILITIES_NAME);

        String name = scanner.nextLine();
        while (name.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_FACILITIES_NAME);
            System.out.println();

            name = scanner.nextLine();
        }

        // Enter monitor duration
        System.out.println(ClientUI.ENTER_MONITOR_DURATION);

        String duration = scanner.nextLine();
        while (!EntryChecker.isAppropriateInteger(duration, 0, (int) Double.POSITIVE_INFINITY)) {
            System.out.println("invalid input");
            duration = scanner.nextLine();
        }

        System.out.println();
        query.setMonitoringDuration(Integer.parseInt(duration));
        booking.setName(name.toUpperCase());
        bookings.add(booking);
    }

    public static void getBookOnVacancyInput(Scanner scanner, List<Booking> bookings, ClientRequest query) {
        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println(ClientUI.BOOK_ON_VACANCY_HEADER);
        System.out.println(ClientUI.LINE_SEPARATOR);

        // Enter Facility Name
        System.out.println(ClientUI.ENTER_FACILITIES_NAME);

        String name = scanner.nextLine();
        while (name.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_FACILITIES_NAME);
            System.out.println();

            name = scanner.nextLine().toUpperCase();
        }

        // Enter Start Day
        System.out.println(ClientUI.ENTER_START_DAY);

        String startDay = scanner.nextLine();
        while (startDay.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_START_DAY);
            System.out.println();

            startDay = scanner.nextLine();
        }

        // Enter Start Time
        System.out.println(ClientUI.ENTER_START_TIME);

        String startTime = scanner.nextLine();
        while (startTime.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_START_TIME);
            System.out.println();

            startTime = scanner.nextLine();
        }

        // Enter End Day
        System.out.println(ClientUI.ENTER_END_DAY);

        String endDay = scanner.nextLine();
        while (endDay.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_END_DAY);
            System.out.println();

            endDay = scanner.nextLine();
        }

        // Enter End Time
        System.out.println(ClientUI.ENTER_END_TIME);

        String endTime = scanner.nextLine();
        while (endTime.length() == 0) {
            System.out.println(ClientUI.INVALID_INPUT);
            System.out.println();
            System.out.print(ClientUI.ENTER_END_TIME);
            System.out.println();

            endTime = scanner.nextLine();
        }

        // Enter Monitor Duration
        System.out.println(ClientUI.ENTER_MONITOR_DURATION);

        String duration = scanner.nextLine();
        while (!EntryChecker.isAppropriateInteger(duration, 0, (int) Double.POSITIVE_INFINITY)) {
            System.out.println("invalid input");
            duration = scanner.nextLine();
        }
        // Create Potential Booking And Set Monitor Duration
        System.out.println();
        DayAndTime d1 = new DayAndTime(Integer.parseInt(startDay), Integer.parseInt(startTime.substring(0, 2)), Integer.parseInt(startTime.substring(2, 4)));
        DayAndTime d2 = new DayAndTime(Integer.parseInt(endDay), Integer.parseInt(endTime.substring(0, 2)), Integer.parseInt(endTime.substring(2, 4)));
        Booking booking = new Booking(name.toUpperCase(), d1, d2);
        bookings.add(booking);
        query.setMonitoringDuration(Integer.parseInt(duration));
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
        if (!EntryChecker.isAppropriateInteger(choice, 1, 2)) {
            while (!EntryChecker.isAppropriateInteger(choice, 1, 2)) {
                System.out.println("Invalid input, choice can only be 1 or 2");
                choice = scanner.nextLine();
            }
        }

        // Enter offset
        System.out.println(ClientUI.ENTER_OFFSET);
        String[] date = scanner.nextLine().split(" ");

        if (date.length != 3 || !DateUtils.checkDate(date[0], date[1], date[2])) {
            while (date.length != 3 || !DateUtils.checkDate(date[0], date[1], date[2])) {
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
        System.out.println("Offset: " + offset.getReadableFormat());

        Booking payload = new Booking();
        payload.setUuid(UUID);
        payload.setOffset(offset);

        return payload;
    }
    public static Booking getOffsetBookingInput(Scanner scanner) {
        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println(ClientUI.CHANGE_BOOKING_HEADER);
        System.out.println(ClientUI.LINE_SEPARATOR);

        // Enter booking id
        System.out.println(ClientUI.ENTER_UUID);
        String UUID = scanner.nextLine();

        // Enter advance or postpone choice
        System.out.println(ClientUI.ADVANCE_OR_POSTPONE);
        String choice = scanner.nextLine();
        if (!EntryChecker.isAppropriateInteger(choice, 1, 2)) {
            while (!EntryChecker.isAppropriateInteger(choice, 1, 2)) {
                System.out.println("Invalid input, choice can only be 1 or 2");
                choice = scanner.nextLine();
            }
        }

        // Enter offset
        System.out.println(ClientUI.ENTER_OFFSET);
        String[] date = scanner.nextLine().split(" ");

        if (date.length != 3 || !DateUtils.checkDate(date[0], date[1], date[2])) {
            while (date.length != 3 || !DateUtils.checkDate(date[0], date[1], date[2])) {
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
        System.out.println("Offset: " + offset.getReadableFormat());

        Booking payload = new Booking();
        payload.setUuid(UUID);
        payload.setOffset(offset);

        return payload;
    }

    public static Booking getShortenBookingInput(Scanner scanner) {
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

        if (date.length != 3 || !DateUtils.checkDate(date[0], date[1], date[2])) {
            while (date.length != 3 || !DateUtils.checkDate(date[0], date[1], date[2])) {
                System.out.println("Invalid date format");
                date = scanner.nextLine().split(" ");
            }
        }

        DayAndTime offset = new DayAndTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));

        System.out.println("your information is as follows");
        System.out.println("Booking id: " + UUID);
        System.out.println("Offset: " + offset.getReadableFormat());

        Booking payload = new Booking();
        payload.setUuid(UUID);
        payload.setOffset(offset);

        return payload;
    }
}