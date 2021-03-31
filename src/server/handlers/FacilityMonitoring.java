package server.handlers;

import client.ClientUI;
import database.Database;
import network.Network;
import remote_objects.Client.ClientCallback;
import remote_objects.Server.ServerResponse;

import java.net.InetSocketAddress;
import java.util.List;

public class FacilityMonitoring {

    public static void informRegisteredClients(Network network, ServerResponse res, int responseType) {
        String facilityName = res.getBookings().get(0).getName();
        List<ClientCallback> addresses = Database.getValidMonitorFacilityRequests(facilityName);
        if (addresses == null) {
            return;
        }
        for (ClientCallback cInfo : addresses) {
            ServerResponse callbackRes = res.clone();
            callbackRes.setRequestChoice(responseType);
            callbackRes.setRequestId(cInfo.getRequestId());
            network.replyClient(callbackRes, (InetSocketAddress) cInfo.getSocket());
        }
    }
}
