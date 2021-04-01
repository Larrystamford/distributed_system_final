package semantics;

import remote_objects.Common.AddressAndData;
import remote_objects.Server.ServerResponse;

import java.net.InetSocketAddress;

/**
 * At-most-once semantics
 * Duplicated data will re-execute the client's query
 */
public class AtLeastOnceSemantics extends Semantics {

    public AtLeastOnceSemantics(UdpAgent communicator) {
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
