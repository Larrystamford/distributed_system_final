package network;

import entity.PacketInfo;
import entity.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AtMostOnceNetwork extends Network {

    public AtMostOnceNetwork(UDPCommunicator communicator) {
        super(communicator);
    }

    private ConcurrentMap<PacketInfo, Long> received = new ConcurrentHashMap<>();
    private final static Logger logger = LoggerFactory.getLogger(AtMostOnceNetwork.class);

    protected boolean responseNeeded(Response data) {
        // TODO - hash this right?
        PacketInfo key = new PacketInfo(data.getData().getId(), data.getOrigin());

        System.out.println("response id data " + data.getData().getId());
        System.out.println("response key data " + key);
        System.out.println("first time seeing response" + !received.containsKey(key));

        if (!received.containsKey(key)) {
            // first time seeing this response - response always needed
            received.put(key, System.currentTimeMillis());
            return true;
        } else {
            System.out.println("duplicated reponse received");

            long lastReceived = received.get(key);

            // after around 5 seconds, they reset the received hash map and return true to re run
            if (lastReceived < System.currentTimeMillis() - (3000 + SEND_TIMEOUT * MAX_TRY)) {
                // Given ping uncertainty of 3 sec, this is a different message, id have cycled
                received.put(key, System.currentTimeMillis());
                return true;
            }

            logger.info("Duplicate found: {}", data);
            return false;
        }
    }

}
