import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Finds the least related term in a set
 */
public class Outcast {
    private WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxDist = 0;
        String maxNoun = "";
        // for all items in the nouns array
        // calculates the shortest ancestral path length for
        // all other items in the nouns array
        // and if it is larger than the current max
        // replaces it
        for (String n : nouns) {
            int sum = 0;
            for (String l : nouns) {
                sum += wordnet.distance(n, l);
            }
            if (sum > maxDist) {
                maxNoun = n;
                maxDist = sum;
            }
        }
        return maxNoun;
    }

    // see test client below
    public static void main(String[] args) {
        if (args == null) return;
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
