import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Data type that store the actual words in a word net in a digraph
 * with all locations of a word in a hashtable
 * and all synsets in an arraylist
 */
public class WordNet {
    private Hashtable<String, ArrayList<Integer>> words;
    private ArrayList<String> syns;
    private Digraph G;
    private SAP x;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException("Arguments must not be null");
        // initializes
        this.words = new Hashtable<>();
        this.syns = new ArrayList<String>();
        In in = new In(synsets);
        // splits input and adds it to synsets and hashtable
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] items = line.split(",");
            syns.add(items[1]);
            String[] words = items[1].split(" ");
            for (int i = 0; i < words.length; i++) {
                ArrayList<Integer> x = this.words.putIfAbsent(words[i], new ArrayList<Integer>());
                if (x == null) {
                    this.words.get(words[i]).add(Integer.parseInt(items[0]));
                } else {
                    x.add(Integer.parseInt(items[0]));
                }
            }
        }
        // initializes digraph
        G = new Digraph(syns.size());
        in = new In(hypernyms);
        // adds edges between hypernyms
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] items = line.split(",");
            for (int i = 1; i < items.length; i++) {
                G.addEdge(Integer.parseInt(items[0]), Integer.parseInt(items[i]));
            }
        }
        // checks if digraph is rooted
        int rooted = 0;
        for (int i = 0; i < syns.size(); i++) {
            if (G.outdegree(i) == 0) rooted++;
        }
        if (!(rooted == 1)) throw new IllegalArgumentException("DAG must be rooted");
        // checks that DAG is actually a DAG
        DirectedCycle checkDAG = new DirectedCycle(G);
        if (checkDAG.hasCycle()) throw new IllegalArgumentException("WordNet must be a DAG");
        this.x = new SAP(G);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return words.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("Arguments must not be null");
        return words.get(word) != null;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Arguments must not be null");
        if (!this.isNoun(nounA) || !this.isNoun(nounB))
            throw new IllegalArgumentException("Noun must be part of WordNet");
        ArrayList<Integer> v = words.get(nounA);
        ArrayList<Integer> u = words.get(nounB);
        // finds shortest ancestral path between nouns using all occurences of them in the graph
        return x.length(v, u);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Arguments must not be null");
        if (!this.isNoun(nounA) || !this.isNoun(nounB))
            throw new IllegalArgumentException("Noun must be part of WordNet");
        ArrayList<Integer> v = words.get(nounA);
        ArrayList<Integer> u = words.get(nounB);
        // finds shortest ancestral path's common ancestor between nouns using all occurences of them in the graph
        return syns.get(x.ancestor(v, u));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // not sure what to test yet
    }
}
