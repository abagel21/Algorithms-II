import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {
    private Integer[] index;
    private String words;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("No null arguments");
        index = new Integer[s.length()];
        words = s;
        // add initial suffix indices
        for (int i = 0; i < index.length; i++) {
            index[i] = i;
        }
        // sort the index string by the suffixes (but don't explicitly form them, compare characters that would be in the suffix
        Arrays.sort(index, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                int x = a;
                int y = b;
                // compare what would be the created suffix
                for (int i = 0; i < words.length(); i++) {
                    if (x == words.length()) x = 0;
                    if (y == words.length()) y = 0;
                    if (words.charAt(x) > words.charAt(y))
                        return 1;
                    else if (words.charAt(y) > words.charAt(x))
                        return -1;
                    x++;
                    y++;
                }
                return 0;
            }
        });
    }

    // length of s
    public int length() {
        return words.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > index.length - 1) throw new IllegalArgumentException("Index must be in range");
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray x = new CircularSuffixArray("Abracadabra!");
        StdOut.println(x.length());
        StdOut.println("Indexes of Circular Suffix Array");
        for (int i = 0; i < "Abracadabra!".length(); i++) {
            StdOut.println(x.index(i));
        }
    }
}
