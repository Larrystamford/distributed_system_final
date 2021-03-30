package main.java.network;

import main.java.remote_objects.Common.AddressAndData;

public class AtLeastOnceNetwork extends Network {

    public AtLeastOnceNetwork(UdpAgent communicator) {
        super(communicator);
    }

    @Override
    public boolean filterDuplicate(AddressAndData data) {
        return false;
    }

}
