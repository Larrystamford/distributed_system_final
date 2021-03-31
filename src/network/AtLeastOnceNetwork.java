package network;

import remote_objects.Common.AddressAndData;
import remote_objects.Server.ServerResponse;

import java.net.InetSocketAddress;

public class AtLeastOnceNetwork extends Network {


    public AtLeastOnceNetwork(UdpAgent communicator) {
        super(communicator);
    }

    @Override
    public boolean filterDuplicate(AddressAndData data) {
        return false;
    }

    @Override
    protected void registerResponse(ServerResponse resp, InetSocketAddress dest) {

    }

}
