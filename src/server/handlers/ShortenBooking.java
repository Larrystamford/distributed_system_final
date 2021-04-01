package server.handlers;

import database.database;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.Booking;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import semantics.Semantics;
import server.ServerUI;
import utils.DateUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * NON-IDEMPOTENT EXAMPLE
 * Allows user to shorten their existing booking via their unique booking ID
 */
public class ShortenBooking {
    private static DayAndTime newDayAndTimeEnd;
    private static DayAndTime newDayAndTimeStart;

    public static void handleRequest(Semantics semInvo, InetSocketAddress origin, database database, ClientRequest query) {
        ServerResponse response;
        Booking changeInfo = query.getBookings().get(0);
        DayAndTime offset = changeInfo.getOffset();
        String UUID = changeInfo.getUuid();

        List<Booking> res = new ArrayList<>();

        Booking booking = database.getBookingByUUID(UUID);
        if (booking != null) {
            if (validOffset(booking, offset)) {
                res.add(changeBooking(booking, database
                ));
                response = new ServerResponse(query.getId(), 200, res);
                FacilityMonitoring.informRegisteredClients(semInvo, response, query.getRequestChoice());
                MonitorAndBookOnVacancy.informRegisteredClients(semInvo, response, database);
            } else {
                response = new ServerResponse(query.getId(), 405, res);
            }
        } else {
            response = new ServerResponse(query.getId(), 404, res);
        }

        semInvo.replyClient(response, origin);
        ServerUI.printServerResponse(query, response);
    }


    public static Booking changeBooking(Booking booking, database database) {
        // database updated
        database.updateBooking(booking.getUuid(), booking.getStartTime(), newDayAndTimeEnd);
        booking.setEndTime(newDayAndTimeEnd);

        return booking;
    }

    public static boolean validOffset(Booking booking, DayAndTime offset) {
        int newEndSecs = booking.getEndTime().getEquivalentSeconds() - offset.getEquivalentSeconds();
        newDayAndTimeEnd = DateUtils.convertSecondsToDate(newEndSecs);
        return newDayAndTimeEnd != null && newDayAndTimeEnd.convertDateIntoSeconds() > booking.getStartTime().convertDateIntoSeconds();
    }
}