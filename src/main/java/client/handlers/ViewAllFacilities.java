package main.java.client.handlers;

import main.java.client.ClientUI;
import main.java.network.Network;
import main.java.remote_objects.Client.ClientQuery;
import main.java.remote_objects.Server.ServerResponse;
import main.java.utils.Constants;

public class ViewAllFacilities {
    private static ClientQuery query;

    public static void createAndSendMessage(Network network) {
        query = new ClientQuery();
        query.setType(Constants.VIEW_ALL_FACILITIES);

        int id = network.send(query);
        network.receive(id, (response) -> {
            if (response.getStatus() == 200) {
                printFaciliiesAvailability(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }

    public static void printFaciliiesAvailability(ServerResponse response) {
        ClientUI.ServerSuccessStatus();
        System.out.println("Facilities Availability:");
        for (int i = 0; i < response.getInfos().size(); i++) {
            System.out.println("[" + i + "] " + response.getInfos().get(i).getName());
        }
        System.out.println("=================================================");
    }
}
