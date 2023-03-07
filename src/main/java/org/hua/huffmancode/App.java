package org.hua.huffmancode;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import static org.hua.huffmancode.decoder.CAPACITY;
import static org.hua.huffmancode.decoder.readFrequencyFile;
import static org.hua.huffmancode.huffmanTree.huffmanDecoder;
import static org.hua.huffmancode.huffmanTree.saveCode;
import static org.hua.huffmancode.huffmanTree.saveTree;

/**
 *
 * @author it21921,it21909,it21954
 */
class encoder {

    public static final int CAPACITY = 128;
    //Line 26 and 27 are used for PART 4
    public static String total = "";
    public static ArrayList<String> tempArray = new ArrayList<String>();

    public static void main(String args[]) throws IOException, Exception {
        try {
            //PART 1 of Huffman Project
            //make frequencies.dat
            Frequency fr = new Frequency();
            fr.makeNewFile();

            //PART 2 of Huffman Project
            //make frequency array for Huffman
            int frequency[] = new int[CAPACITY];
            readFrequencyFile("frequencies.dat", frequency);

            //create a minheap
            MinHeap<huffmanTree> heap = new MyArrayMinHeap<huffmanTree>();
            //make one node trees for every frequency
            for (int i = 0; i < frequency.length; i++) {
                huffmanTree h = new huffmanTree();
                h.setRoot(h.createNodeJunction(i, frequency[i], null, null));
                heap.insert(h);
            }

            //get the trees with the 2 lowest values
            //make new huffmantree with asciiLetterNumber -1 to signify that this is not a leaf node and frequency the sum of the 2 previous trees
            //add this tree to the heap
            //clear old trees
            //repeat until the heap has only one tree.
            while (heap.size() != 1) {
                huffmanTree mintree1 = new huffmanTree();
                huffmanTree mintree2 = new huffmanTree();
                mintree1 = heap.deleteMin();
                mintree2 = heap.deleteMin();
                int junctionFreq = mintree1.getFrequency() + mintree2.getFrequency();
                huffmanTree temptree = new huffmanTree();
                temptree.createNodeJunction(-1, junctionFreq, mintree1.getRoot(), mintree2.getRoot());
                heap.insert(temptree);
                mintree1.clear();
                mintree2.clear();
            }
            huffmanTree tree = new huffmanTree();
            tree = heap.deleteMin();
            //saves tree to tree.dat
            saveTree(tree);
            tree.clear();

            //PART 3 of Huffman Project
            //read file tree.dat and store the tree into a new huffmantree
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("trees.dat"));
            huffmanTree fileTree = (huffmanTree) in.readObject();

            String codes = "";
            //save the codes to codes.dat file
            saveCode(fileTree.getRoot(), codes);

            //PART 4 of Huffman Project
            System.out.println("Running Program...");
            //make an ASCII array
            String temp = "";
            for (int i = 0; i < 128; i++) {
                temp += Character.toString((char) i);;
            }
            char[] Ascii = new char[temp.length()];
            Ascii = temp.toCharArray();
            //check if file input exist

            Scanner sc = new Scanner(new File(args[0]));
            String inputContent = "";
            //read file content
            while (sc.hasNextLine()) {
                inputContent = inputContent.concat(sc.nextLine() + "\n");
            }
            //Take all characters from input file
            for (int i = 0; i < inputContent.length(); i++) {
                char asciiChar = inputContent.charAt(i);
                int numberOfAscii = -1;
                //check for each character what value has in ASCII TABLE
                for (int j = 0; j < 128; j++) {
                    if (asciiChar == Ascii[j]) {
                        numberOfAscii = j;
                        break;
                    }
                }
                //convert int ascii number to String decimal
                String decimal = String.valueOf(numberOfAscii);
                //open codes.dat file
                File file = new File("codes.dat");
                Scanner reader = new Scanner(file);
                //read codes.dat
                while (reader.hasNextLine()) {
                    String search = reader.nextLine();
                    String hCode = search.substring(search.lastIndexOf(":") + 2);
                    if (search.contains(decimal)) {
                        //if contains decimal in the start of the string before :
                        //we will check the first 3 characters of the line
                        String firstNums = search.substring(0, 3);
                        //check if character is \n 
                        if (numberOfAscii == 10) {
                            tempArray.add("110011");
                            break;
                        } else {
                            if (firstNums.contains(decimal)) {
                                tempArray.add(hCode);
                            }
                        }
                    }
                }
            }
            //make a string builder
            StringBuilder sb = new StringBuilder();
            for (String s : tempArray) {
                sb.append(s);
            }
            //store all String builder to variable total
            total = sb.toString();

            //string of all the strings, contains the entire huffmancode of input file
            String huffmanCodeStr = total;
            int numOfBytes = huffmanCodeStr.length() / 8;
            int numOfLastBits = huffmanCodeStr.length() % 8;
            //an arraylist to keep all bytes before saving them to file
            ArrayList byteArray = new ArrayList<Byte>();

            //make an 8 space char array to store 8 chars at a time from the huffmanCodeStr
            char[] byteCharArray = new char[8];
            int bitIndex = 0;
            //convert string huffmanCodeStr containing all of the huffman code to bytes
            for (int i = 0; i < numOfBytes; i++) {
                for (int j = 0; j < 8; j++) {
                    byteCharArray[j] = huffmanCodeStr.charAt(bitIndex);
                    bitIndex++;
                }
                //convert the byteCharArray to a string
                String byteString = new String(byteCharArray);
                //convert the string to an integer and typecast it as byte
                byte newByte = (byte) Integer.parseInt(byteString, 2);
                //save byte to byteArray
                byteArray.add(newByte);
            }

            //clear any remaining old values from byteCharArray
            byteCharArray = new char[8];
            //save the last bits who are always 7 or less
            if (numOfLastBits != 0) {
                int emptyBits = (8 - numOfLastBits);
                for (int i = 0; i < emptyBits; i++) {
                    //place zero in the empty bits of the last byte
                    byteCharArray[i + numOfLastBits] = '0';

                }
                for (int i = 0; i < numOfLastBits; i++) {
                    byteCharArray[i] = huffmanCodeStr.charAt(bitIndex);
                    bitIndex++;
                }
                String byteString = new String(byteCharArray);
                byte lastByte = (byte) Integer.parseInt(byteString, 2);

                //save lastbyte to byteArray
                byteArray.add(lastByte);
            }

            int numOfBits = bitIndex;
            //save numOfBits to file
            //save bytes from byteArray to file
            byte[] primitiveByteArray = convertBytes(byteArray);
            writeBytesToFile(args[1], primitiveByteArray, numOfBits);
            System.out.println("Program finished");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred. Input file does not exist!");
            e.printStackTrace();
        }
    }

    //convert the ArrayList<Byte> to primitive byte[]
    private static byte[] convertBytes(List<Byte> bytes) {
        byte[] ret = new byte[bytes.size()];
        Iterator<Byte> iterator = bytes.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().byteValue();
        }
        return ret;
    }

    //save byte[] to file
    private static void writeBytesToFile(String fileOutput, byte[] bytes, int numOfBits)
            throws IOException {

        try ( OutputStream fos = new FileOutputStream(fileOutput)) {
            DataOutputStream numOfBitsAtStart = new DataOutputStream (fos);
            numOfBitsAtStart.writeInt(numOfBits);
            fos.write(bytes);
        }

    }

    //this part of code belongs to PART 2 of Huffman Project
    public static void readFrequencyFile(String filePath, int frequency[]) throws Exception {
        Scanner sc = new Scanner(new BufferedReader(new FileReader(new File(filePath))));
        //read frequencies.dat and save to int array
        for (int i = 0; sc.hasNextLine(); i++) {
            frequency[i] = Integer.parseInt(sc.nextLine());
        }
    }

}

//PART 5 of Huffman Project
class decoder {

    public static final int CAPACITY = 128;
    //Line 244 and 245 are used for PART 4
    public static String total = "";
    public static ArrayList<String> tempArray = new ArrayList<String>();

    public static void main(String args[]) throws IOException, Exception {
        //PART 1 of Huffman Project
        //make frequencies.dat
        Frequency fr = new Frequency();
        fr.makeNewFile();

        //PART 2 of Huffman Project
        //make frequency array for Huffman
        int frequency[] = new int[CAPACITY];
        readFrequencyFile("frequencies.dat", frequency);

        //create a minheap
        MinHeap<huffmanTree> heap = new MyArrayMinHeap<huffmanTree>();
        //make one node trees for every frequency
        for (int i = 0; i < frequency.length; i++) {
            huffmanTree h = new huffmanTree();
            h.setRoot(h.createNodeJunction(i, frequency[i], null, null));
            heap.insert(h);
        }

        //get the trees with the 2 lowest values
        //make new huffmantree with asciiLetterNumber -1 to signify that this is not a leaf node and frequency the sum of the 2 previous trees
        //add this tree to the heap
        //clear old trees
        //repeat until the heap has only one tree.
        while (heap.size() != 1) {
            huffmanTree mintree1 = new huffmanTree();
            huffmanTree mintree2 = new huffmanTree();
            mintree1 = heap.deleteMin();
            mintree2 = heap.deleteMin();
            int junctionFreq = mintree1.getFrequency() + mintree2.getFrequency();
            huffmanTree temptree = new huffmanTree();
            temptree.createNodeJunction(-1, junctionFreq, mintree1.getRoot(), mintree2.getRoot());
            heap.insert(temptree);
            mintree1.clear();
            mintree2.clear();
        }
        huffmanTree tree = new huffmanTree();
        tree = heap.deleteMin();
        //saves tree to tree.dat
        saveTree(tree);
        tree.clear();

        //PART 3 of Huffman Project
        //read file tree.dat and store the tree into a new huffmantree
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("trees.dat"));
        huffmanTree fileTree = (huffmanTree) in.readObject();

        String codes = "";
        //save the codes to codes.dat file
        saveCode(fileTree.getRoot(), codes);

        //PART 5 of Huffman Project
        File file = null;
        int numberOfReadBits = 0;
        try {

            System.out.println("Running Decoder...");
            
            file = new File(args[0]);
            FileInputStream fin = new FileInputStream(args[0]);
            DataInputStream din = new DataInputStream(fin);
            //read the integer at the start of the file
            numberOfReadBits = din.readInt();
            din.close();

        } catch (FileNotFoundException fe) {
            System.out.println("FileNotFoundException : " + fe);
            System.out.println("Decoder failed");
            System.exit(1);
        } catch (IOException ioe) {
            System.out.println("IOException : " + ioe);
            System.out.println("Decoder failed");
            System.exit(1);
        }
        //file = new File("out.txt");
        //read all the bytes, including, the integer at the start
        byte[] allBytes = readContentIntoByteArray(file);
        
        //number of bytes to convert from byte to string and appended to AllbyteStrings
        int numberOfBytes = numberOfReadBits / 8;
        //the number of bits that should be read from the last byte
        int numberOfBitsInLastByte = numberOfReadBits % 8;

        String AllbyteStrings = "";
        int intSize = (Integer.SIZE)/8;
        int finalByteIndex = 0;
        for (int i = intSize; i < (numberOfBytes+intSize); i++) {  
            finalByteIndex = i;
            //converts byte type to integer, taking only the first 8 bits, and then the integer is converted into a string, with format xxxxxxxx, with x being a bit
            String byteString = String.format("%8s", Integer.toBinaryString(allBytes[i] & 0xFF)).replace(' ', '0');
            tempArray.add(byteString);
        }
        //for the last byte add to the string only the last n chars, with n being the numberOfBitsInLastByte
        String tempByteString = String.format("%8s", Integer.toBinaryString(allBytes[finalByteIndex + 1] & 0xFF)).replace(' ', '0');
        char[] ch = new char[numberOfBitsInLastByte];
        for(int i = 0; i < numberOfBitsInLastByte; i++){
            ch[i] = tempByteString.charAt(i);
        }
        String bitsString = new String(ch);
        tempArray.add(bitsString
        );
        //make a string builder
        StringBuilder sb = new StringBuilder();
        for (String s : tempArray) {
            sb.append(s);
        }
        //store all String builder to variable AllbyteString
        AllbyteStrings = sb.toString();

        // Method for deserialization of object 
        try {
            // fileTree = (huffmanTree) in.readObject();
            FileInputStream fileInp = new FileInputStream("trees.dat");
            ObjectInputStream inp = new ObjectInputStream(fileInp);
            fileTree = (huffmanTree) inp.readObject();
            String text = huffmanDecoder(fileTree, AllbyteStrings).toString();

            //make a new output file that stores the text results
            PrintStream toTheFile = new PrintStream(new File(args[1]));
            // Store current System.out before assigning a new value 
            PrintStream console = System.out;
            // Assign toTheFile to output stream 
            System.setOut(toTheFile);
            System.out.print(text);
            // Back to default System.out
            System.setOut(console);
            inp.close();
            fileInp.close();
            System.out.println("Decoder finished");
        } catch (EOFException e) {

            System.out.println("Decoder failed");
        } catch (IOException e) {
            System.out.println("Decoder failed");
            e.printStackTrace();
        }

    }

    public static byte[] readContentIntoByteArray(File file) {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bFile;
    }

    //this part of code belongs to PART 2 of Huffman Project
    public static void readFrequencyFile(String filePath, int frequency[]) throws Exception {
        Scanner sc = new Scanner(new BufferedReader(new FileReader(new File(filePath))));
        //read frequencies.dat and save to int array
        for (int i = 0; sc.hasNextLine(); i++) {
            frequency[i] = Integer.parseInt(sc.nextLine());
        }
    }

}
