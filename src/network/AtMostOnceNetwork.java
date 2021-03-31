package network;

import remote_objects.Common.AddressAndData;
import remote_objects.Server.ServerResponse;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AtMostOnceNetwork extends Network {

    public AtMostOnceNetwork(UdpAgent communicator) {
        super(communicator);
    }
    private ConcurrentMap<String, Long> received = new ConcurrentHashMap<>();

    public boolean filterDuplicate(AddressAndData data) {
        String origin = data.getOrigin().toString();
        int clientId = data.getData().getId();
        String clientKey = genClientKey(origin, clientId);

        // client request not seen before
        if (!received.containsKey(clientKey)) {
            received.put(clientKey, System.currentTimeMillis());
            return false;
        } else {
            System.out.print("Repeated Request " + clientKey + " is ignored.");
            return true;
        }
    }

    @Override
    public void registerResponse(ServerResponse resp, InetSocketAddress socketAddress) {
        int clientQueryId = resp.getRequestId();
        String uniqueClientIdData = genClientKey(socketAddress.toString(), clientQueryId);
        System.out.println("response registered");
       generatedResponses.put(uniqueClientIdData, resp);
    }

}
