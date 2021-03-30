package main.java.remote_objects.Common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import main.java.marshal_handler.MarshalHandler;
import main.java.marshal_handler.UnmarshalHandler;

@Getter
@Setter
@ToString
public class Marshal {
    int id;

    public byte[] marshall() {
        return MarshalHandler.marshall(this);
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
