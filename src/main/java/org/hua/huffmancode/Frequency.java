package org.hua.huffmancode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author it21921,it21909,it21954
 */
public class Frequency {
     public static final int CAPACITY = 128;
    
    private String fileContentOfFIles() throws MalformedURLException, IOException{
        //import the link for the first text
        String urlString1 = "https://www.gutenberg.org/files/1342/1342-0.txt";
        //import the link for the second text
        String urlString2 = "https://www.gutenberg.org/files/11/11-0.txt";
        //import the link for the third text
        String urlString3 = "https://www.gutenberg.org/files/2701/2701-0.txt";

        // create the url
        URL url1 = new URL(urlString1);
        URL url2 = new URL(urlString2);
        URL url3 = new URL(urlString3);
        // open the url stream, wrap it an a few "readers"
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(url1.openStream()));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(url2.openStream()));
        BufferedReader reader3 = new BufferedReader(new InputStreamReader(url3.openStream()));
        //make file content. File content contains all texts
        String fileContent = "";
        String line;

        while ((line = reader1.readLine()) != null) {
            fileContent = fileContent.concat(line + "\n");
        }
        reader1.close();
        while ((line = reader2.readLine()) != null) {
            fileContent = fileContent.concat(line + "\n");
        }
        reader2.close();
        while ((line = reader3.readLine()) != null) {
            fileContent = fileContent.concat(line + "\n");
        }
        reader3.close();

        //make a new file. File contains the fileContent
        FileWriter writer = new FileWriter("file.txt");
        writer.write(fileContent);
        writer.close();
        
        return fileContent;
    }
    
    public void makeNewFile() throws IOException{
        String fileContent = fileContentOfFIles();
        String temp = "";
        for (int i = 0; i < 128; i++) {
            temp += Character.toString((char) i);;
        }
        char[] Ascii = new char[temp.length()];
        Ascii = temp.toCharArray();

        //make a int array that counts the characters of ASCII from the file
        int asciiCounter[] = new int[CAPACITY];
        //use method findFrequency for all Ascii characters
        findFrequency(fileContent, Ascii, asciiCounter);
        //counter of \n must be reduced by 3 because there are 3 extra \n  
        asciiCounter[10] = asciiCounter[10] - 3;

        //make a new file that restores the result
        PrintStream toTheFile = new PrintStream(new File("frequencies.dat"));
        // Store current System.out before assigning a new value 
        PrintStream console = System.out;
        // Assign toTheFile to output stream 
        System.setOut(toTheFile);

        for (int i = 0; i < 128; i++) {
            System.out.printf("%d\n",asciiCounter[i]);
        }
        // Back to System.out
        System.setOut(console);

    }
    
    private void findFrequency(String fileContent, char Ascii[], int asciiCounter[]) {
        //use a temporary char array to store fileContent
        char[] tmpContent = fileContent.toCharArray();
        //size of fileContent
        int size = fileContent.length();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 128; j++) {
                //if character of ASCII and character of content are equal ,count++
                if (Ascii[j] == tmpContent[i]) {
                    asciiCounter[j]++;
                }
            }
        }
    }
    
}
