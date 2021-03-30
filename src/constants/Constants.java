package constants;

import entity.ServerResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    // Argument Constant
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 2222;
    public static final double DEFAULT_FAILURE_RATE = 0.0;
    public static final int DEFAULT_TIMEOUT = 1000;
    public static final int DEFAULT_NO_TIMEOUT = 0;
    public static final int DEFAULT_MAX_TIMEOUT = 0;
    public static final int DEFAULT_MINUTE_TIMEOUT = 60000;


    // Connection Constant
    public static final int ACK = 1;
    public static final String ACK_CHAR = "1";
    public static final int NAK = 0;
    public static final String NAK_CHAR = "0";
    public static final int RESPONSE_TYPE_SIZE = 1;
    public static final String INVALID_RESPONSE = "Sorry we are having problem in the server";
    public static final String TIMEOUT_MSG = "Timeout!, resending request ... (%d/%d)\n";
    public static final String MAX_TIMEOUT_MSG = "Max timeout limit exceeded\n";


    public static final int NO_SEM_INVO = 0;
    public static final int AT_LEAST_ONE_SEM_INVO = 1;
    public static final int AT_MOST_ONE_SEM_INVO = 2;

    // Type Constant
    public static final int INT_SIZE = 4;
    public static final int FLOAT_SIZE = 4;

    // Service constant
    public static final int VIEW_ALL_FACILITIES = 1;
    public static final int CHECK_FACILITIES_AVAILABILITY = 2;
    public static final int BOOK_FACILITY = 3;
    public static final int CHANGE_BOOKING = 4;
    public static final int MONITOR_BOOKING = 5;
    public static final int CANCEL_BOOKING = 6;
    public static final int EXTEND_BOOKING = 7;
    public static final int SHORTEN_BOOKING = 8;
    public static final int BOOK_ON_VACANCY = 9;
    public static final int SERVICE_EXIT = 10;

    public static final HashMap<Integer, String> SERVICES_MAP = new HashMap<>();

    static {
        SERVICES_MAP.put(3, "BOOKING MADE:");
        SERVICES_MAP.put(4, "BOOKING CHANGED:");
        SERVICES_MAP.put(6, "BOOKING CANCELED");
        SERVICES_MAP.put(7, "BOOKING EXTENDED:");
        SERVICES_MAP.put(8, "BOOKING SHORTENED:");
    }

    // Currency Constant
    public static final int CUR_SGD = 1;
    public static final int CUR_USD = 2;

    public static final String[] CURRENCY_STR = {
            "not supported",
            "SGD",
            "USD"
    };


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
        System.out.println(Constants.SELECTION_SVC_MSG);
        System.out.println(Constants.VIEW_ALL_FACILITIES_SVC_MSG);
        System.out.println(Constants.CHECK_FACILITIES_AVAILABILITY_SVC_MSG);
        System.out.println(Constants.BOOK_FACILITY_SVC_MSG);
        System.out.println(Constants.CHANGE_BOOKING_SVC_MSG);
        System.out.println(Constants.MONITOR_UPDATE_SVC_MSG);
        System.out.println(Constants.CANCEL_BOOKING_SVC_MSG);
        System.out.println(Constants.EXTEND_BOOKING_SVC_MSG);
        System.out.println(Constants.SHORTEN_BOOKING_SVC_MSG);
        System.out.println(Constants.WATCH_BOOKING_SVC_MSG);
        System.out.println(Constants.EXIT_SVC_MSG);
        System.out.println(Constants.SANITY_CHECK);
        System.out.println();
        System.out.print(Constants.CHOICE_SVC_MSG);
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

    // CANCEL BOOKING UI
    public static final String CANCEL_BOOKING_HEADER = "CANCEL FACILITY";

    // EXTEND BOOKING UI
    public static final String SHORTEN_BOOKING_HEADER = "SHORTEN BOOKING";
    public static final String SHORTEN_BOOKING_PROMPT = "BY HOW MUCH WOULD YOU LIKE TO SHORTEN YOUR BOOKING?";

    // SHORTEN BOOKING UI
    public static final String EXTEND_BOOKING_HEADER = "EXTEND BOOKING";
    public static final String EXTEND_BOOKING_PROMPT = "BY HOW MUCH WOULD YOU LIKE TO EXTEND YOUR BOOKING?";

    // BOOK ON VACANCY UI
    public static final String BOOK_ON_VACANCY_HEADER = "BOOK ON VACANCY";
    public static final String BOOK_ON_VACANCY_PROMPT = "HOW LONG WOULD YOU LIKE TO CONSIDER THE BOOKING";


    // Close Account UI Constant
    public static final String CLOSE_MSG = "Closing existing account!";
    public static final String CLOSE_NAME_MSG = "Enter your name: ";
    public static final String CLOSE_ACC_NUM_MSG = "Enter your account number: ";
    public static final String CLOSE_PASSWORD_MSG = "Enter your password: ";
    public static final String SUCCESSFUL_CLOSE_ACCOUNT = "You have successfully close your account.";

    // Change Password UI Constant
    public static final String CHANGE_MSG = "Change Password!";
    public static final String CHANGE_NAME_MSG = "Enter your name: ";
    public static final String CHANGE_ACC_NUM_MSG = "Enter your account number: ";
    public static final String CHANGE_PASSWORD_MSG = "Enter your old password: ";
    public static final String CHANGE_NEW_PASSWORD_MSG = "Enter your new password: ";
    public static final String SUCCESSFUL_CHANGE_PASSWORD = "You have successfully change your password.";

    // Deposit Money UI Constant
    public static final String DEPOSIT_MSG = "Deposit money!";
    public static final String DEPOSIT_NAME_MSG = "Enter your name: ";
    public static final String DEPOSIT_ACC_NUM_MSG = "Enter your account number: ";
    public static final String DEPOSIT_PASSWORD_MSG = "Enter your password: ";
    public static final String DEPOSIT_SELECT_CURRENCY_MSG = "Choose from the following currency: ";
    public static final String DEPOSIT_CURRENCY_MSG = "Enter currency: ";
    public static final String DEPOSIT_BALANCE_MSG = "Enter deposit balance (e.g. 1000.00): ";
    public static final String SUCCESSFUL_DEPOSIT_MONEY = "You have successfully deposited money.\nYour new balance: %s %f\n";

    // Withdraw Money UI Constant
    public static final String WITHDRAW_MSG = "Withdraw money!";
    public static final String WITHDRAW_NAME_MSG = "Enter your name: ";
    public static final String WITHDRAW_ACC_NUM_MSG = "Enter your account number: ";
    public static final String WITHDRAW_PASSWORD_MSG = "Enter your password: ";
    public static final String WITHDRAW_SELECT_CURRENCY_MSG = "Choose from the following currency: ";
    public static final String WITHDRAW_CURRENCY_MSG = "Enter currency: ";
    public static final String WITHDRAW_BALANCE_MSG = "Enter withdraw balance (e.g. 1000.00): ";
    public static final String SUCCESSFUL_WITHDRAW_MONEY = "You have successfully withdrawn money.\nYour new balance: %s %f\n";

    // Transfer Money UI Constant
    public static final String TRANSFER_MSG = "Transfer money!";
    public static final String TRANSFER_NAME_MSG = "Enter your name: ";
    public static final String TRANSFER_ACC_NUM_MSG = "Enter your account number: ";
    public static final String TRANSFER_REC_NAME_MSG = "Enter recipient name: ";
    public static final String TRANSFER_REC_ACC_NUM_MSG = "Enter recipient account number: ";
    public static final String TRANSFER_PASSWORD_MSG = "Enter your password: ";
    public static final String TRANSFER_SELECT_CURRENCY_MSG = "Choose from the following currency: ";
    public static final String TRANSFER_CURRENCY_MSG = "Enter currency: ";
    public static final String TRANSFER_BALANCE_MSG = "Enter transfer balance (e.g. 1000.00): ";
    public static final String SUCCESSFUL_TRANSFER_MONEY = "You have successfully transferred money.\nYour new balance: %s %f\n";

    // Monitor Update UI Constant
    public static final String MONITOR_MSG = "Monitor update!";
    public static final String MONITOR_DURATION_MSG = "Enter duration to monitor (ms): ";
    public static final String MONITORING_START_MSG = "Start monitoring!";
    public static final String NEW_UPDATE = "[%s] UPDATE: %s\n";
    public static final String MONITORING_FINISH_MSG = "Monitoring Update Finished";

    // Confirmation UI Constant
    public static final String CONFIRM_SUMMARY = "Summary:";
    public static final String CONFIRM_NAME = "Name: %s\n";
    public static final String CONFIRM_REC_NAME = "Name: %s\n";
    public static final String CONFIRM_PASSWORD = "Password: %s\n";
    public static final String CONFIRM_NEW_PASSWORD = "New Password: %s\n";
    public static final String CONFIRM_CURRENCY = "Currency: %s\n";
    public static final String CONFIRM_BALANCE = "Balance: %f\n";
    public static final String CONFIRM_ACCOUNT_NUMBER = "Account Number: %d\n";
    public static final String CONFIRM_REC_ACCOUNT_NUMBER = "Recipient Account Number: %d\n";
    public static final String CONFIRM_DURATION = "Duration to monitor: %d\n";
    public static final String CONFIRM_MSG = "Are you sure? (Y/N) ";
    public static final String CONFIRM_YES = "Y";
    public static final String CONFIRM_NO = "N";

    // Error UI Constant
    public static final String ERR_INPUT = "Invalid input, must be non-empty!";
    public static final String ERR_PASSWORD_INPUT = "Invalid password, must be non-empty string!";

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