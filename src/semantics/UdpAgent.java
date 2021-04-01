package semantics;

import remote_objects.Client.ClientRequest;
import remote_objects.Common.AddressAndData;
import remote_objects.Common.Marshal;
import remote_objects.Server.ServerResponse;

import java.io.IOException;
import java.net.*;

/**
 * Udp Agent is solely in charge of sending and receiving bytes
 */
public class UdpAgent {
    private DatagramSocket dSocket;
    private InetSocketAddress serverSocket;

    /**
     * The Client's UdpAgent
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
     * The Server's UdpAgent
     */
    public UdpAgent(int selfPortNumber) {
        try {
            dSocket = new DatagramSocket(selfPortNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSocketTimeout(int newTimeout) {
        try {
            this.dSocket.setSoTimeout(newTimeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(Marshal data, InetSocketAddress dest) {
        System.out.printf("PACKET SENT\t\t\tID: %s\tTYPE: %s\n", data.getId(), getPacketType(data));
        byte[] byteArray = data.marshal();
        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, dest);

        try {
            this.dSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Communicator sending error");
            e.printStackTrace();
        }
    }

    public AddressAndData receive() throws SocketTimeoutException {
        byte[] inputBuffer = new byte[20000];
        DatagramPacket p = new DatagramPacket(inputBuffer, inputBuffer.length);
        try {
            dSocket.receive(p);
            AddressAndData resp = new AddressAndData((InetSocketAddress) p.getSocketAddress(), Marshal.unmarshal(p.getData()));
            System.out.printf("PACKET RECEIVED\t\tID: %s\tTYPE: %s\n", resp.getData().getId(), getPacketType(resp.getData()));
            return resp;
        } catch (SocketTimeoutException s) {
            throw new SocketTimeoutException();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getPacketType(Marshal data) {
        String payload = "ACK";
        if (data instanceof ClientRequest)
            payload = "CLIENT REQUEST";
        else if (data instanceof ServerResponse)
            payload = "SERVER RESPONSE";
        return payload;
    }

    InetSocketAddress getServerSocket() {
        return serverSocket;
    }

}
