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


    // Service constant
    public static final int VIEW_ALL_FACILITIES = 1;
    public static final int FACILITY_AVAILABILITY = 2;
    public static final int BOOK_FACILITY = 3;
    public static final int CHANGE_BOOKING = 4;
    public static final int MONITOR_BOOKING = 5;
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
}