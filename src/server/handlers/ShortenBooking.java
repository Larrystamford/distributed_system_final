package server.handlers;

import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import semantics.Semantics;
import database.database;
import server.ServerUI;
import utils.DateUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

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
        // to be returned to client
        booking.setEndTime(newDayAndTimeEnd);

        // update database
        database.updateBooking(booking.getUuid(), booking.getStartTime(), newDayAndTimeEnd);
        return booking;
    }

    public static boolean validOffset(Booking booking, DayAndTime offset) {
        int newEndSecs = booking.getEndTime().getEquivalentSeconds() - offset.getEquivalentSeconds();
        newDayAndTimeEnd = DateUtils.convSecondsToDateTime(newEndSecs);
        return newDayAndTimeEnd != null && newDayAndTimeEnd.convSecs() > booking.getStartTime().convSecs();
    }
}