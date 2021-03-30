package network;
import java.io.File;

public class IdContainer {
    private int id = 0;

    synchronized int get() {
        id++;
        return id;
    }
}
