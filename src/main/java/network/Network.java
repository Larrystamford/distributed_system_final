package main.java.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import main.java.remote_objects.Client.ClientQuery;
import main.java.remote_objects.Common.Ack;
import main.java.remote_objects.Common.AddressAndData;
import main.java.remote_objects.Common.Marshal;
import main.java.remote_objects.Server.ServerResponse;
import main.java.utils.Constants;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A wrapper class built on top of the UDP communicator that
 * establishes a request-reply protocol
 * Implemented by AtMostOnce and AtLeastOnce network classes
 */
public class Network {
    UdpAgent communicator;
    private final IdGenerator idGen = new IdGenerator();
    private static final Logger logger = LoggerFactory.getLogger(Network.class);

    /**
     * responses the client expects from the server
     */
    ConcurrentMap<Integer, Consumer<ServerResponse>> callbacks = new ConcurrentHashMap<>();
    /**
     * threads in the client which are waiting for response from the server
     * more specifically, threads in network.send()
     */
    ConcurrentMap<Integer, Thread> threadsToBreak = new ConcurrentHashMap<>();
    /**
     * acknowledgements from either client or server
     */
    ConcurrentMap<Integer, Thread> acks = new ConcurrentHashMap<>();

    BiConsumer<InetSocketAddress, ClientQuery> serverAction;

    public Network(UdpAgent communicator) {
        this.communicator = communicator;
        runReceiver();
    }

    public boolean filterDuplicate(AddressAndData data) {
        // default to no filter
        return false;
    }

    private void runReceiver() {
        Thread t = new Thread(() -> {
            while (true) {
                // response from the other party
                AddressAndData resp = communicator.receive();

                if (resp.getData() instanceof Ack) {
                    Ack ack = (Ack) resp.getData();
                    Thread ackThread = acks.get(ack.getAckId());
                    if (ackThread == null) continue; // Already acknowledged and interrupted
                    acks.remove(ack.getAckId());
                    ackThread.interrupt(); // interrupt the client and server to stop sending messages
                } else {
                    // client and server both sends back an ack
                    sendAck(resp.getData().getId(), resp.getOrigin());

                    if (!filterDuplicate(resp)) {
                        if (resp.getData() instanceof ServerResponse) {
                            ServerResponse serverResponse = (ServerResponse) resp.getData();
                            System.out.println(serverResponse);
                            Consumer<ServerResponse> c = callbacks.get(serverResponse.getQueryId());
                            System.out.println(c);
                            if (c == null) {
                                continue; //Results were already displayed
                            }
                            // perform the operation in the client with this response as the argument
                            // and interrupt the receive thread in client (if not monitoring)
                            c.accept(serverResponse);
                            if (threadsToBreak.containsKey(serverResponse.getQueryId())) {
                                threadsToBreak.get(serverResponse.getQueryId()).interrupt();
                            }
                        } else if (resp.getData() instanceof ClientQuery) {
                            // this determines how many times the server is ran
                            ClientQuery clientQuery = (ClientQuery) resp.getData();
                            serverAction.accept(resp.getOrigin(), clientQuery);
                        }
                    }
                }
            }
        });
        t.start();
    }

    void sendAck(int ackId, InetSocketAddress dest) {
        Ack ack = new Ack(ackId); // the request / respond ID is the ackId

        // luke - can we remove this?
        int id = idGen.get();
        ack.setId(id);
        //

        communicator.send(ack, dest);
    }

    // client side receive
    public void receive(int id, Consumer<ServerResponse> callback, boolean continuous, int blockTime) {
        callbacks.put(id, callback);
        if (!continuous) {
            // everything but monitoring services
            threadsToBreak.put(id, Thread.currentThread());
        }

        // for monitoring, no interrupt will be thrown since the request has not been put in threadsToBreak
        try {
            Thread.sleep(blockTime * 1000);
        } catch (InterruptedException ignored) {
            // received and display, operation ends.
            return;
        } finally {
            callbacks.remove(id);
            threadsToBreak.remove(id);
        }

        // if time out
        if (!continuous) {
            logger.error("Failed to retrieve results. Internal Server error and time out.");
            callback.accept(new ServerResponse(id, 500, null));
        }
    }


    // server side receive
    public void receive(BiConsumer<InetSocketAddress, ClientQuery> serverOps) {
        this.serverAction = serverOps;
        Thread.yield();
    }

    // both server and client use this
    public int send(Marshal data, InetSocketAddress dest) {
        int id = idGen.get();

        data.setId(id);
        // a new thread with the following run function is created to send messages
        // message is sent multiple times until the thread is interrupted
        // or it times out
        // acknowledgements will interrupt this thread
        Thread t = new Thread(() -> {
            try {
                for (int i = 0; i < Constants.DEFAULT_MAX_TRY; i++) {
                    communicator.send(data, dest);
                    Thread.sleep(Constants.DEFAULT_TIMEOUT);
                }
                logger.error("Failed to send after {} tries: {}", Constants.DEFAULT_MAX_TRY, data);
                acks.remove(id);
            } catch (InterruptedException ignored) {
                // when the correct ack is received, sending is interrupted and we can stop sending req / respond
            }
        });

        // hashmap of threads
        acks.put(id, t);

        // calls the run function in a thread
        t.start();

        return id;
    }

    public int send(Marshal data) {
        // client calls the send func above with the server socket address
        return send(data, communicator.getServerSocket());
    }

}
