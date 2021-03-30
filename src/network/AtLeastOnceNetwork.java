package network;

import entity.Ack;
import entity.ClientQuery;
import entity.Response;
import entity.ServerResponse;

import java.util.function.Consumer;

public class AtLeastOnceNetwork extends Network {


    public AtLeastOnceNetwork(UDPCommunicator communicator) {
        super(communicator);
    }

    @Override
    protected boolean responseNeeded(Response data) {
        //Do nothing for at least once
        return true;
    }

}
