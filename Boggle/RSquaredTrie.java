import edu.princeton.cs.algs4.In;


/**
 * ADT to calculate all solutions for a boggle board utilizing a combinations 26 squared way trie for all combinations of two letters
 * then connecting to a Ternary Search Trie for each two letter combo
 * Allowing rapid and more space-efficient insertion and get operations
 */
public class RSquaredTrie {
    RootNode root;

    // can be initialized empty
    public RSquaredTrie() {
        root = new RootNode(26);
    }

    // or with array of strings
    public RSquaredTrie(String[] dictionary) {
        root = new RootNode(26);
        for (String s : dictionary) {
            this.insert(s);
        }
    }

    // contains the 26 squared array of two letter combinations of the alphabet
    private static class RootNode {
        TwoLetterNode[] next;
        boolean value;

        public RootNode(int n) {
            next = new TwoLetterNode[(int) Math.pow(n, 2)];
        }
    }

    // nodes in the 26 squared array that act as root nodes for the ternary search tries
    private static class TwoLetterNode {
        boolean value;
        Node next;

        private TwoLetterNode(boolean isTwoLetter) {
            value = isTwoLetter;
        }

        public TwoLetterNode(Node connection) {
            next = connection;
        }
    }

    // ternary search trie nodes
    public static class Node {
        Node left;
        Node right;
        Node mid;
        boolean wordEnds;
        char value;

        public Node(char n) {
            this.value = n;
        }
    }

    // inserts a value into the RSquaredTrie
    public void insert(String s) {
        if (s.length() == 1) return;
        // calculate position of two letter node
        int r = (s.charAt(0) - 65) * 26 + s.charAt(1) - 65;
        TwoLetterNode x = root.next[r];
        if (s.length() == 2) {
            // if string of length two, check if already has node, if not add it, if so mark word
            if (x == null) {
                root.next[r] = new TwoLetterNode(true);
            } else {
                root.next[r].value = true;
            }
        } else {
            // create first
            if (x == null) {
                root.next[r] = new TwoLetterNode(new Node(s.charAt(2)));
            }
            // moves on to Ternary search trie
            Node y = root.next[r].next;
            // begins recursion for adding in TST
            root.next[r].next = insert(s, y, 2);
        }
    }

    private Node insert(String s, Node y, int i) {
        // recurse through TST until null or word ends;
        // find current char
        char n = s.charAt(i);
        // if node is null create it
        if (y == null) y = new Node(n);
        // if char < node go left
        // if char > node go right
        // if they are equal and it isn't the last char in the word
        // go down the middle
        // otherwise set the current node's wordEnds value to true
        // return the last node
        if (n > y.value) y.right = insert(s, y.right, i);
        else if (n < y.value) y.left = insert(s, y.left, i);
        else if (i < s.length() - 1) y.mid = insert(s, y.mid, i + 1);
        else y.wordEnds = true;
        return y;
    }

    public boolean get(String s) {
        // check single words
        if (s.length() == 1) {
            if (s.equals("A") || s.equals("I"))
                return true;
            else
                return false;
        }
        // find root link
        int r = (s.charAt(0) - 65) * 26 + s.charAt(1) - 65;
        TwoLetterNode x = root.next[r];
        if (s.length() == 2) {
            // if string of length two, check if already has node, if not add it, if so mark word
            if (x == null) {
                return false;
            } else if (x.value) {
                return true;
            } else {
                return false;
            }
        } else {
            // create first
            if (x == null) {
                return false;
            }
            // move into ternary search trie
            Node y = root.next[r].next;
            // begins recursion
            y = get(s, y, 2);
            // if get returned null there is no word
            if (y == null) return false;
            // if there was a node at the end, check if there is a word that terminates at it
            return y.wordEnds;
        }
    }

    public Node get(String s, Node y, int i) {
        // check if i is out of bounds
        if (i > s.length() - 1) {
            if (y.wordEnds) return y;
            else return null;
        }
        // extracts the current char
        char n = s.charAt(i);
        // if there is no node here get is false, so return null
        if (y == null) return null;
        // if char < node go left
        // if char > node go right
        // if they are equal and it isn't the last char in the word
        // go down the middle
        // otherwise
        // if a word ends at this node, return it
        // else return null
        if (n > y.value) return get(s, y.right, i);
        else if (n < y.value) return get(s, y.left, i);
        else if (i < s.length() - 1) return get(s, y.mid, i + 1);
        else {
            if (y.wordEnds) return y;
            else return null;
        }
    }

    // intended to be called with strings of length 2, to determine if it is a two letter prefix of another word
    public Node prefix(String s) {
        // gets the first node in the TST for the given string using the first two chars and returns it
        int r = (s.charAt(0) - 65) * 26 + s.charAt(1) - 65;
        TwoLetterNode x = root.next[r];
        if (x == null) return null;
        if (s.length() == 2) {
            return x.next;
        } else {
            throw new IllegalArgumentException("Should be a string of length two");
        }
    }

    // called from client after predetermining the path with a node from the two letter sequence
    // allows prefixing without recalculating already found paths
    // as long as client passes in an i < s.length - 1
    public Node prefix(String s, Node y, int i) {
        if (y == null) return null;
        // begins recursion with the given node and string
        Node x = getPrefixingNode(s, y, i);
        // if prefixing found a null node, return null
        if (x == null) return null;
        // otherwise return the next node used for prefixing and finding the word
        return x;
    }

    public Node getPrefixingNode(String s, Node y, int i) {
        if (i > s.length() - 1) return y;
        if (y == null) return null;
        char l = s.charAt(i);
        // travel left or right if greater or less than node
        // and return the middle value when equal because prefixing is only one more character than the current node
        // which gives the next node used for prefixing and get
        if (l > y.value) return getPrefixingNode(s, y.right, i);
        else if (l < y.value) return getPrefixingNode(s, y.left, i);
        else return y.mid;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        RSquaredTrie x = new RSquaredTrie(dictionary);
//        System.out.println(x.get("REQUS"));
//        System.out.println(x.get("FON"));
//        Node r = x.prefix("FO");
//        r = x.prefix("FON", r, 2);
//        System.out.println(r.value);
//        r = x.prefix("FONR", r, 3);
//        System.out.println(r.value);
    }
}
