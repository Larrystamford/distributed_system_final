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
                ClientUI.printFaciliiesAvailability(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }
}
