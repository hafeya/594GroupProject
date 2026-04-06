package edu.upenn.cit5940.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.upenn.cit5940.common.dto.StringHelper.stringTokenizer;
import static edu.upenn.cit5940.datamanagement.StopWords.stopWordChecker;

public class SearchProcessor {

    private Map<String, Set<Integer>> invertedIndex;

    public SearchProcessor(Map<String, Set<Integer>> invertedIndex) {
        this.invertedIndex = invertedIndex;
    }

    /**
     * This method searches for a query to return a matching set of document IDs
     * @param query String containing the search query
     * @return Set containing the document IDs that match the search query
     */
    public Set<Integer> search(String query) {

        //starting with a check to validate that the query is not null/empty and that the root is not null
        //if any of those conditions are met, we'll return an empty HashSet
        if (query == null || query.isEmpty()) {
            return new HashSet<>();
        }

        //initializing variables here
        List<String> tokens = stringTokenizer(query); //initialize a List of Strings that contains our tokenized query using helper method
        int firstTokenCheck = 0; //initialize int firstTokenCheck as we'll handle the first and subsequent tokens differently
        Set<Integer> result = new HashSet<>(); //initialize HashSet result to return after we finish processing/filtering the given doc IDs

        //enhanced for loop to iterate through the valid tokens
        for (String token : tokens) {

            //calling our helper method stopWordChecker along with checking if the token is empty
            //if either of these conditions are met, we'll skip this token
            if (stopWordChecker(token) || token.isEmpty()) {
                continue;
            }

            Set<Integer> givenURIs = invertedIndex.get(token);


            if (givenURIs == null || givenURIs.isEmpty()) {
                return new HashSet<>();
            }

            firstTokenCheck++;

            if (firstTokenCheck == 1) {
                result.addAll(givenURIs);
            } else {
                result.retainAll(givenURIs);
            }

        }
        //returning the result
        return result;
    }
}
