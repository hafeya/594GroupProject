package edu.upenn.cit5940.common.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringHelper {

    /**
     * Helper method to tokenize the input text into a list of cleaned tokens
     * @param text input text to be tokenized
     * @return list of cleaned tokens from input text
     */
    public static List<String> stringTokenizer(String text) {

        //checking if the input text is null or empty, if so we return an empty list
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        //characters are replaced with a space and then split on whitespace
        text = text.toLowerCase().replaceAll("[^a-z0-9\\s-]", " ");
        text = text.replaceAll("^-+", "");
        text = text.replaceAll("-+$", "");
        text = text.replaceAll("\\s-+", " ");
        text = text.replaceAll("-+\\s", " ");

        return Arrays.asList(text.trim().split("\\s+")); //splitting cleaned text on whitespace and return as a list of tokens
    }

}
