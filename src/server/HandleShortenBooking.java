package server;

import entity.BookingInfo;
import entity.ClientQuery;
import entity.DateTime;
import entity.ServerResponse;
import network.Network;
import utils.DateUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class HandleShortenBooking {
    private static DateTime newDateTimeEnd;
    private static DateTime newDateTimeStart;

    public static void handleRequest(Network network, InetSocketAddress origin, ServerDB database, ClientQuery query) {
        ServerResponse response;
        BookingInfo changeInfo = query.getBookings().get(0);
        DateTime offset = changeInfo.getOffset();
        String UUID = changeInfo.getUuid();

        List<BookingInfo> res = new ArrayList<>();

        BookingInfo booking = database.returnBookingIfExists(UUID);
        if (booking != null) {
            if (validOffset(booking, offset)) {
                res.add(changeBooking(booking, database
                ));
                response = new ServerResponse(query.getId(), 200, res);
                HandleMonitorFacility.informRegisteredClients(network, response, query.getType());
                HandleBookOnVacancy.informRegisteredClients(network, response, database);
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
    public static BookingInfo changeBooking(BookingInfo booking, ServerDB database) {
        // to be returned to client
        booking.setEndTime(newDateTimeEnd);

        // update database
        database.updateBooking(booking.getUuid(), booking.getStartTime(), newDateTimeEnd);
        return booking;
    }

    public static boolean validOffset(BookingInfo booking, DateTime offset) {
        int newEndSecs = booking.getEndTime().getEquivalentSeconds() - offset.getEquivalentSeconds();
        newDateTimeEnd = DateUtils.convSecondsToDateTime(newEndSecs);
        return newDateTimeEnd != null && newDateTimeEnd.convSecs() > booking.getStartTime().convSecs();
    }
}