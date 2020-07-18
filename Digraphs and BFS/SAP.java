import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;


/**
 * finds the shortest ancestral path and common ancestor on that path
 * utilizing the specialized BFS
 * Basically all methods just call the specialized BFS
 */
public class SAP {
    private Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Argument must not be null");
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (inRange(v) || inRange(w)) throw new IllegalArgumentException("Arguments must be in range");
        SAPBFS x = new SAPBFS(G, v, w);
        return x.getLength();
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (inRange(v) || inRange(w)) throw new IllegalArgumentException("Arguments must be in range");
        SAPBFS x = new SAPBFS(G, v, w);
        return x.getAncestor();
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Arguments can't be null");
        for (Integer n : v) {
            if (n == null || inRange(n)) throw new IllegalArgumentException("Arguments must be in range");
        }
        for (Integer n : w) {
            if (n == null || inRange(n)) throw new IllegalArgumentException("Arguments must be in range");
        }
        SAPBFS x = new SAPBFS(G, v, w);
        return x.getLength();
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Arguments can't be null");
        for (Integer n : v) {
            if (inRange(n)) throw new IllegalArgumentException("Arguments must be in range");
            if (n == null) throw new IllegalArgumentException("null");
        }
        for (Integer n : w) {
            if (inRange(n)) throw new IllegalArgumentException("Arguments must be in range");
            if (n == null) throw new IllegalArgumentException("null");
        }
        SAPBFS x = new SAPBFS(G, v, w);
        return x.getAncestor();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private boolean inRange(Integer v) {
        return v < 0 || v > G.V() - 1;
    }
}
