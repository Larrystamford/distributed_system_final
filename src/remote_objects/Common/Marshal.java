package remote_objects.Common;


import marshal_handler.Marshaller;

public class Marshal {
    int id;
    public byte[] marshall() {
        return Marshaller.marshall(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Marshal unmarshall(byte[] raw) {
        try {
            return (Marshal) Marshaller.unmarshall(raw);
        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends Marshal> T unmarshall(byte[] raw, Class<T> clazz) {
        return clazz.cast(unmarshall(raw));
    }
}
