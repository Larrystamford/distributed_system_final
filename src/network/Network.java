package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remote_objects.Client.ClientRequest;
import remote_objects.Common.Ack;
import remote_objects.Common.AddressAndData;
import remote_objects.Common.Marshal;
import remote_objects.Server.ServerResponse;
import utils.Constants;
import utils.LRUCache;

import java.net.InetSocketAddress;
import java.net.SocketException;
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
    Map<String, ServerResponse> generatedResponses = new LRUCache<>(50);

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

    public String genClientKey(String socketAddress, int marshallableId) {
        return socketAddress + "-ID:" + marshallableId;
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

        for (int i = 0; i < Constants.DEFAULT_MAX_TRY; i++) {
            try {
                resp = communicator.receive();
            } catch (SocketException ignored) {
                // timeout
                System.out.println("FAILED TO RECEIVE " + i);
                if (!continuous) {
                    // max monitor duration has been reached
                    break;
                }
                continue;
            }

            if (resp.getData() instanceof ServerResponse) {
                sendAck(resp.getData().getId(), resp.getOrigin());
                callback.accept((ServerResponse) resp.getData());
                return;
            }

            if (!continuous) {
                break;
            }
        }

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
            } catch (SocketException ignored) {
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

        // keep trying to send until ack received
        for (int i = 0; i < Constants.DEFAULT_MAX_TRY; i++) {
            communicator.send(payload, dest);
            communicator.setSocketTimeout(Constants.DEFAULT_TIMEOUT); // set timeout
            try {
                Marshal respData = communicator.receive().getData();
                if (respData instanceof Ack && respData.getId() == id) {
                    break;
                }
            } catch (SocketException ignored) {
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
