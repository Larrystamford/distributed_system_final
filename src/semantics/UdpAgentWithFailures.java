package semantics;

import remote_objects.Common.Marshal;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * This class is for simulating the UDP failure
 * We generate a random float, and if the failure
 * rate is higher than this float, we do not send the data
 */
public class UdpAgentWithFailures extends UdpAgent {

    private double failProb;

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
            System.out.printf("%s WAS INTENTIONALLY FAILED TO SEND ID: %s", getPacketType(data).toUpperCase(), data.getId());;
        }
    }
}
