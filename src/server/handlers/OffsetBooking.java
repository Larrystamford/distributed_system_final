package server.handlers;

import remote_objects.Common.FacilityBooking;
import remote_objects.Client.ClientQuery;
import remote_objects.Common.DayAndTime;
import remote_objects.Server.ServerResponse;
import network.Network;
import server.ServerDB;
import utils.DateUtils;
import utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class OffsetBooking {
    private static DayAndTime newDayAndTimeEnd;
    private static DayAndTime newDayAndTimeStart;

    /**
     * @param network  - the udp communicator used
     * @param origin   - client's ip address and port
     * @param database - ServerDB
     * @param query    - change booking query
     */
    public static void handleRequest(Network network, InetSocketAddress origin, ServerDB database, ClientQuery query) {
        ServerResponse response;
        FacilityBooking changeInfo = query.getBookings().get(0);
        DayAndTime offset = changeInfo.getOffset();
        String UUID = changeInfo.getUuid();

        List<FacilityBooking> res = new ArrayList<>();

        FacilityBooking booking = database.returnBookingIfExists(UUID);

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


    /**
     * changes the start and end time of a booking by the offset if the change is valid
     *
     * @param booking  - booking object in question
     * @param offset   - change in time slot desired by the user
     * @param database - ServerDB database
     * @return - new booking if change is valid otherwise null
     */
    public static FacilityBooking changeBooking(FacilityBooking booking, DayAndTime offset, ServerDB database) {
        // remove current booking to check if new booking would cause conflicts
        List<FacilityBooking> bookings = database.getBookingsByName(booking.getName());
        for (int i = 0; i < bookings.size(); i++) {
            FacilityBooking bInfo = bookings.get(i);
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
    public static boolean validOffset(FacilityBooking booking, DayAndTime offset) {
        int newStartSecs = booking.getStartTime().getEquivalentSeconds() + offset.getEquivalentSeconds();
        int newEndSecs = booking.getEndTime().getEquivalentSeconds() + offset.getEquivalentSeconds();

        newDayAndTimeStart = DateUtils.convSecondsToDateTime(newStartSecs);
        newDayAndTimeEnd = DateUtils.convSecondsToDateTime(newEndSecs);

        return newDayAndTimeStart != null && newDayAndTimeEnd != null;
    }
}

