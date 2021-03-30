package main.java.server.handlers;

import main.java.remote_objects.Common.Booking;
import main.java.remote_objects.Client.ClientQuery;
import main.java.remote_objects.Common.DayAndTime;
import main.java.remote_objects.Server.ServerResponse;
import main.java.network.Network;
import main.java.database.database;
import main.java.utils.DateUtils;
import main.java.utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class OffsetBooking {
    private static DayAndTime newDayAndTimeEnd;
    private static DayAndTime newDayAndTimeStart;


    public static void handleRequest(Network network, InetSocketAddress origin, database database, ClientQuery query) {
        ServerResponse response;
        Booking changeInfo = query.getBookings().get(0);
        DayAndTime offset = changeInfo.getOffset();
        String UUID = changeInfo.getUuid();

        List<Booking> res = new ArrayList<>();

        Booking booking = database.getBookingByUUID(UUID);

        if (booking != null) {
            if (!validOffset(booking, offset)) {
                response = new ServerResponse(query.getId(), 405, res);
            } else if (changeBooking(booking, offset, database) != null) {
                res.add(booking);
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


    public static Booking changeBooking(Booking booking, DayAndTime offset, database database) {
        // remove current booking to check if new booking would cause conflicts
        List<Booking> bookings = database.getBookingsByName(booking.getName());
        for (int i = 0; i < bookings.size(); i++) {
            Booking bInfo = bookings.get(i);
            if (bInfo.getUuid().equals(booking.getUuid())) {
                bookings.remove(bInfo);
                break;
            }
        }

        // to be returned to client
        booking.setStartTime(newDayAndTimeStart);
        booking.setEndTime(newDayAndTimeEnd);

        // check for vacancy with selected timings and update db if changes are valid
        if (VacancyChecker.isVacant(bookings, newDayAndTimeStart, newDayAndTimeEnd)) {
            database.updateBooking(booking.getUuid(), newDayAndTimeStart, newDayAndTimeEnd);
            return booking;
        }
        return null;
    }

    /**
     * checks whether the the offset provided by the client is valid. An offset
     * is considered valid when the new start and ends DateTime created by the
     * offset are valid
     *
     * @param booking - initial booking from client
     * @param offset  - offset by which the client wants to change the booking
     * @return
     */
    public static boolean validOffset(Booking booking, DayAndTime offset) {
        int newStartSecs = booking.getStartTime().getEquivalentSeconds() + offset.getEquivalentSeconds();
        int newEndSecs = booking.getEndTime().getEquivalentSeconds() + offset.getEquivalentSeconds();

        newDayAndTimeStart = DateUtils.convSecondsToDateTime(newStartSecs);
        newDayAndTimeEnd = DateUtils.convSecondsToDateTime(newEndSecs);

        return newDayAndTimeStart != null && newDayAndTimeEnd != null;
    }
}

