package edu.upenn.cit5940.datamanagement;

import java.io.IOException;
import java.util.*;

import edu.upenn.cit5940.common.dto.Article;

public class CSVArticleReader {

    private final CharacterReader reader;
    private int iLine = 1;
    private int iRecord = 1;

    public CSVArticleReader(CharacterReader reader) {
        this.reader = reader;
    }

    // The states for the Finite State Machine (FSM).
    private enum STATES {

        START_FIELD, //this is the beginning of a new field; we'll look for a quote/comma/newline/content
        IN_QUOTED, //this is inside a quoted field; we'll collect chars until we find the closing quote
        AFTER_QUOTE, //this is after we come across a quote within a quoted field; we'll handle escaped quotes, end of field, and possible errors
        IN_UNQUOTED //this is inside an unquoted field; we'll collect chars until we find a comma/newline
    }

    /**
     * Reads the entire CSV stream and parses it into a map of Articles.
     *
     * @return A map where the key is the article's URI (String) and the value
     * is the fully populated Article object.
     * @throws IOException when the underlying reader encounters an error.
     * @throws CSVFormatException when the CSV file is formatted incorrectly.
     */
    public Map<String, Article> readAllArticles() throws IOException, CSVFormatException {

        //initialize variables
        //map containing our articles; key is URI, value is Article object
        Map<String, Article> articles = new HashMap<>();
        //list holding the fields of the current record being parsed
        List<String> record = new ArrayList<>();
        //string builder to collect the chars of the current field being parsed
        StringBuilder currentField = new StringBuilder();
        //simple column counter to track placement within the record for error reporting
        int col = 0;
        //each char read will start in the START_FIELD state and update accordingly as we read chars
        STATES state = STATES.START_FIELD;
        //initialize readingInt to the first char in the stream; will be used to track progress and eventually end the loop
        int readingInt = reader.read();

        //kick off the loop to read chars one by one
        //loop will end at the end of the stream, which will be at -1
        while (readingInt != -1) {

            //casting int to char to process it
            char currentChar = (char) readingInt;

            //FSM starting in START_FIELD state: beginning of a new field
            //here, we'll look for a quote, comma, newline, or content char
            if (state == STATES.START_FIELD) {
                if (currentChar == '"') {
                    state = STATES.IN_QUOTED; //coming across a quote leads us to the IN_QUOTED state
                } else if (currentChar == ',') {
                    record.add(""); //comma at the start of a field = empty field, so we add an empty string
                    col++; //increment column counter to move onto the next column in the record
                } else if (currentChar == '\n') {
                    if (record.isEmpty()) {
                        iLine++; //skipping blank line
                    } else {
                        record.add(""); //newline at the start of a field = empty field, so we add an empty string
                        processRecord(record, articles); //calling helper method to add the record to the map as an Article object
                        record.clear(); //clearing the record list to prepare for the next record
                        iRecord++; //increment record counter for error reporting
                        iLine++; //increment line counter for error reporting
                        col = 0; //reset column counter before the next record
                    }
                } else if (currentChar == '\r') {
                    int next = reader.read();
                    if (next != '\n') { //throwing an error if newline doesn't follow the carriage return
                        throw new CSVFormatException(iLine, col, iRecord, record.size() + 1);
                    }
                    if (record.isEmpty()) {
                        iLine++; //skipping blank line
                    } else {
                        record.add("");
                        processRecord(record, articles);
                        record.clear();
                        iRecord++;
                        iLine++;
                        col = 0;
                    }
                } else {
                    currentField.append(currentChar); //append content char to the current field
                    state = STATES.IN_UNQUOTED; //update state to IN_UNQUOTED since we've come across a content char
                }

                //FSM algorithm for IN_QUOTED state: inside a quoted field
                //here, we'll look for a quote to mark the end of the quoted field
                //if we don't find that, we'll look for content chars to add to the current field
            } else if (state == STATES.IN_QUOTED) {
                if (currentChar == '"') {
                    state = STATES.AFTER_QUOTE; //switching to AFTER_QUOTE state to determine if this is the end of the field or an escaped quote
                } else {
                    currentField.append(currentChar);
                    if (currentChar == '\n') {
                        iLine++; //tracking newlines for error reporting
                    }
                }

                //FSM algorithm for AFTER_QUOTE state: handling the chars after we see a quote in the IN_QUOTED state
                //here, we'll determine if that was the end of a field, record, or if it was an escaped quote
                //if we encounter any erroneous chars, we'll throw a CSVFormatException
            } else if (state == STATES.AFTER_QUOTE) {
                if (currentChar == '"') {
                    currentField.append('"'); //adding an escaped quote to the current field
                    state = STATES.IN_QUOTED; //then going back to IN_QUOTED state to continue processing
                } else if (currentChar == ',') {
                    record.add(currentField.toString()); //converting to string to add to the record list
                    currentField.setLength(0); //reset current field to prepare for the next field
                    state = STATES.START_FIELD; //launching the next field, so we update state to START_FIELD
                    col++;
                } else if (currentChar == '\n') {
                    record.add(currentField.toString());
                    currentField.setLength(0);
                    processRecord(record, articles);
                    record.clear();
                    state = STATES.START_FIELD;
                    iRecord++;
                    iLine++;
                    col = 0;
                } else if (currentChar == '\r') {
                    int next = reader.read();
                    if (next != '\n') {
                        throw new CSVFormatException(iLine, col, iRecord, record.size() + 1);
                    }
                    record.add(currentField.toString());
                    currentField.setLength(0);
                    processRecord(record, articles);
                    record.clear();
                    state = STATES.START_FIELD;
                    iRecord++;
                    iLine++;
                    col = 0;
                } else {
                    throw new CSVFormatException(iLine, col, iRecord, record.size() + 1);
                }

                //FSM algorithm for IN_UNQUOTED state: inside an unquoted field
                //here, we'll look for a comma or newline to mark the end of the field/record
            } else if (state == STATES.IN_UNQUOTED) {
                if (currentChar == ',') {
                    record.add(currentField.toString());
                    currentField.setLength(0);
                    state = STATES.START_FIELD;
                    col++;
                } else if (currentChar == '\n') {
                    record.add(currentField.toString());
                    currentField.setLength(0);
                    processRecord(record, articles);
                    record.clear();
                    state = STATES.START_FIELD;
                    iRecord++;
                    iLine++;
                    col = 0;
                } else if (currentChar == '\r') {
                    int next = reader.read();
                    if (next != '\n') {
                        throw new CSVFormatException(iLine, col, iRecord, record.size() + 1);
                    }
                    record.add(currentField.toString());
                    currentField.setLength(0);
                    processRecord(record, articles);
                    record.clear();
                    state = STATES.START_FIELD;
                    iRecord++;
                    iLine++;
                    col = 0;
                } else if (currentChar == '"') { //throwing exception if we happen upon a quote in an unquoted field
                    throw new CSVFormatException(iLine, col, iRecord, record.size() + 1);
                } else {
                    currentField.append(currentChar);
                }
            }
            //at the end of each iteration of the loop, we'll move onto the next char in the stream
            readingInt = reader.read();
        }

        //EOF handling to finalize any remaining fields/records
        if (state == STATES.IN_QUOTED) {
            throw new CSVFormatException(iLine, col, iRecord, record.size() + 1);
        }

        //if end the stream in AFTER_QUOTE or IN_UNQUOTED, we need to finalize the current field
        if (state == STATES.AFTER_QUOTE) {
            record.add(currentField.toString());
        } else if (state == STATES.IN_UNQUOTED) {
            record.add(currentField.toString());
        } else if (state == STATES.START_FIELD) {
            if (!record.isEmpty()) {
                record.add("");
            }
        }

        //calling processRecord to handle the final record if we have any remaining fields
        if (!record.isEmpty()) {
            processRecord(record, articles);
        }

        //finally, we return the map of articles that we've constructed
        return articles;
    }

    /**
     * Helper method to convert a parsed record (list of strings) into an Article
     * and add it to the map.
     */
    private void processRecord(List<String> record, Map<String, Article> articles) throws CSVFormatException {

        //performing a few quick checks on the record before converting to an Article object
        //first check: ensure we have the correct number of fields
        if (record.size() != Article.EXPECTED_FIELD_COUNT) {
            throw new IllegalArgumentException("Incorrect number of fields.");
        }
        //second check: if the first field is "uri", skip it as it is a header row
        if (record.get(0).equals("uri")) {
            return;
        }

        Article article = new Article(record.get(0), record.get(5), record.get(6), record.get(1));

        //add article to map; use the URI as key and the Article object as value
        String uri = article.getUri();
        articles.put(uri, article);
    }
}

