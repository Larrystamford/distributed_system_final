package server.handlers;

import database.Database;
import semantics.Semantics;
import remote_objects.Client.ClientCallback;
import remote_objects.Server.ServerResponse;

import java.net.InetSocketAddress;
import java.util.List;

public class FacilityMonitoring {

    public static void informRegisteredClients(Semantics semInvo, ServerResponse res, int responseType) {
        String facilityName = res.getBookings().get(0).getName();
        List<ClientCallback> addresses = Database.getValidMonitorFacilityRequests(facilityName);
        if (addresses == null) {
            return;
        }
        for (ClientCallback cInfo : addresses) {
            ServerResponse callbackRes = res.clone();
            callbackRes.setRequestChoice(responseType);
            callbackRes.setRequestId(cInfo.getRequestId());
            semInvo.replyClient(callbackRes, (InetSocketAddress) cInfo.getSocket());
        }
    }
}
