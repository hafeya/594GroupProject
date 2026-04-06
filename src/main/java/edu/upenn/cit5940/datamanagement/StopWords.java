package edu.upenn.cit5940.datamanagement;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class StopWords {

    private static final String STOP_WORDS_FILE = "src/main/resources/stop_words.txt";
    private static final Set<String> STOP_WORDS = loadStopWords(STOP_WORDS_FILE);

    private static Set<String> loadStopWords(String filepath) {
        Set<String> STOP_WORDS = new HashSet<>();

        try (Scanner scanner = new Scanner(new FIle(filepath))) {
            while (scanner.hasNext()) {
                STOP_WORDS.add(scanner.next().trim().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            //TODO: placeholder, we'll replace this with Logger
            System.err.println("Couldn't find the stop words file: " + e.getMessage());
        }
        return STOP_WORDS;
    }

    /**
     * Helper method to check if a given word is a stop word
     * @param word token to check
     * @return boolean indicating whether the token is a stop word or not
     */
    public static boolean stopWordChecker(String word) {

        //returns true if the word is in the STOP_WORDS set, false otherwise
        return STOP_WORDS.contains(word);
    }




}
