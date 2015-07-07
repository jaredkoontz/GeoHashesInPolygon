package coords;

/**
 * Simple tuple class for holding arbitrary value pairs.
 *
 * @author malensek
 */
public class Pair<A, B> {
    public A a;
    public B b;

    public Pair() {
    }

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
