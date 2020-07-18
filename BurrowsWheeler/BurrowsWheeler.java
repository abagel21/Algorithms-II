import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        // takes in input string
        String s = BinaryStdIn.readString();
        // creates a circular suffix array for that string
        CircularSuffixArray x = new CircularSuffixArray(s);
        int first = 0;
        char[] l = new char[s.length()];
        // without actually calculating the suffixes, constructs the encoded string
        // by finding the last letter of the string in each position of the index array
        for (int i = 0; i < x.length(); i++) {
            if (x.index(i) == 0) {
                first = i;
                l[i] = s.charAt(x.length() - 1);
                continue;
            }
            l[i] = s.charAt(x.index(i) - 1);
        }
        // prints out the encoded string in binary
        BinaryStdOut.write(first);
        for (char p : l) {
            BinaryStdOut.write(p);
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        // read input
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        // initialize next
        int[] next = new int[s.length()];
        // initialize array of first characters in the circular suffix array
        // (because first letters are just the last letters in sorted order)
        char[] sorted = s.toCharArray();
        Arrays.sort(sorted);
        // initialize array for key indexed counting
        int[] count = new int[257];
        // use key indexed counting to find position of i in next
        // taking advantage of the fact that we can locate the current first letter in the last letter string
        // because the first letters are the sorted positions of the last letters
        // and key indexed counting is linear in time
        for (int i = 0; i < sorted.length; i++) {
            count[s.charAt(i) + 1]++;
        }
        for (int i = 0; i < count.length - 1; i++) {
            count[i + 1] += count[i];
        }
        for (int i = 0; i < sorted.length; i++) {
            next[count[s.charAt(i)]++] = i;
        }

        // print the decoded values following the next array
        int p = first;
        for (int i = 0; i < s.length(); i++) {
            BinaryStdOut.write(sorted[p]);
            p = next[p];
        }
        BinaryStdOut.close(); //very important!
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("arg must equal + or -");
    }
}
