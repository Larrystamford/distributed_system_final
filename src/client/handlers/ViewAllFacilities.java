package client.handlers;

import client.ClientUI;
import network.Network;
import remote_objects.Client.ClientRequest;
import remote_objects.Server.ServerResponse;
import utils.Constants;

public class ViewAllFacilities {
    private static ClientRequest query;

    public static void createAndSendMessage(Network network) {
        query = new ClientRequest();
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
