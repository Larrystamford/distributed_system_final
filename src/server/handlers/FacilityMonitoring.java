package server.handlers;

import entity.ClientCallbackInfo;
import entity.ServerResponse;
import network.Network;
import server.ServerDB;

import java.net.InetSocketAddress;
import java.util.List;

public class FacilityMonitoring {

    public static void informRegisteredClients(Network network, ServerResponse res, int responseType) {
        String facilityName = res.getInfos().get(0).getName();
        List<ClientCallbackInfo> addresses = ServerDB.getValidMonitorFacilityRequests(facilityName);
        if (addresses == null) {
            return;
        }
        for (ClientCallbackInfo cInfo : addresses) {
            ServerResponse callbackRes = res.clone();
            callbackRes.setType(responseType);
            callbackRes.setQueryId(cInfo.getQueryId());
            network.send(callbackRes, (InetSocketAddress) cInfo.getSocket());
        }
    }
}
