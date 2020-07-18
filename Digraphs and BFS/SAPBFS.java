import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

/**
 * Special BFS abstract data type
 * designed for calculating the shortest ancestral path in a digraph
 */
public class SAPBFS {
    private Digraph G;
    private Queue<Integer> nodes;
    private boolean[] markedV;
    private boolean[] markedU;
    private int[] distToV;
    private int[] distToU;
    private int[] prevNodeV;
    private int[] prevNodeU;
    private int commonAncestor;
    private int ancestralPath;

    public SAPBFS(Digraph G, int v, int u) {
        // initializes all the arrays, digraph, and queue
        this.G = new Digraph(G);
        markedV = new boolean[G.V()];
        markedU = new boolean[G.V()];
        distToV = new int[G.V()];
        distToU = new int[G.V()];
        prevNodeV = new int[G.V()];
        prevNodeU = new int[G.V()];
        nodes = new Queue<>();
        this.commonAncestor = -1;
        // begins recursive breadth first search
        this.BFS(v, u);
    }

    public SAPBFS(Digraph G, Iterable<Integer> v, Iterable<Integer> u) {
        if (v == null || u == null) throw new IllegalArgumentException("Arguments can't be null");
        // initializes all the arrays, digraph, and queue
        this.G = new Digraph(G);
        markedV = new boolean[G.V()];
        markedU = new boolean[G.V()];
        distToV = new int[G.V()];
        distToU = new int[G.V()];
        prevNodeV = new int[G.V()];
        prevNodeU = new int[G.V()];
        nodes = new Queue<Integer>();
        this.commonAncestor = -1;
        // begins recursive breadth first search
        this.BFS(v, u);
    }

    private void BFS(int v, int u) {
        // adds starting nodes to queue
        nodes.enqueue(u);
        nodes.enqueue(v);
        // sets their requisite distTo, marked, and prevNode values
        distToU[u] = 0;
        distToV[v] = 0;

        markedU[u] = true;
        markedV[v] = true;

        prevNodeU[u] = u;
        prevNodeV[v] = v;

        int y;
        // while queue isn't empty, so while there are remaining unvisited nodes from either BFS
        while (!nodes.isEmpty()) {
            // dequeues node, following BFS procedure
            y = nodes.dequeue();
            // checking if BFS from both starting nodes has reached the current node
            if (markedV[y] && markedU[y]) {
                // if so, traces the prevNode arrays to count and calculate the path
                int n = y;
                int counterV = 0;
                while (n != v) {
                    n = prevNodeV[n];
                    counterV++;
                }
                int l = y;
                int counterU = 0;
                while (l != u) {
                    l = prevNodeU[l];
                    counterU++;
                }
                // sets the value of common ancestor
                if (commonAncestor == -1) {
                    commonAncestor = y;
//                    ancestralPath = distToV[commonAncestor] + distToU[commonAncestor];
                    ancestralPath = counterU + counterV;
                } else if (distToV[y] + distToU[y] < distToV[commonAncestor] + distToU[commonAncestor]) {
                    commonAncestor = y;
//                    ancestralPath = distToV[commonAncestor] + distToU[commonAncestor];
                    ancestralPath = counterU + counterV;
                }
            }
            // checks whether this path is part of the V BFS, and if so adds its adjacents and sets its marked, distTo, and prevNode values
            if (markedV[y]) {
                for (int w : G.adj(y)) {
                    if (!markedV[w]) {
                        nodes.enqueue(w);
                        markedV[w] = true;
                        distToV[w] = distToV[y] + 1;
                        prevNodeV[w] = y;
                    }
                }
            }
            // checks whether this path is part of the U BFS, and if so adds its adjacents and sets its marked, distTo, and prevNode values
            if (markedU[y]) {
                for (int w : G.adj(y)) {
                    if (!markedU[w]) {
                        nodes.enqueue(w);
                        markedU[w] = true;
                        distToU[w] = distToU[y] + 1;
                        prevNodeU[w] = y;
                    }
                }
            }
        }
    }

    private void BFS(Iterable<Integer> v, Iterable<Integer> u) {
        // adding all v nodes and setting their initial values
        for (int n : v) {
            nodes.enqueue(n);
            distToV[n] = 0;
            markedV[n] = true;
        }
        // adding all u nodes and setting their initial values
        for (int n : u) {
            nodes.enqueue(n);
            distToU[n] = 0;
            markedU[n] = true;
        }
        int x;
        // while queue isn't empty, so while there are remaining unvisited nodes from either BFS
        while (!nodes.isEmpty()) {
            // dequeues node, following BFS procedure
            x = nodes.dequeue();
            // checking if BFS from both starting nodes has reached the current node
            if (markedV[x] && markedU[x]) {
                // if so, traces the prevNode arrays to count and calculate the path
                int n = x;
                int counterV = 0;
                while (!this.contains(v, n)) {
                    n = prevNodeV[n];
                    counterV++;
                }
                int l = x;
                int counterU = 0;
                while (!this.contains(u, l)) {
                    l = prevNodeU[l];
                    counterU++;
                }
                if (commonAncestor == -1) {
                    commonAncestor = x;
//                    ancestralPath = distToV[commonAncestor] + distToU[commonAncestor];
                    ancestralPath = counterU + counterV;
                } else if (distToV[x] + distToU[x] < distToV[commonAncestor] + distToU[commonAncestor]) {
                    commonAncestor = x;
//                    ancestralPath = distToV[commonAncestor] + distToU[commonAncestor];
                    ancestralPath = counterU + counterV;
                }
            }
            // checks whether this path is part of the V BFS, and if so adds its adjacents and sets its marked, distTo, and prevNode values
            if (markedV[x]) {
                for (int w : G.adj(x)) {
                    if (!markedV[w]) {
                        nodes.enqueue(w);
                        markedV[w] = true;
                        distToV[w] = distToV[x] + 1;
                        prevNodeV[w] = x;
                    }
                }
            }
            // checks whether this path is part of the U BFS, and if so adds its adjacents and sets its marked, distTo, and prevNode values
            if (markedU[x]) {
                for (int w : G.adj(x)) {
                    if (!markedU[w]) {
                        nodes.enqueue(w);
                        markedU[w] = true;
                        distToU[w] = distToU[x] + 1;
                        prevNodeU[w] = x;
                    }
                }
            }
        }
    }

    // checks if the current node is one of the starting nodes while tracing back the path
    private boolean contains(Iterable<Integer> a, int b) {
        for (int p : a) {
            if (p == b) return true;
        }
        return false;
    }

    // returns the common ancestor on the shortest ancestral path
    public int getAncestor() {
        return commonAncestor;
    }

    // returns the length of the shortest ancestral path
    public int getLength() {
        if (commonAncestor == -1) return -1;
        return ancestralPath;
    }
}
