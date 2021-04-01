package client.handlers;

import client.ClientUI;
import utils.Constants;
import remote_objects.Common.Booking;
import remote_objects.Client.ClientRequest;
import semantics.Semantics;
import utils.UserInputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FacilityMonitoring {

    public static void createAndSendMessage(Semantics semInvo, Scanner scanner) {
        ClientRequest query;
        List<Booking> bookings = new ArrayList<Booking>();

        // get monitor duration
        query = new ClientRequest();
        ClientUI.getMonitorFacilityInput(scanner, bookings, query);

        query.setRequestChoice(Constants.FACILITY_MONITORING);
        query.setBookings(bookings);

        int id = semInvo.requestServer(query);
        semInvo.monitorServer((response) -> {
            if (response.getServerStatus() == 200) {
                ClientUI.monitoringResponse(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, true, query.getMonitoringDuration() * 1000);

        System.out.println(ClientUI.LINE_SEPARATOR);
        System.out.println("MONITORING COMPLETE");
        System.out.println(ClientUI.LINE_SEPARATOR);
    }


}
