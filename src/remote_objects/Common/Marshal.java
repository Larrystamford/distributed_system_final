package remote_objects.Common;

import marshal_handler.MarshalHandler;
import marshal_handler.UnmarshalHandler;

public class Marshal {
    int id;

    public byte[] marshal() {
        return MarshalHandler.marshal(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Marshal unmarshal(byte[] byteList) {
        try {
            return UnmarshalHandler.unmarshal(byteList);
        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
}
