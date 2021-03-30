package server.handlers;

import database.database;
import network.Network;
import remote_objects.Client.ClientCallback;
import remote_objects.Server.ServerResponse;

import java.net.InetSocketAddress;
import java.util.List;

public class FacilityMonitoring {

    public static void informRegisteredClients(Network network, ServerResponse res, int responseType) {
        String facilityName = res.getInfos().get(0).getName();
        List<ClientCallback> addresses = database.getValidMonitorFacilityRequests(facilityName);
        if (addresses == null) {
            return;
        }
        for (ClientCallback cInfo : addresses) {
            ServerResponse callbackRes = res.clone();
            callbackRes.setType(responseType);
            callbackRes.setRequestId(cInfo.getRequestId());
            network.send(callbackRes, (InetSocketAddress) cInfo.getSocket());
        }
    }
}
