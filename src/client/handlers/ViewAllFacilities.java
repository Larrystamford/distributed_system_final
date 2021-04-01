package client.handlers;

import client.ClientUI;
import remote_objects.Client.ClientRequest;
import semantics.Semantics;
import utils.Constants;

/**
 * Additional query that allows user to view all the facilities names that are available for booking
 */
public class ViewAllFacilities {
    private static ClientRequest query;

    public static void createAndSendMessage(Semantics semInvo) {
        query = new ClientRequest();
        query.setRequestChoice(Constants.VIEW_ALL_FACILITIES);

        int id = semInvo.requestServer(query);
        semInvo.receiveResponse(id, (response) -> {
            if (response.getServerStatus() == 200) {
                ClientUI.listFacilitiesResponse(response);
            } else {
                ClientUI.ServerErrorUI(response);
            }
        }, false, 5);
    }
}
