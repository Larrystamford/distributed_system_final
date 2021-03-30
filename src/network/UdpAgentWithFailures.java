package network;

import remote_objects.Common.Marshal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Random;

public class UdpAgentWithFailures extends UdpAgent {

    private double failProb;
    private static final Logger logger = LoggerFactory.getLogger(UdpAgentWithFailures.class);

    private Random random = new Random(System.currentTimeMillis());

    public UdpAgentWithFailures(InetSocketAddress socketAddress, double failProb) {
        super(socketAddress);
        this.failProb = failProb;
    }

    public UdpAgentWithFailures(int selfPortNumber, double failProb) {
        super(selfPortNumber);
        this.failProb = failProb;
    }

    public void send(Marshal data, InetSocketAddress dest) {

        float limit = random.nextFloat();
        if (limit >= failProb) {
            super.send(data, dest);
        } else {
            logger.info("Sending failure simulated for {}", data);
        }
    }
}
