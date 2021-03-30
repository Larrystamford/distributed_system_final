package remote_objects.Common;

import marshal_handler.MarshalHandler;
import marshal_handler.UnmarshalHandler;

public class Marshal {
    int id;

    public byte[] marshall() {
        return MarshalHandler.marshall(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Marshal unmarshall(byte[] raw) {
        try {
            return (Marshal) UnmarshalHandler.unmarshall(raw);
        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
}
