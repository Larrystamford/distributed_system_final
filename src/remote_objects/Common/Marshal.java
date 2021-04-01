package remote_objects.Common;

import marshal_handler.MarshalHandler;
import marshal_handler.UnmarshalHandler;

/**
 * This is the Parent Marshal class that allows all its children object to be marshalled and unmarshalled
 */
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
