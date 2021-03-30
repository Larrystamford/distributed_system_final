package server;

import entity.BookingInfo;
import entity.ClientQuery;
import entity.DateTime;
import entity.ServerResponse;
import network.Network;
import utils.DateUtils;
import utils.VacancyChecker;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class HandleExtendBooking {
    private static DateTime newDateTimeEnd;

    public static void handleRequest(Network network, InetSocketAddress origin, ServerDB database, ClientQuery query) {
        ServerResponse response;
        BookingInfo changeInfo = query.getBookings().get(0);
        DateTime offset = changeInfo.getOffset();
        String UUID = changeInfo.getUuid();

        List<BookingInfo> res = new ArrayList<>();

        BookingInfo booking = database.returnBookingIfExists(UUID);

        if (booking != null) {
            if (!validOffset(booking, offset)){
                response = new ServerResponse(query.getId(), 405, res);
            } else if (changeBooking(booking, offset, database) != null) {
                res.add(booking);
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
     * changes the end time of a booking by the offset if the change is valid
     * @param booking - booking object in question
     * @param offset - change in time slot desired by the user
     * @param database - ServerDB database
     * @return - new booking if change is valid otherwise null
     */
    public static BookingInfo changeBooking(BookingInfo booking, DateTime offset, ServerDB database){
        // remove current booking to check if new booking would cause conflicts
        List<BookingInfo> bookings = database.getBookingsByName(booking.getName());
        for (int i=0; i < bookings.size(); i++) {
            BookingInfo bInfo = bookings.get(i);
            if (bInfo.getUuid().equals(booking.getUuid())) {
                bookings.remove(bInfo);
                break;
            }
        }

        // to be returned to client
        booking.setEndTime(newDateTimeEnd);

        // check for vacancy with selected timings and update db if changes are valid
        if (VacancyChecker.isVacant(bookings, booking.getStartTime(), newDateTimeEnd)) {
            database.updateBooking(booking.getUuid(), booking.getStartTime(), newDateTimeEnd);
            return booking;
        }
        return null;
    }

    public static boolean validOffset(BookingInfo booking, DateTime offset) {
        int newEndSecs = booking.getEndTime().getEquivalentSeconds() + offset.getEquivalentSeconds();
        newDateTimeEnd = DateUtils.convSecondsToDateTime(newEndSecs);
        return newDateTimeEnd != null;
    }
}

