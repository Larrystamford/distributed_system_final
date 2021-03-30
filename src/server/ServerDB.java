package server;

import client.ClientUI;
import remote_objects.Common.FacilityBooking;
import remote_objects.Client.ClientCallback;
import remote_objects.Client.ClientQuery;
import remote_objects.Common.DayAndTime;
import javacutils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ServerDB {
    private static final ArrayList<FacilityBooking> bookingData = new ArrayList<FacilityBooking>();
    private static final ArrayList<String> facilitiesAvailableNames = new ArrayList<>();

    private static final HashMap<String, List<ClientCallback>> monitorFacilityList = new HashMap<>();
    private static final HashMap<String, List<Pair<ClientCallback, ClientQuery>>> bookOnVacancyList = new HashMap<>();

    public ServerDB() {
        seed();
    }

    public static void seed() {
        DayAndTime d1 = new DayAndTime(1, 2, 23);
        DayAndTime d2 = new DayAndTime(1, 12, 12);
        DayAndTime d3 = new DayAndTime(3, 11, 21);
        DayAndTime d4 = new DayAndTime(4, 12, 23);
        DayAndTime d5 = new DayAndTime(2, 0, 0);
        DayAndTime d6 = new DayAndTime(6, 23, 59);
        DayAndTime d7 = new DayAndTime(6, 10, 23);
        DayAndTime d8 = new DayAndTime(6, 23, 11);

        bookingData.add(new FacilityBooking("SS1", d1, d2));
        bookingData.add(new FacilityBooking("SS2", d3, d4));
        bookingData.add(new FacilityBooking("SS3", d5, d6));
        bookingData.add(new FacilityBooking("SS4", d7, d8));

        FacilityBooking changeBookingTest = new FacilityBooking("SS5", d1, d2);
//        changeBookingTest.setUuid("1");
        bookingData.add(changeBookingTest);

        facilitiesAvailableNames.add("SS1");
        facilitiesAvailableNames.add("SS2");
        facilitiesAvailableNames.add("SS3");
        facilitiesAvailableNames.add("SS4");
        facilitiesAvailableNames.add("SS5");
        facilitiesAvailableNames.add("SS6");
    }

    public static FacilityBooking createBooking(String name, DayAndTime startTime, DayAndTime endTime) {
        FacilityBooking newBooking = new FacilityBooking(name, startTime, endTime);
        bookingData.add(newBooking);

        return newBooking;
    }

    public List<FacilityBooking> getAllBookings() {
        for (FacilityBooking booking : bookingData) {
            System.out.println(booking.getUuid());
            System.out.println(booking.getName() + booking.getStartTime().toNiceString() + booking.getEndTime().toNiceString());
        }

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

    public void updateBooking(String UUID, DayAndTime start, DayAndTime end) {
        FacilityBooking booking = returnBookingIfExists(UUID);
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
    public FacilityBooking returnBookingIfExists(String UUID) {
        for (FacilityBooking booking : bookingData) {
            if (booking.getUuid() != null && booking.getUuid().equals(UUID)) {
                return booking;
            }
        }
        return null;
    }

    public List<FacilityBooking> getBookingsByName(String name) {
        List<FacilityBooking> res = new ArrayList<>();
        for (FacilityBooking booking : bookingData) {
            if (booking.getName().equals(name)) {
                res.add(booking);
            }
        }
        return res;
    }

    public List<FacilityBooking> getBookingsByNameAndDay(String name, int day) {
        List<FacilityBooking> res = new ArrayList<>();
        for (FacilityBooking booking : bookingData) {
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
     * @param facilityName       - name of facility client is interested in monitoring
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
