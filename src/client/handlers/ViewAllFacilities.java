package client.handlers;

import client.ClientUI;
import semantics.Semantics;
import remote_objects.Client.ClientRequest;
import remote_objects.Server.ServerResponse;
import utils.Constants;

public class ViewAllFacilities {
    private static ClientRequest query;

    public static void createAndSendMessage(Semantics semInvo) {
        query = new ClientRequest();
        query.setRequestChoice(Constants.VIEW_ALL_FACILITIES);

        int id = semInvo.requestServer(query);
        semInvo.receiveResponse(id, (response) -> {
            if (response.getServerStatus() == 200) {
                printFaciliiesAvailability(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }

    public static void printFaciliiesAvailability(ServerResponse response) {
        ClientUI.ServerSuccessStatus();
        System.out.println("Facilities Availability:");
        for (int i = 0; i < response.getBookings().size(); i++) {
            System.out.println("[" + i + "] " + response.getBookings().get(i).getName());
        }
        System.out.println("=================================================");
    }
}
