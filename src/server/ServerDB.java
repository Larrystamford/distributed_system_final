package server;

import client.ClientUI;
import constants.Constants;
import entity.BookingInfo;
import entity.ClientCallbackInfo;
import entity.ClientQuery;
import entity.DateTime;
import javacutils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ServerDB {
    private static final ArrayList<BookingInfo> bookingData = new ArrayList<BookingInfo>();
    private static final ArrayList<String> facilitiesAvailableNames = new ArrayList<>();

    private static final HashMap<String, List<ClientCallbackInfo>> monitorFacilityList = new HashMap<>();
    private static final HashMap<String, List<Pair<ClientCallbackInfo, ClientQuery>>> bookOnVacancyList = new HashMap<>();

    public ServerDB() {
        seed();
    }

    public static void seed() {
        DateTime d1 = new DateTime(1, 2, 23);
        DateTime d2 = new DateTime(1, 12, 12);
        DateTime d3 = new DateTime(3, 11, 21);
        DateTime d4 = new DateTime(4, 12, 23);
        DateTime d5 = new DateTime(2, 0, 0);
        DateTime d6 = new DateTime(6, 23, 59);
        DateTime d7 = new DateTime(6, 10, 23);
        DateTime d8 = new DateTime(6, 23, 11);

        bookingData.add(new BookingInfo("SS1", d1, d2));
        bookingData.add(new BookingInfo("SS2", d3, d4));
        bookingData.add(new BookingInfo("SS3", d5, d6));
        bookingData.add(new BookingInfo("SS4", d7, d8));

        BookingInfo changeBookingTest = new BookingInfo("SS5", d1, d2);
//        changeBookingTest.setUuid("1");
        bookingData.add(changeBookingTest);

        facilitiesAvailableNames.add("SS1");
        facilitiesAvailableNames.add("SS2");
        facilitiesAvailableNames.add("SS3");
        facilitiesAvailableNames.add("SS4");
        facilitiesAvailableNames.add("SS5");
        facilitiesAvailableNames.add("SS6");
    }

    public static BookingInfo createBooking(String name, DateTime startTime, DateTime endTime) {
        BookingInfo newBooking = new BookingInfo(name, startTime, endTime);
        bookingData.add(newBooking);

        return newBooking;
    }

    public List<BookingInfo> getAllBookings() {
        for (BookingInfo booking : bookingData) {
            System.out.println(booking.getUuid());
            System.out.println(booking.getName() + booking.getStartTime().toNiceString() + booking.getEndTime().toNiceString());
        }

        System.out.println(ClientUI.SEPARATOR);
        System.out.println("Monitor facilities list");

        monitorFacilityList.forEach((name, cInfoList) -> {
            for (ClientCallbackInfo cInfo : cInfoList) {
                System.out.println(cInfo.getExpire() + " " + cInfo.getQueryId());
            }
        });

        System.out.println(ClientUI.SEPARATOR);

        return bookingData;
    }

    public void updateBooking(String UUID, DateTime start, DateTime end) {
        BookingInfo booking = returnBookingIfExists(UUID);
        if (booking != null) {
            booking.setStartTime(start);
            booking.setEndTime(end);
        }
    }

    // *** change this to hash map implementation
    public boolean bookingNameExist(String name) {
        boolean exist = false;
        for (String facilityName : facilitiesAvailableNames) {
            if (facilityName.equals(name)) {
                exist = true;
                break;
            }
        }

        return exist;
    }

    // *** change this to hash map
    public BookingInfo returnBookingIfExists(String UUID) {
        for (BookingInfo booking : bookingData) {
            if (booking.getUuid() != null && booking.getUuid().equals(UUID)) {
                return booking;
            }
        }
        return null;
    }

    public List<BookingInfo> getBookingsByName(String name) {
        List<BookingInfo> res = new ArrayList<>();
        for (BookingInfo booking : bookingData) {
            if (booking.getName().equals(name)) {
                res.add(booking);
            }
        }
        return res;
    }

    public List<BookingInfo> getBookingsByNameAndDay(String name, int day) {
        List<BookingInfo> res = new ArrayList<>();
        for (BookingInfo booking : bookingData) {
            if (booking.getName().equals(name) && (booking.getStartTime().getDay() == day || booking.getEndTime().getDay() == day || (booking.getStartTime().getDay() < day && day <= booking.getEndTime().getDay()))) {
                res.add(booking);
            }
        }

        System.out.println("check this too");
        System.out.println(res);
        return res;
    }

    /**
     * registers a client for monitoring a facility's availability over the week
     *
     * @param facilityName       - name of facility client is interested in monitoring
     * @param clientCallbackInfo - client's socket information
     */
    public void registerMonitoring(String facilityName, ClientCallbackInfo clientCallbackInfo) {
        if (!monitorFacilityList.containsKey(facilityName)) {
            monitorFacilityList.put(facilityName, new ArrayList<>());
        }
        List<ClientCallbackInfo> addresses = monitorFacilityList.get(facilityName);
        addresses.add(clientCallbackInfo);
        monitorFacilityList.put(facilityName, addresses);
    }

    public static List<ClientCallbackInfo> getValidMonitorFacilityRequests(String facilityName) {
        filterCallBackAddresses();
        return monitorFacilityList.get(facilityName);
    }

    /**
     * registers a client for monitoring a facility's availability for an occupied slot
     * and booking it should it become vacant
     *
     * @param facilityName       - name of facility client is interested in monitoring
     * @param clientCallbackInfo - client's socket information
     */
    public void registerBookOnVacancy(String facilityName, ClientCallbackInfo clientCallbackInfo, ClientQuery query) {
        if (!bookOnVacancyList.containsKey(facilityName)) {
            bookOnVacancyList.put(facilityName, new ArrayList<>());
        }
        List<Pair<ClientCallbackInfo, ClientQuery>> addresses = bookOnVacancyList.get(facilityName);
        Pair<ClientCallbackInfo, ClientQuery> pair = Pair.of(clientCallbackInfo, query);
        addresses.add(pair);
        bookOnVacancyList.put(facilityName, addresses);
    }

    public static List<Pair<ClientCallbackInfo, ClientQuery>> getValidBookOnVacancyRequest(String facilityName) {
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
