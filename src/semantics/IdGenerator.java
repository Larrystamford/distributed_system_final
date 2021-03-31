package semantics;

public class IdGenerator {
    private int id = 0;

    synchronized int get() {
        id++;
        return id;
    }
}
