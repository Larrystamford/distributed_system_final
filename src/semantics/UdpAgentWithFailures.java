package semantics;

import remote_objects.Common.Marshal;

import java.net.InetSocketAddress;
import java.util.Random;

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
            System.out.printf("%s ID: %s WAS INTENTIONALLY FAILED TO SEND\n", getPacketType(data).toUpperCase(), data.getId());;
        }
    }
}
