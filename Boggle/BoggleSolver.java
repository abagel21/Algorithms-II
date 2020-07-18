import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

/**
 * ADT to calculate all solutions to a boggle board
 * utilizing the RSquaredTrie data type (that I wrote)
 */
public class BoggleSolver {
    private RSquaredTrie dict;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dict = new RSquaredTrie();
        for (String s : dictionary) {
            dict.insert(s);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        // traverse the board
        // prefix adjacents and check if they are words
        // for valid prefixes
        // continue to prefix and check for words from adjacents that are valid
        // add words to an arraylist

        ArrayList<String> validWords = new ArrayList<>();
        // traverse the board
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                // for every possible starting letter
                // prefix adjacents and get the two letter nodes connecting to the TSTs
                getTwoLetterNodes(i, j, validWords, board);
            }
        }
        return validWords;
    }

    private void getTwoLetterNodes(int i, int j, ArrayList<String> validWords, BoggleBoard board) {
        // find words for each two letter node and add any two letter nodes that are words
        char qu = 'Q';
        for (int x = i - 1; x < i + 2; x++) {
            for (int y = j - 1; y < j + 2; y++) {
                if (x == i && y == j) continue;
                if (x < 0 || y < 0) continue;
                if (x > board.rows() - 1 || y > board.cols() - 1) continue;
                // gets the two letter combo for this adjacency
                char l = board.getLetter(i, j);
                char o = board.getLetter(x, y);
                String start;
                // if one of the letters is q, calculate the two letter node for the substring of the first two letters, then prefix the whole string after u is appended to q
                // in both cases, it finds the two letter node, marks the starting node, and starts the recursion in the TSTs
                if (l == qu || o == qu) {
                    start = "" + (l - qu == 0 ? "QU" : l) + (o - qu == 0 ? "QU" : o);
                    RSquaredTrie.Node startingNode = dict.prefix(start.substring(0, 2));
                    if (startingNode != null) {
                        RSquaredTrie.Node quCheck = dict.get(start, startingNode, 2);
                        startingNode = dict.prefix(start, startingNode, 2);
                        if (startingNode != null) {
                            if (quCheck != null) {
                                if (!validWords.contains(start))
                                    validWords.add(start);
                            }
                            boolean[][] marked = new boolean[board.rows()][board.cols()];
                            // sets the starting position to be marked
                            marked[i][j] = true;
                            getWords(x, y, startingNode, start, validWords, board, 3, marked);
                        }
                    }
                } else {
                    start = "" + l + o;
                    // checks if that two letter combo is a word and adds it if so
                    // prefixes that two letter combo
                    RSquaredTrie.Node startingNode = dict.prefix(start);
                    // creates a marked array for that two letter combo (to avoid overlap after recursion)
                    if (startingNode != null) {
                        // creates new marked array for each two letter combo so they can separately keep track of marked squares
                        boolean[][] marked = new boolean[board.rows()][board.cols()];
                        // sets the starting position to be marked
                        marked[i][j] = true;
                        // starts tst recursion
                        getWords(x, y, startingNode, start, validWords, board, 2, marked);
                    }
                }
            }
        }
    }

    private void getWords(int i, int j, RSquaredTrie.Node s, String word, ArrayList<String> validWords, BoggleBoard board, int d, boolean[][] marked) {
        // now we should have all possible adjacent squares from the starting square
        // so we need to continue checking adjacent squares that aren't marked
        char qu = 'Q';
        marked[i][j] = true;
        // checking if prefixing failed
        if (s == null) {
            // unmarking current square if there are no further words possible on this recursive path
            marked[i][j] = false;
            return;
        } else {
            for (int x = i - 1; x < i + 2; x++) {
                for (int y = j - 1; y < j + 2; y++) {
                    // checking that square is in bounds and unmarked
                    // and square isn't original node
                    if (x == i && y == j) continue;
                    if (x < 0 || y < 0 || x > marked.length - 1 || y > marked[x].length - 1) continue;
                    if (marked[x][y]) {
                        continue;
                    }
                    // adding new square to word
                    String next = "" + word + board.getLetter(x, y);
                    // checking if string is a word
                    RSquaredTrie.Node node = dict.get(next, s, d);
                    // checking if new square is a q, and if so prefixing then adding u and continuing normally
                    if (board.getLetter(x, y) - qu == 0) {
                        RSquaredTrie.Node prefixNode = dict.prefix(next, s, d);
                        next = next + "U";
                        node = dict.get(next, prefixNode, d + 1);
                        // adding if next is a word and not already added
                        if (node != null) {
                            if (!validWords.contains(next))
                                validWords.add(next);
                        }
                        // recursively calling to prefix and check all unmarked adjacents,
                        // passing the next node for this prefix so getting and prefixing operations are faster (can start on current node rather than retracing
                        getWords(x, y, dict.prefix(next, prefixNode, d + 1), next, validWords, board, d + 2, marked);
                    } else {
                        // adding if next is a word and not already added
                        if (node != null) {
                            if (!validWords.contains(next))
                                validWords.add(next);
                        }
                        // recursively calling to prefix and check all unmarked adjacents,
                        // passing the next node for this prefix so getting and prefixing operations are faster (can start on current node rather than retracing
                        getWords(x, y, dict.prefix(next, s, d), next, validWords, board, d + 1, marked);
                    }
                }
            }
        }
        // setting the current node to false when all recursive calls on all adjacents have finished
        marked[i][j] = false;
    }


    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        boolean x = dict.get(word);
        if (x && word.length() > 2) {
            if (word.length() < 5) return 1;
            else if (word.length() == 5) return 2;
            else if (word.length() == 6) return 3;
            else if (word.length() == 7) return 5;
            else return 11;
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int counter = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
            counter++;
        }
        StdOut.println("Score = " + score);
        StdOut.println("Words = " + counter);
    }
}
