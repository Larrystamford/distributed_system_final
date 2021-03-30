package server;

import entity.BookingInfo;
import entity.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import server.handlers.OffsetBooking;

import static org.junit.jupiter.api.Assertions.*;

class OffsetBookingTest {

    BookingInfo booking;
    List<BookingInfo> bookingData;

    public static void populateDatabase(List<BookingInfo> bookingData) {
        // 10 hours after monday 12 am
        DateTime d1 = new DateTime(1, 10, 0);
        DateTime d2 = new DateTime(1, 12, 0);
        // 30 minute gap
        DateTime d3 = new DateTime(1, 12, 30);
        DateTime d4 = new DateTime(1, 14, 40);
        // 1 hour 20 minute gap
        DateTime d5 = new DateTime(1, 16, 0);
        DateTime d6 = new DateTime(1, 18, 0);
        // 8 hour gap
        DateTime d7 = new DateTime(2, 2, 0);
        DateTime d8 = new DateTime(2, 3, 0);

        bookingData.add(new BookingInfo("TEST", d1, d2));
        bookingData.add(new BookingInfo("TEST", d3, d4));
        bookingData.add(new BookingInfo("TEST", d5, d6));
        bookingData.add(new BookingInfo("TEST", d7, d8));
    }

    @BeforeEach
    void createBooking() {
        bookingData = new ArrayList<>();
        populateDatabase(bookingData);
    }

    @Test
    void validOffset_resolveOffsetTooEarly_shouldFail() {
        DateTime d1 = new DateTime(1, 10, 0);
        DateTime d2 = new DateTime(1, 12, 0);
        booking = new BookingInfo("TEST", d1, d2);
        DateTime offset = new DateTime(-7, 0, 0);
        boolean valid = OffsetBooking.validOffset(booking, offset);
        assertFalse(valid);
    }

    @Test
    void validOffset_resolveOffsetEqualsMonday12AM_shouldPass() {
        DateTime d1 = new DateTime(1, 10, 0);
        DateTime d2 = new DateTime(1, 12, 0);
        booking = new BookingInfo("TEST", d1, d2);
        DateTime offset = new DateTime(0, -10, 0);
        boolean valid = OffsetBooking.validOffset(booking, offset);
        assertTrue(valid);
    }

    @Test
    void validOffset_resolveOffsetEqualsMonday12AmMinus1Minute_shouldFail() {
        DateTime d1 = new DateTime(1, 10, 0);
        DateTime d2 = new DateTime(1, 12, 0);
        booking = new BookingInfo("TEST", d1, d2);
        DateTime offset = new DateTime(0, -10, -1);
        boolean valid = OffsetBooking.validOffset(booking, offset);
        assertFalse(valid);
    }
}