package client;

import constants.Constants;
import entity.ServerResponse;

import java.util.HashMap;

public class ClientUI {


    // Main UI Constant
    public static final String ERR_MSG = "Error: %s\n";
    public static final String SUCCESS_MSG = "SUCCESS!";
    public static final String SEPARATOR = "================================================================================\n";
    public static final String WELCOME_MSG = "Welcome to NTU's Facility Booking System";
    public static final String EXIT_MSG = "Thank you for using our system!";
    public static final String SELECTION_SVC_MSG = "Select the service you want to do:";
    public static final String VIEW_ALL_FACILITIES_SVC_MSG = "1. View all facilities.";
    public static final String CHECK_FACILITIES_AVAILABILITY_SVC_MSG = "2. Check facility availability.";
    public static final String BOOK_FACILITY_SVC_MSG = "3. Book facility.";
    public static final String CHANGE_BOOKING_SVC_MSG = "4. Change booking.";
    public static final String MONITOR_UPDATE_SVC_MSG = "5. Monitor booking.";
    public static final String CANCEL_BOOKING_SVC_MSG = "6. Cancel booking.";
    public static final String EXTEND_BOOKING_SVC_MSG = "7. Extend booking.";
    public static final String SHORTEN_BOOKING_SVC_MSG = "8. Shorten booking";
    public static final String WATCH_BOOKING_SVC_MSG = "9. Watch booking";
    public static final String EXIT_SVC_MSG = "10. Exit.";
    public static final String SANITY_CHECK = "11. SANITY CHECK";
    public static final String CHOICE_SVC_MSG = "Your choice: ";
    public static final String UNRECOGNIZE_SVC_MSG = "Sorry we cannot recognize your service choice!";


    public static void MainMenuSelectionMessage() {
        System.out.println(ClientUI.SELECTION_SVC_MSG);
        System.out.println(ClientUI.VIEW_ALL_FACILITIES_SVC_MSG);
        System.out.println(ClientUI.CHECK_FACILITIES_AVAILABILITY_SVC_MSG);
        System.out.println(ClientUI.BOOK_FACILITY_SVC_MSG);
        System.out.println(ClientUI.CHANGE_BOOKING_SVC_MSG);
        System.out.println(ClientUI.MONITOR_UPDATE_SVC_MSG);
        System.out.println(ClientUI.CANCEL_BOOKING_SVC_MSG);
        System.out.println(ClientUI.EXTEND_BOOKING_SVC_MSG);
        System.out.println(ClientUI.SHORTEN_BOOKING_SVC_MSG);
        System.out.println(ClientUI.WATCH_BOOKING_SVC_MSG);
        System.out.println(ClientUI.EXIT_SVC_MSG);
        System.out.println(ClientUI.SANITY_CHECK);
        System.out.println();
        System.out.print(ClientUI.CHOICE_SVC_MSG);
    }

    // COMMON YES AND NO PRINTS
    public static final String YES_OPTION = "1. Yes";
    public static final String NO_OPTION = "2. No";

    // CHECK FACILITIES AVAILABILITY UI
    public static final String FACILITIES_AVAILABILITY = "FACILITIES AVAILABILITY";
    public static final String ENTER_FACILITIES_NAME = "ENTER THE FACILITY NAME";
    public static final String ENTER_DAYS = "ENTER DAY (1-7: Monday-Sunday)";
    public static final String CONTINUE_ENTER_DAYS = "CONTINUE ADDING MORE DAYS?";

    // BOOK FACILITY UI
    public static final String BOOKING_FACILITY = "BOOKING FACILITY";
    public static final String ENTER_START_DAY = "ENTER START DAY (1-7: Monday-Sunday)";
    public static final String ENTER_START_TIME = "ENTER START TIME (0000 - 2359)";
    public static final String ENTER_END_DAY = "ENTER END DAY (1-7: Monday-Sunday)";
    public static final String ENTER_END_TIME = "ENTER END TIME (0000 - 2359)";


    // CHANGE BOOKING UI
    public static final String CHANGE_BOOKING_HEADER = "CHANGE BOOKING";
    public static final String ENTER_UUID = "ENTER BOOKING ID";
    public static final String ADVANCE_OR_POSTPONE = "HOW WOULD YOU LIKE TO CHANGE YOUR BOOKING\n1. BRING FORWARD\n2. PUSH BACKWARD";
    public static final String ENTER_OFFSET = "ENTER OFFSET - format(day hour minute)";

    // MONITOR BOOKING UI
    public static final String MONITOR_FACILITY_HEADER = "MONITOR FACILITY";
    public static final String ENTER_MONITOR_DURATION = "HOW LONG WOULD YOU LIKE TO MONITOR THE FACILITY? (SECONDS)";


    // EXTEND BOOKING UI
    public static final String SHORTEN_BOOKING_HEADER = "SHORTEN BOOKING";
    public static final String SHORTEN_BOOKING_PROMPT = "BY HOW MUCH WOULD YOU LIKE TO SHORTEN YOUR BOOKING?";


    // BOOK ON VACANCY UI
    public static final String BOOK_ON_VACANCY_HEADER = "BOOK ON VACANCY";


    // Error UI Constant
    public static final String ERR_INPUT = "Invalid input, must be non-empty!";

    // Details of UI
    public static void PrintServerResponse() {
        System.out.println("\n=================================================");
        System.out.println("================ Server Response ================");
        System.out.println("=================================================");
        System.out.println("================= Status 200 OK =================");
        System.out.println("=================================================");
    }


    public static void PrintErrorMessage(ServerResponse response) {
        System.out.println("\n=================================================");
        System.out.println("============== ERROR CODE " + response.getStatus() + " ==============");
        System.out.println("=================================================");
        switch (response.getStatus()) {
            case 500:
                System.out.println("Internal Server Error or Timeout");
                System.out.println("\n=================================================");
                break;
            case 404:
                System.out.println("User provided input was not found in our system");
                System.out.println("\n=================================================");
                break;
            case 405:
                System.out.println("The offset provided is invalid");
                System.out.println("\n=================================================");
            case 409:
                System.out.println("The booking time slot selected is not available, \nplease try another time slot");
                System.out.println("\n=================================================");
                break;
            default:
                System.out.println("Unknown Error");
                System.out.println("\n=================================================");
                break;
        }

    }
}
