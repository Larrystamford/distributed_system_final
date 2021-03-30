package database;

import client.ClientUI;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientCallback;
import remote_objects.Client.ClientQuery;
import remote_objects.Common.DayAndTime;
import javacutils.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class database {
    private static final ArrayList<Booking> bookingData = new ArrayList<Booking>();
    private static final Set<String> facilityNames = new HashSet();
    private static final HashMap<String, List<ClientCallback>> monitorFacilityList = new HashMap<>();
    private static final HashMap<String, List<Pair<ClientCallback, ClientQuery>>> bookOnVacancyList = new HashMap<>();

    public database() {
        initDatabase();
    }

    public static void initDatabase() {
        facilityNames.add("LAB 1");
        facilityNames.add("LAB 2");
        facilityNames.add("LAB 3");
        facilityNames.add("LAB 4");

        DayAndTime d1 = new DayAndTime(1, 2, 23);
        DayAndTime d2 = new DayAndTime(1, 12, 12);
        DayAndTime d3 = new DayAndTime(3, 11, 21);
        DayAndTime d4 = new DayAndTime(4, 12, 23);
        DayAndTime d5 = new DayAndTime(2, 0, 0);
        DayAndTime d6 = new DayAndTime(6, 23, 59);
        DayAndTime d7 = new DayAndTime(6, 10, 23);
        DayAndTime d8 = new DayAndTime(6, 23, 11);

        bookingData.add(new Booking("LAB 1", d1, d2));
        bookingData.add(new Booking("LAB 2", d3, d4));
        bookingData.add(new Booking("LAB 3", d5, d6));
        bookingData.add(new Booking("LAB 4", d7, d8));
    }

    public static Booking createBooking(String name, DayAndTime startTime, DayAndTime endTime) {
        Booking newBooking = new Booking(name, startTime, endTime);
        bookingData.add(newBooking);

        return newBooking;
    }

    public List<Booking> getAllBookings() {
        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println("Monitor facilities list");

        monitorFacilityList.forEach((name, cInfoList) -> {
            for (ClientCallback cInfo : cInfoList) {
                System.out.println(cInfo.getExpire() + " " + cInfo.getQueryId());
            }
        });

        System.out.println(ClientUI.LINE_SEPARATOR);

        return bookingData;
    }

    public Booking getBookingByUUID(String UUID) {
        for (Booking booking : bookingData) {
            if (booking.getUuid() != null && booking.getUuid().equals(UUID)) {
                return booking;
            }
        }
        return null;
    }

    public List<Booking> getBookingsByName(String name) {
        List<Booking> res = new ArrayList<>();
        for (Booking booking : bookingData) {
            if (booking.getName().equals(name)) {
                res.add(booking);
            }
        }
        return res;
    }

    public List<Booking> getBookingsByNameAndDay(String name, int day) {
        List<Booking> res = new ArrayList<>();
        for (Booking booking : bookingData) {
            if (booking.getName().equals(name) && (booking.getStartTime().getDay() == day || booking.getEndTime().getDay() == day || (booking.getStartTime().getDay() < day && day <= booking.getEndTime().getDay()))) {
                res.add(booking);
            }
        }

        return res;
    }

    public void updateBooking(String UUID, DayAndTime start, DayAndTime end) {
        Booking booking = getBookingByUUID(UUID);
        if (booking != null) {
            booking.setStartTime(start);
            booking.setEndTime(end);
        }
    }

    public boolean facilityNameExist(String name) {
        if (facilityNames.contains(name)) {
            return true;
        }

        return false;
    }


    /**
     * registers a client for monitoring a facility's availability over the week
     *
     * @param facilityName   - name of facility client is interested in monitoring
     * @param clientCallback - client's socket information
     */
    public void registerMonitoring(String facilityName, ClientCallback clientCallback) {
        if (!monitorFacilityList.containsKey(facilityName)) {
            monitorFacilityList.put(facilityName, new ArrayList<>());
        }
        List<ClientCallback> addresses = monitorFacilityList.get(facilityName);
        addresses.add(clientCallback);
        monitorFacilityList.put(facilityName, addresses);
    }

    public static List<ClientCallback> getValidMonitorFacilityRequests(String facilityName) {
        filterCallBackAddresses();
        return monitorFacilityList.get(facilityName);
    }

    /**
     * registers a client for monitoring a facility's availability for an occupied slot
     * and booking it should it become vacant
     *
     * @param facilityName   - name of facility client is interested in monitoring
     * @param clientCallback - client's socket information
     */
    public void registerBookOnVacancy(String facilityName, ClientCallback clientCallback, ClientQuery query) {
        if (!bookOnVacancyList.containsKey(facilityName)) {
            bookOnVacancyList.put(facilityName, new ArrayList<>());
        }
        List<Pair<ClientCallback, ClientQuery>> addresses = bookOnVacancyList.get(facilityName);
        Pair<ClientCallback, ClientQuery> pair = Pair.of(clientCallback, query);
        addresses.add(pair);
        bookOnVacancyList.put(facilityName, addresses);
    }

    public static List<Pair<ClientCallback, ClientQuery>> getValidBookOnVacancyRequest(String facilityName) {
        filterCallBackAddresses();
        return bookOnVacancyList.get(facilityName);
    }

    /**
     * removes expired callbacks from the callback lists
     */
    public static void filterCallBackAddresses() {
        monitorFacilityList.forEach((facility, registeredCallbacks) -> {
            monitorFacilityList.put(facility, registeredCallbacks.stream().filter((c) -> c.getExpire() > System.currentTimeMillis()).collect(Collectors.toList()));
        });
        bookOnVacancyList.forEach((facility, registeredCallbacks) -> {
            bookOnVacancyList.put(facility, registeredCallbacks.stream().filter((c) -> c.first.getExpire() > System.currentTimeMillis()).collect(Collectors.toList()));
        });
    }
}
