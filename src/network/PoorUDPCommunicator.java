package network;

import marshaller.Marshallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Random;

public class PoorUDPCommunicator extends UDPCommunicator {

    private double failProb;
    private static final Logger logger = LoggerFactory.getLogger(PoorUDPCommunicator.class);

    private Random random = new Random(System.currentTimeMillis());

    public PoorUDPCommunicator(InetSocketAddress socketAddress, double failProb) {
        super(socketAddress);
        this.failProb = failProb;
    }

    public PoorUDPCommunicator(int selfPortNumber, double failProb) {
        super(selfPortNumber);
        this.failProb = failProb;
    }

    public void send(Marshallable data, InetSocketAddress dest) {

        float limit = random.nextFloat();
        if (limit >= failProb) {
            super.send(data, dest);
        } else {
            logger.info("Sending failure simulated for {}", data);
        }
    }
}
