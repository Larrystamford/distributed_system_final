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
    Map<Integer, AddressAndData> monitorResponses = new LRUCache<>(50); // client side

    public Network(UdpAgent communicator) {
        this.communicator = communicator;
    }

    public abstract boolean filterDuplicate(AddressAndData data);
    protected abstract void registerResponse(ServerResponse resp, InetSocketAddress dest);

    /**
     * Unique key generation on client request
     * @param socketAddress - client's socket address
     * @param marshalId - client request marshallable id
     * @return unique key
     */
    public String genClientKey(String socketAddress, int marshalId) {
        return socketAddress + "-ID:" + marshalId;
    }

    /**
     * Send acknowledgement to the sender's socket address
     * @param ackId - request or response id
     * @param dest - socket to which the acknowledgement is addressed
     */
    void sendAck(int ackId, InetSocketAddress dest) {
        Ack ack = new Ack();
        ack.setId(ackId); // we set ackId to the same Id that belongs to the client request or server response
        communicator.send(ack, dest);
    }

    /**
     * Receive server responses. Continues to attempt receiving until a response is
     * received or the maximum number of retries has been reached.
     *
     * If the response with the same request id has been already received earlier,
     * perform the promise with the stored response.
     *
     * @param id - client request id
     * @param callback - promise to be performed once a server response is received
     * @param continuous - set to true when monitoring
     * @param blockTime - the sleep time of the datagram socket between retries
     */
    public void receiveResponse(int id, Consumer<ServerResponse> callback, boolean continuous, int blockTime) {
        AddressAndData resp;

        // response has already been received before the client received an ack
        // retrieve response and perform the promise on the stored response
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

        // TODO - change blocktimes in the handlers
        int timeout = 500;
        communicator.setSocketTimeout(timeout); // set socket timeout
        for (int i = 0; i < Constants.DEFAULT_MAX_TRY; i++) {
            try {
                resp = communicator.receive();

            } catch (SocketTimeoutException ignored) {
                // timeout
                System.out.println("Failed to receive server response on client " + i);
                continue;
            }

            // response received
            if (resp.getData() instanceof ServerResponse) {
                sendAck(resp.getData().getId(), resp.getOrigin());
                callback.accept((ServerResponse) resp.getData());
                communicator.setSocketTimeout(0); // unset socket timeout
                return;
            }
        }

        communicator.setSocketTimeout(0); // unset socket timeout

        // if time out
        if (!continuous) {
            logger.error("Failed to retrieve results. Internal Server error and time out.");
            callback.accept(new ServerResponse(id, 500, null));
        }
    }

    /**
     * Receive server responses for monitoring applications. Continues to attempt receiving
     * until the timeout period is over or, in the case of monitor and book on vacancy,
     * stops receiving upon receiving a response.
     *
     * If the response with the same response id has been already received earlier, ignore it
     *
     * @param callback - promise to be performed once a server response is received
     * @param continuous - set to true when monitoring
     * @param blockTime - the sleep time of the datagram socket between retries
     */
    public void monitorServer(Consumer<ServerResponse> callback, boolean continuous, int blockTime) {
        communicator.setSocketTimeout(blockTime);
        while (true) {
            try {
                AddressAndData resp = communicator.receive();
                sendAck(resp.getData().getId(), resp.getOrigin());
                if (!monitorResponses.containsKey(resp.getData().getId())) {
                    callback.accept((ServerResponse) resp.getData());
                    monitorResponses.put(((ServerResponse) resp.getData()).getRequestId(), resp);
                }
                if (continuous) {
                    break;
                }
            } catch (SocketTimeoutException ignored) {
                break;
            }
        }
    }

    /**
     * Receive client requests. When using at most once semantics invocation,
     * requests are filtered and duplicate requests will be replied with stored
     * server responses to the earliest request (of the same id) received
     *
     * @param serverOps - promise to be performed by the server
     */
    public void receiveClientRequest(BiConsumer<InetSocketAddress, ClientRequest> serverOps) {
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
                System.out.println("duplicates handle");
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

    /**
     * Sends server response to the socket address supplied. Calls UDPAgent
     * to perform the marshalling and sending of data. Continues sending data
     * until an acknowledgement is received from the client.
     *
     * When a client query is received instead of an ack, acknowledge the
     * query and continue trying to send the server response.
     *
     * @param response - response to be sent to client
     * @param dest    - client's socket address
     * @return marshallable id
     */
    public int replyClient(ServerResponse response, InetSocketAddress dest) {
        int id = idGen.get();
        response.setId(id);
        // store server's generated response to send the client should the
        // same client request come in. (Response will only be extracted on
        // at most once network)
        registerResponse(response, dest);

        AddressAndData resp;
        Marshal data;

        communicator.setSocketTimeout(Constants.DEFAULT_TIMEOUT); // set timeout

        // keep trying to send until ack received
        for (int i = 0; i < Constants.DEFAULT_MAX_TRY; i++) {
            communicator.send(response, dest);

            try {
                // attempt to get ack
                resp = communicator.receive();
                data = resp.getData();

            } catch (SocketTimeoutException ignored) {
                // timeout occurred, will try to send again if server has tried less than
                // Constants.DEFAULT_MAX_TRY number of times
                System.out.printf("Failed to receive ack on server send %d\n", i);
                continue;
            }

            // if ack received, stop sending
            if (data instanceof Ack && data.getId() == id) {
                System.out.println("server received acknowledgement from other party");
                break;
            } else if (data instanceof ClientRequest && data.getId() == response.getRequestId()) {
                // if server has began performing the client's query but the client failed to
                // receive the previous ack and has continued sending the same client query,
                // send another ack to the client
                sendAck(data.getId(), resp.getOrigin());
            }
        }
        communicator.setSocketTimeout(0); // unset timeout
        return id;
    }

    /**
     * Sends the client query to the server socket supplied. Calls UDP Agent
     * to perform the marshalling and sending of data. Continues sending data
     * until an acknowledgement is received from the server.
     *
     * When a server response is received before an ack, the client also stops
     * sending the request and stores the server's response.
     *
     * @param request - client request
     * @return marshallable id
     */
    public int requestServer(ClientRequest request) {
        InetSocketAddress serverSocket = communicator.getServerSocket();
        int id = idGen.get();
        request.setId(id);

        AddressAndData resp;
        Marshal data;

        communicator.setSocketTimeout(Constants.DEFAULT_TIMEOUT); // set timeout

        // keep trying to send until ack received
        for (int i = 0; i < Constants.DEFAULT_MAX_TRY; i++) {
            communicator.send(request, serverSocket);
            try {
                // attempt to get ack
                resp = communicator.receive();
                data = resp.getData();


            } catch (SocketTimeoutException ignored) {
                // timeout occurred, will try to send again if client has tried less than
                // Constants.DEFAULT_MAX_TRY number of times
                System.out.printf("Failed to receive ack on client send %d", i);
                continue;
            }

            // if ack received, stop sending
            if (data instanceof Ack && data.getId() == id) {
                System.out.println("client received acknowledgement from the server");
                break;
            } else if (data instanceof ServerResponse) {
                // if client expected an ack but received a ServerResponse, stop sending
                // the client request and store the server's response in storedResponses
                storedResponses.put(((ServerResponse) data).getRequestId(), resp);
                break;
            }
        }
        communicator.setSocketTimeout(0); // unset timeout
        return id;
    }

}
