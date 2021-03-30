package server.handlers;

import remote_objects.Common.FacilityBooking;
import remote_objects.Client.ClientQuery;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import network.Network;
import server.ServerDB;
import utils.DateUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ShortenBooking {
    private static DayAndTime newDayAndTimeEnd;
    private static DayAndTime newDayAndTimeStart;

    public static void handleRequest(Network network, InetSocketAddress origin, ServerDB database, ClientQuery query) {
        ServerResponse response;
        FacilityBooking changeInfo = query.getBookings().get(0);
        DayAndTime offset = changeInfo.getOffset();
        String UUID = changeInfo.getUuid();

        List<FacilityBooking> res = new ArrayList<>();

        FacilityBooking booking = database.returnBookingIfExists(UUID);
        if (booking != null) {
            if (validOffset(booking, offset)) {
                res.add(changeBooking(booking, database
                ));
                response = new ServerResponse(query.getId(), 200, res);
                FacilityMonitoring.informRegisteredClients(network, response, query.getType());
                MonitorAndBookOnVacancy.informRegisteredClients(network, response, database);
            } else {
                response = new ServerResponse(query.getId(), 405, res);
            }
        } else {
            response = new ServerResponse(query.getId(), 404, res);
        }

        network.send(response, origin);
    }


    /**
     * reduces the end time of a booking by the offset if the change is valid
     *
     * @param booking  - booking object in question
     * @param database - ServerDB database
     * @return - new booking if change is valid otherwise null
     */
    public static FacilityBooking changeBooking(FacilityBooking booking, ServerDB database) {
        // to be returned to client
        booking.setEndTime(newDayAndTimeEnd);

        // update database
        database.updateBooking(booking.getUuid(), booking.getStartTime(), newDayAndTimeEnd);
        return booking;
    }

    public static boolean validOffset(FacilityBooking booking, DayAndTime offset) {
        int newEndSecs = booking.getEndTime().getEquivalentSeconds() - offset.getEquivalentSeconds();
        newDayAndTimeEnd = DateUtils.convSecondsToDateTime(newEndSecs);
        return newDayAndTimeEnd != null && newDayAndTimeEnd.convSecs() > booking.getStartTime().convSecs();
    }
}