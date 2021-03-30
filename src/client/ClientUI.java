package client;

import remote_objects.Server.ServerResponse;

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
        System.out.println("\n=================================================");
        System.out.println("==================== Success =====================");
        System.out.println("==================================================");
    }


    public static void ServerErrorUI(ServerResponse response) {
        System.out.println("\n=================================================");
        System.out.println("=================== " + response.getStatus() + " ERROR ================");
        System.out.println("=================================================");
        switch (response.getStatus()) {
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
}
