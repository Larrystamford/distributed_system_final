package client.handlers;

import client.ClientUI;
import utils.Constants;
import remote_objects.Client.ClientQuery;
import remote_objects.Server.ServerResponse;
import network.Network;

public class ViewAllFacilities {
    private static ClientQuery query;

    /**
     * creates and sends view all facilities query to the server then waits
     * and handles the response. If no response is received before the timeout
     * send timeout message to the client
     *
     * @param network - udp communicator
     */
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

    /**
     * parses the server response and prints the relevant information
     *
     * @param response - response from the server
     */
    public static void printFaciliiesAvailability(ServerResponse response) {
        ClientUI.ServerSuccessStatus();
        System.out.println("QUERY:");
//        String format = "%-40s%s%n";
//        System.out.printf(format, "Source:", query.getBooking().getName());
        System.out.println("=================================================");
        System.out.println("Facilities Availability:");
        for (int i = 0; i < response.getInfos().size(); i++) {
            System.out.println("[" + i + "] " + response.getInfos().get(i).getName());
        }
        System.out.println("=================================================");
    }
}
