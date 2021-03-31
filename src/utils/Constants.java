package utils;

import java.util.HashMap;

public class Constants {
    // Argument Constant
    public static final double DEFAULT_FAILURE_RATE = 0.0;
    public static final int DEFAULT_TIMEOUT = 500; // milliseconds
    public static final int DEFAULT_MAX_TRY = 5;


    // Service constant
    public static final int VIEW_ALL_FACILITIES = 1;
    public static final int FACILITY_AVAILABILITY = 2;
    public static final int FACILITY_BOOKING = 3;
    public static final int OFFSET_BOOKING = 4;
    public static final int FACILITY_MONITORING = 5;
    public static final int SHORTEN_BOOKING = 6;
    public static final int MONITOR_AND_BOOK_ON_AVAILABLE = 7;
    public static final int SERVICE_EXIT = 8;

    public static final HashMap<Integer, String> SERVICES_MAP = new HashMap<>();

    static {
        SERVICES_MAP.put(3, "BOOKING MADE -");
        SERVICES_MAP.put(4, "BOOKING OFFSET -");
        SERVICES_MAP.put(6, "BOOKING SHORTENED -");
    }
}