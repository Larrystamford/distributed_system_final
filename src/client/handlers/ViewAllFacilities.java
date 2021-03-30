package client.handlers;

import constants.Constants;
import entity.ClientQuery;
import entity.ServerResponse;
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
                Constants.PrintErrorMessage(response);
            }
        }, false, 5);
    }

    /**
     * parses the server response and prints the relevant information
     *
     * @param response - response from the server
     */
    public static void printFaciliiesAvailability(ServerResponse response) {
        Constants.PrintServerResponse();
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
