package network;

import client.ClientUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.Ack;
import remote_objects.Common.AddressAndData;
import remote_objects.Common.Marshal;
import remote_objects.Server.ServerResponse;
import server.Server;
import utils.Constants;
import utils.LRUCache;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A wrapper class built on top of the UDP communicator that establishes a
 * request-reply protocol Implemented by AtMostOnce and AtLeastOnce network
 * classes
 */
public abstract class Network {
    UdpAgent communicator;
    private final IdGenerator idGen = new IdGenerator();
    private static final Logger logger = LoggerFactory.getLogger(Network.class);
    Map<String, ServerResponse> generatedResponses = new LRUCache<>(50); // server side
    Map<Integer, AddressAndData> storedResponses = new LRUCache<>(50); // client side

    /**
     * responses the client expects from the server
     */
    Map<Integer, Consumer<ServerResponse>> callbacks = new HashMap<>();
    /**
     * threads in the client which are waiting for response from the server more
     * specifically, threads in network.send()
     */

    BiConsumer<InetSocketAddress, ClientRequest> serverAction;


    public Network(UdpAgent communicator) {
        this.communicator = communicator;
    }

    public abstract boolean filterDuplicate(AddressAndData data);
    protected abstract void registerResponse(ServerResponse resp, InetSocketAddress dest);

    public String genClientKey(String socketAddress, int marshalId) {
        return socketAddress + "-ID:" + marshalId;
    }

    void sendAck(int ackId, InetSocketAddress dest) {
        Ack ack = new Ack();
        ack.setId(ackId); // we set ackId to the same Id that belongs to the client request or server response
        communicator.send(ack, dest);
    }

    // client side receive
    public void receive(int id, Consumer<ServerResponse> callback, boolean continuous, int blockTime) {
        callbacks.put(id, callback);
        AddressAndData resp;

        // response has already been received
        if (storedResponses.containsKey(id)) {
            AddressAndData storedResp = storedResponses.get(id);
            System.out.println("Server response was stored with response id" + storedResp.getData().getId());
            sendAck(storedResp.getData().getId(), storedResp.getOrigin());
            callback.accept((ServerResponse) storedResp.getData());
            communicator.setSocketTimeout(0); // unset socket timeout
            System.out.println("Server response with id :" + id + "was removed");
            storedResponses.remove(id);
            return;
        }

        int timeout = continuous ? blockTime : 500;
        communicator.setSocketTimeout(timeout); // set socket timeout
        for (int i = 0; i < Constants.DEFAULT_MAX_TRY; i++) {
            try {
                resp = communicator.receive();

            } catch (SocketTimeoutException ignored) {
                // timeout
                System.out.println("Failed to receive server response on client " + i);
                if (continuous) {
                    // max monitor duration has been reached
                    break;
                }
                continue;
            }

            if (resp.getData() instanceof ServerResponse) {
                sendAck(resp.getData().getId(), resp.getOrigin());
                callback.accept((ServerResponse) resp.getData());
                communicator.setSocketTimeout(0); // unset socket timeout
                return;
            }

            if (!continuous) {
                break;
            }
        }

        communicator.setSocketTimeout(0); // unset socket timeout

        // if time out
        if (!continuous) {
            logger.error("Failed to retrieve results. Internal Server error and time out.");
            callback.accept(new ServerResponse(id, 500, null));
        }
    }

    // server side receive
    public void receive(BiConsumer<InetSocketAddress, ClientRequest> serverOps) {
        AddressAndData clientRequest;
        while (true) {
            try {
                clientRequest = communicator.receive();
            } catch (SocketTimeoutException ignored) {
                // should never happen as timeout is currently at infinity
                // but we need the throws SocketException in the sending function
                continue;
            }

            // filter duplicates and resend stored responses data
            // only used in at most once network
            if (filterDuplicate(clientRequest)) {
                InetSocketAddress origin = clientRequest.getOrigin();
                int clientId = clientRequest.getData().getId();

                String clientKey = genClientKey(origin.toString(), clientId);

                if (generatedResponses.get(clientKey) != null) {
                    communicator.send(generatedResponses.get(clientKey), origin);
                }
                continue;
            }

            if (clientRequest.getData() instanceof ClientRequest) {
                sendAck(clientRequest.getData().getId(), clientRequest.getOrigin());
                serverOps.accept(clientRequest.getOrigin(), (ClientRequest) clientRequest.getData());
            }
        }
    }

    // both server and client use this
    public int send(Marshal payload, InetSocketAddress dest) {

        int id = idGen.get();
        payload.setId(id);

        if (payload instanceof ServerResponse) {
            registerResponse((ServerResponse) payload, dest);
        }

        communicator.setSocketTimeout(Constants.DEFAULT_TIMEOUT); // set timeout

        // keep trying to send until ack received
        for (int i = 0; i < Constants.DEFAULT_MAX_TRY; i++) {
            communicator.send(payload, dest);
            try {
                AddressAndData resp = communicator.receive();
                if (resp.getData() instanceof Ack && resp.getData().getId() == id) {
                    String curr = (payload instanceof ClientRequest) ? "client" : "server";
                    System.out.println(curr + " received acknowledgement from other party");
                    break;
                } else if (resp.getData() instanceof ServerResponse){
                    storedResponses.put(((ServerResponse) resp.getData()).getRequestId(), resp);
                    break;
                }
                break;
            } catch (SocketTimeoutException ignored) {
                if (payload instanceof ClientRequest)
                    System.out.println("Failed to receive ack on client send " + i);
                else
                    System.out.println("Failed to receive ack on server send " + i);
                // timeout
            }
        }

        communicator.setSocketTimeout(0); // unset timeout
        return id;
    }

    public int send(Marshal data) {
        // client calls the send func above with the server socket address
        return send(data, communicator.getServerSocket());
    }

}
