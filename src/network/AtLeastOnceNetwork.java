package network;

import remote_objects.Common.AddressAndData;

public class AtLeastOnceNetwork extends Network {


    public AtLeastOnceNetwork(UdpAgent communicator) {
        super(communicator);
    }

    @Override
    public boolean filterDuplicate(AddressAndData data) {
        return false;
    }

}
