package server;

import remote_objects.Common.Booking;
import remote_objects.Common.DayAndTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import server.handlers.OffsetBooking;

import static org.junit.jupiter.api.Assertions.*;

class OffsetBookingTest {

    Booking booking;
    List<Booking> bookingData;

    public static void populateDatabase(List<Booking> bookingData) {
        // 10 hours after monday 12 am
        DayAndTime d1 = new DayAndTime(1, 10, 0);
        DayAndTime d2 = new DayAndTime(1, 12, 0);
        // 30 minute gap
        DayAndTime d3 = new DayAndTime(1, 12, 30);
        DayAndTime d4 = new DayAndTime(1, 14, 40);
        // 1 hour 20 minute gap
        DayAndTime d5 = new DayAndTime(1, 16, 0);
        DayAndTime d6 = new DayAndTime(1, 18, 0);
        // 8 hour gap
        DayAndTime d7 = new DayAndTime(2, 2, 0);
        DayAndTime d8 = new DayAndTime(2, 3, 0);

        bookingData.add(new Booking("TEST", d1, d2));
        bookingData.add(new Booking("TEST", d3, d4));
        bookingData.add(new Booking("TEST", d5, d6));
        bookingData.add(new Booking("TEST", d7, d8));
    }

    @BeforeEach
    void createBooking() {
        bookingData = new ArrayList<>();
        populateDatabase(bookingData);
    }

    @Test
    void validOffset_resolveOffsetTooEarly_shouldFail() {
        DayAndTime d1 = new DayAndTime(1, 10, 0);
        DayAndTime d2 = new DayAndTime(1, 12, 0);
        booking = new Booking("TEST", d1, d2);
        DayAndTime offset = new DayAndTime(-7, 0, 0);
        boolean valid = OffsetBooking.validOffset(booking, offset);
        assertFalse(valid);
    }

    @Test
    void validOffset_resolveOffsetEqualsMonday12AM_shouldPass() {
        DayAndTime d1 = new DayAndTime(1, 10, 0);
        DayAndTime d2 = new DayAndTime(1, 12, 0);
        booking = new Booking("TEST", d1, d2);
        DayAndTime offset = new DayAndTime(0, -10, 0);
        boolean valid = OffsetBooking.validOffset(booking, offset);
        assertTrue(valid);
    }

    @Test
    void validOffset_resolveOffsetEqualsMonday12AmMinus1Minute_shouldFail() {
        DayAndTime d1 = new DayAndTime(1, 10, 0);
        DayAndTime d2 = new DayAndTime(1, 12, 0);
        booking = new Booking("TEST", d1, d2);
        DayAndTime offset = new DayAndTime(0, -10, -1);
        boolean valid = OffsetBooking.validOffset(booking, offset);
        assertFalse(valid);
    }
}