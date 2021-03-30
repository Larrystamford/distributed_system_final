package main.java.network;

import main.java.remote_objects.Common.AddressAndData;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AtMostOnceNetwork extends Network {

    public AtMostOnceNetwork(UdpAgent communicator) {
        super(communicator);
    }
    private ConcurrentMap<String, Long> received = new ConcurrentHashMap<>();

    public boolean filterDuplicate(AddressAndData data) {
        String uniqueClientIdData = data.getOrigin().toString() + "-ID:" + data.getData().getId();

        if (!received.containsKey(uniqueClientIdData)) {
            received.put(uniqueClientIdData, System.currentTimeMillis());
            return false;
        } else {
            System.out.print("Repeated Request " + uniqueClientIdData + " is ignored.");
            return true;
        }
    }

}
