package network;

import client.ClientUI;
import client.handlers.OffsetBooking;
import database.database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.Booking;
import remote_objects.Common.DayAndTime;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

class NetworkTest {

    Network client;
    Network atLeastOnceServer;
    Network atMostOnceServer;
    database database;

    InetSocketAddress clientSocket = new InetSocketAddress("127.0.0.1", 2222);

    @BeforeEach
    void setup() {
        UdpAgent clientCommunicator = new UdpAgentWithFailures(clientSocket, 0.7);
        client = new AtLeastOnceNetwork(clientCommunicator);

        UdpAgent serverCommunicator = new UdpAgentWithFailures(2222, 0.7);
        atLeastOnceServer = new AtLeastOnceNetwork(serverCommunicator);
        atMostOnceServer = new AtLeastOnceNetwork(serverCommunicator);

        database = new database();
    }
    @Test
    void set() {
        System.out.println("hello");
    }

    @Test
    void idempotentTest_sendRepeatedClientRequests_shouldOnlyReplyWithSameId(){
//        DayAndTime d1 = new DayAndTime(1, 2, 23);
//        DayAndTime d2 = new DayAndTime(1, 12, 12);
        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setOffset(new DayAndTime(0, 2, 0));
        bookings.add(booking);
        ClientRequest cReq = new ClientRequest(3, bookings);

        Thread clientThread = new Thread(() -> {
            int id = client.send(cReq);
            client.receive(id, (response) -> {
                if (response.getStatus() == 200) {
                    OffsetBooking.printChangeBookingSuccess(cReq, response);
                } else {
                    ClientUI.ServerErrorUI(response);
                }
            }, false, 5);
        });

        Thread serverThread = new Thread(() -> {
            server.handlers.OffsetBooking.handleRequest(atMostOnceServer, clientSocket, database, cReq);

        });

        clientThread.start();
        serverThread.start();
    }

}
