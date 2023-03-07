package org.hua.huffmancode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

/**
 *
 * @author it21921,it21909,it21954
 */
public class huffmanTree implements Comparable<huffmanTree>, java.io.Serializable {

    private Node root;
    private int size;

    public huffmanTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Create a node or a junction with weight that of the two children's
     * frequencies
     *
     * @return the node created
     */
    public Node createNodeJunction(int Asciiletter, int frequency, Node leftC, Node rightC) {
        Node n = new Node(Asciiletter, frequency, leftC, rightC);
        if (Asciiletter == -1) {
            root = n;
        }
        size++;

        return n;
    }

    //getter for root frequency
    public int getFrequency() {
        return root.frequency;
    }

    //getter and setter for root
    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    /**
     * Returns true if tree is empty
     *
     * @return True if the tree is empty, else false
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the size of the Huffman Tree
     *
     * @return The size of the Huffman Tree
     */
    public int size() {
        return size;
    }

    /**
     * Clears the tree
     *
     */
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * A compareTo, in order to make the huffmanTree comparable, so it can be
     * inserted in the minHeap
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(huffmanTree o) {
        return Integer.compare(this.root.frequency, o.root.frequency);
    }

    /**
     * Save the huffmanTree into file tree.dat
     *
     * @param t
     */
    public static void saveTree(huffmanTree t) throws FileNotFoundException, IOException {
        FileOutputStream fout = new FileOutputStream("trees.dat");
        ObjectOutputStream out = new ObjectOutputStream(fout);
        out.writeObject(t);
        out.flush();
    }

    //prints the codes for each leaf of the huffmantree
    public static void printCode(Node root, String n) throws FileNotFoundException, IOException, ClassNotFoundException {

        if (root.leftChild == null && root.rightChild == null) {

            System.out.printf("%3d : %s\n", root.asciiValue, n);
            return;
        }
        printCode(root.leftChild, n + "0");
        printCode(root.rightChild, n + "1");
    }

    public static void saveCode(Node root, String n) throws FileNotFoundException, IOException, ClassNotFoundException {
        //make a new file that stores the code results
        PrintStream toTheFile = new PrintStream(new File("codes.dat"));
        // Store current System.out before assigning a new value 
        PrintStream console = System.out;
        // Assign toTheFile to output stream 
        System.setOut(toTheFile);
        printCode(root, n);
        // Back to default System.out
        System.setOut(console);

    }

    //convert a byte string to chaarcters ASCII
    public static StringBuilder huffmanDecoder(huffmanTree t, String allBytesStrings) {
        //make an ASCII array
        String temp = "";
        for (int i = 0; i < 128; i++) {
            temp += Character.toString((char) i);;
        }
        char[] Ascii = new char[temp.length()];
        Ascii = temp.toCharArray();
        //make a new node
        Node curNode = t.getRoot();
        char tempC;
        StringBuilder text = new StringBuilder();
        for (int i = 0; i <= allBytesStrings.length(); i++) {
            if (curNode.leftChild == null && curNode.rightChild == null) {
                //convert curNode.asciiValue to a charcter ascii
                for(int j=0; j<128; j++){
                    //find what value ha the curNode.asciiValue and stores in a string builder text
                    if(j == curNode.asciiValue){
                        text.append(Ascii[j]);
                        curNode = t.root;
                    }
                }
            }
            if(i != allBytesStrings.length()){
                tempC = allBytesStrings.charAt(i);
                if(tempC == '0'){
                    curNode = curNode.leftChild;
                }else{
                    curNode = curNode.rightChild;
                }
            }
        }
        return text;
    }

    private static class Node implements java.io.Serializable {

        public int asciiValue;
        public int frequency;
        public Node leftChild;
        public Node rightChild;

        public Node(int asciiValue, int value, Node left, Node right) {
            this.asciiValue = asciiValue;
            this.frequency = value;
            this.leftChild = left;
            this.rightChild = right;
        }
    }
}
