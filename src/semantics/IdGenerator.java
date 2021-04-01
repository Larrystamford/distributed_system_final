package semantics;

/**
 * Generates IDs that allows the client and server to identify each other's marshalled object
 */
public class IdGenerator {
    private int id = 0;

    synchronized int get() {
        id++;
        return id;
    }
}
