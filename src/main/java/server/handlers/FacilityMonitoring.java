package main.java.server.handlers;

import main.java.remote_objects.Client.ClientCallback;
import main.java.remote_objects.Server.ServerResponse;
import main.java.network.Network;
import main.java.database.database;

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
            callbackRes.setQueryId(cInfo.getQueryId());
            network.send(callbackRes, (InetSocketAddress) cInfo.getSocket());
        }
    }
}
