package network;

import remote_objects.Common.AddressAndData;
import remote_objects.Common.Marshal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Manages the sending and receiving of marshalled datagrams
 */
public class UdpAgent {
    private DatagramSocket dSocket;
    private InetSocketAddress serverSocket;
    private static final Logger logger = LoggerFactory.getLogger(UdpAgent.class);

    /**
     * for client to create communicator with server
     *
     * @param serverSocket - server socket (default 2222)
     */
    public UdpAgent(InetSocketAddress serverSocket) {
        try {
            dSocket = new DatagramSocket();
            this.serverSocket = serverSocket;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * for server to create communicator with client
     *
     * @param selfPortNumber - port number extracted from client query received
     */
    public UdpAgent(int selfPortNumber) {
        try {
            dSocket = new DatagramSocket(selfPortNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Marshal data, InetSocketAddress dest) {
        logger.info("Send: {}", data);
        byte[] byteArray = data.marshall();
        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, dest);

        try {
            this.dSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Communicator sending error");
            e.printStackTrace();
        }
    }

    public AddressAndData receive() {
        byte[] inputBuffer = new byte[5000];
        DatagramPacket p = new DatagramPacket(inputBuffer, inputBuffer.length);
        try {
            dSocket.receive(p);
            AddressAndData resp = new AddressAndData((InetSocketAddress) p.getSocketAddress(), Marshal.unmarshall(p.getData()));
            logger.info("Recv: {}", resp);
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getIPaddress() {
        String ret = null;
        try {
            ret = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;

    }

    InetSocketAddress getServerSocket() {
        return serverSocket;
    }

}
