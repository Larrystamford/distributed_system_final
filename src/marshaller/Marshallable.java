package marshaller;
import java.io.File;


public class Marshallable {
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

    public static Marshallable unmarshall(byte[] raw) {
        try {
            return (Marshallable) Marshaller.unmarshall(raw);
        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends Marshallable> T unmarshall(byte[] raw, Class<T> clazz) {
        return clazz.cast(unmarshall(raw));
    }
}
