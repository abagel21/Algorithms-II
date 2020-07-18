import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        // initializes "alphabet" array
        char[] count = new char[256];
        for (char i = 0; i < count.length; i++) {
            count[i] = i;
        }
        // reads in a character, finds its current location in the alphabet array
        // then writes its current index and moves it to the front
        while (!BinaryStdIn.isEmpty()) {
            // read input char
            char s = BinaryStdIn.readChar();
            int i;
            // find its location in alphabet array
            for (i = 0; i < count.length; i++) {
                if (count[i] == s) break;
            }
            // write its current index
            BinaryStdOut.write((char) i);
            // move the char to the front by swapping it with all chars in front of it
            while (i > 0) {
                char temp = count[i];
                count[i] = count[i - 1];
                count[i - 1] = temp;
                i--;
            }
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        // initialize "alphabet" array
        char[] count = new char[256];
        for (char i = 0; i < count.length; i++) {
            count[i] = i;
        }
        // takes input char, writes its current value
        // then moves it to the front
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(count[c]);
            char temp = count[c];
            for (int i = c; i > 0; i--) {
                count[i] = count[i - 1];
            }
            count[0] = temp;
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        String s = args[0];
        if (s.equals("-")) {
            encode();
        } else if (s.equals("+")) {
            decode();
        } else throw new IllegalArgumentException("must be either - or +");
    }

}
