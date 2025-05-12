package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.stream.Stream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

public class Model extends Observable implements ModelInterface {

    private String startWord;
    private String targetWord;
    private String currentWord;
    private List<String> moveHistory;
    private String statusMessage;
    private String errorMessage; // Add errorMessage field
    private String dictionaryFile;
    private boolean useRandomWords = false;
    private List<String> dictionary;

    public Model(String dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
        this.currentWord = "";
        this.moveHistory = new ArrayList<>();
        this.statusMessage = "";
        this.errorMessage = ""; // Initialize errorMessage
        this.dictionary = readWordsFromDictionary();
        setRandomWords(true); // Ensure random words are generated initially
    }

    public void setRandomWords(boolean useRandomWords) {
        this.useRandomWords = useRandomWords;
        if (useRandomWords) {
            setRandomStartAndTargetWords();
        } else {
            startWord = "WAST";
            targetWord = "WEST";
        }
        setChanged();
        notifyObservers("NEW_GAME");
    }

    private void setRandomStartAndTargetWords() {
        if (dictionary.size() >= 2) {
            Random random = new Random();
            this.startWord = dictionary.get(random.nextInt(dictionary.size()));
            this.targetWord = dictionary.get(random.nextInt(dictionary.size()));
            while (this.targetWord.equals(this.startWord)) {
                this.targetWord = dictionary.get(random.nextInt(dictionary.size()));
            }
            System.out.println("Generated random words: startWord=" + startWord + ", targetWord=" + targetWord); // Debug information
            setChanged();
            notifyObservers("NEW_GAME"); // Ensure observers are notified
        } else {
            this.startWord = "NONE";
            this.targetWord = "NONE";
            System.err.println("Not enough words in the dictionary to pick random words.");
        }
    }

    @Override
    public String getStartWord() {
        return startWord;
    }

    @Override
    public String getTargetWord() {
        return targetWord;
    }

    @Override
    public String getCurrentWord() {
        return currentWord;
    }

    @Override
    public List<String> getMoveHistory() {
        return moveHistory;
    }

    @Override
    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public boolean makeMove(String word) {
        String lowerCaseWord = word.toLowerCase();
        String previousWord;
        if (moveHistory.isEmpty()) {
            previousWord = startWord;
        } else {
            previousWord = moveHistory.get(moveHistory.size() - 1);
        }

        // Check if the word is the same as the previous word
        if (lowerCaseWord.equals(previousWord.toLowerCase())) {
            errorMessage = "No change!";
            statusMessage = "NO CHANGE";
            setChanged();
            notifyObservers("NO CHANGE");
            return false;
        }

        // Check the number of changed letters
        int diff = 0;
        for (int i = 0; i < previousWord.length(); i++) {
            if (i < lowerCaseWord.length() && lowerCaseWord.charAt(i) != previousWord.charAt(i)) {
                diff++;
            } else if (i >= lowerCaseWord.length()) {
                diff++; // Consider a difference if the new word is shorter
            }
        }

        // Check if no letters have changed
        if (diff == 0) {
            errorMessage = "Must change at least one letter!";
            statusMessage = "NO LETTER CHANGED";
            setChanged();
            notifyObservers("NO LETTER CHANGED");
            return false;
        }

        // Check if more than one letter has changed
        if (diff > 1) {
            errorMessage = "Must change only one letter at a time!";
            statusMessage = "NOT ONE LETTER DIFFERENT";
            setChanged();
            notifyObservers("NOT ONE LETTER DIFFERENT");
            return false;
        }

        if (!isValidWord(lowerCaseWord)) {
            errorMessage = "Not in dictionary!";
            statusMessage = "NOT IN DICTIONARY";
            setChanged();
            notifyObservers("NOT IN DICTIONARY");
            return false;
        }

        // Check if only one letter is different
        if (!isOneLetterDifferent(lowerCaseWord, previousWord)) {
            errorMessage = "Must change only one letter!";
            statusMessage = "NOT ONE LETTER DIFFERENT";
            setChanged();
            notifyObservers("NOT ONE LETTER DIFFERENT");
            return false;
        }

        moveHistory.add(lowerCaseWord);
        currentWord = "";
        statusMessage = "VALID_MOVE";
        errorMessage = ""; // Clear error message

        if (lowerCaseWord.equals(targetWord.toLowerCase())) {
            statusMessage = "GAME WON";
        }

        setChanged();
        notifyObservers(statusMessage);
        return true;
    }

    private boolean isValidWord(String word) {
        return dictionary.contains(word);
    }

    private List<String> readWordsFromDictionary() {
        List<String> words = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(dictionaryFile))) {
            lines.forEach(word -> words.add(word.toLowerCase()));
        } catch (IOException e) {
            System.err.println("Error reading dictionary file: " + e.getMessage());
        }
        return words;
    }

    private boolean isOneLetterDifferent(String word, String previousWord) {
        word = word.toLowerCase();
        previousWord = previousWord.toLowerCase();
        if (word.length() != previousWord.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < previousWord.length(); i++) {
            if (word.charAt(i) != previousWord.charAt(i)) {
                diff++;
            }
        }
        return diff == 1;
    }

    @Override
    public boolean isWin() {
        if (moveHistory.isEmpty()) {
            return false;
        }

        String lastWord = moveHistory.get(moveHistory.size() - 1);
        return lastWord.equals(targetWord);
    }

    @Override
    public void restartGame() {
        moveHistory.clear(); // Ensure moveHistory is cleared
        currentWord = "";
        statusMessage = "";
        errorMessage = ""; // Reset error message
        setChanged();
        notifyObservers("NEW_GAME"); // Notify "NEW_GAME"
    }

    @Override
    public void undoMove() {
        if (!moveHistory.isEmpty()) {
            moveHistory.remove(moveHistory.size() - 1);
            currentWord = "";
            statusMessage = "";
            setChanged();
            notifyObservers();
        }
    }

    public void setStartWord(String startWord) {
        this.startWord = startWord;
        setChanged();
        notifyObservers();
    }

    public void setTargetWord(String targetWord) {
        this.targetWord = targetWord;
        setChanged();
        notifyObservers();
    }

    @Override
    public String getpreviousword() {
        // Check if there is a move history
        if (moveHistory == null || moveHistory.isEmpty()) {
            return ""; // Return an empty string if there is no history
        }
        // Return the last word (i.e., the previous word)
        return moveHistory.get(moveHistory.size() - 1);
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public List<String> findPath(String start, String target) {
        start = start.toLowerCase();
        target = target.toLowerCase();

        if (!isValidWord(start) || !isValidWord(target)) {
            System.out.println("Invalid start or target word.");
            return null;
        }

        Queue<String> queue = new LinkedList<>();
        queue.offer(start);

        Set<String> visited = new HashSet<>();
        visited.add(start);

        Map<String, String> parentMap = new HashMap<>();
        parentMap.put(start, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(target)) {
                List<String> path = new ArrayList<>();
                String node = target;
                while (node != null) {
                    path.add(0, node);
                    node = parentMap.get(node);
                    if (node != null && !isValidWord(node)) {
                        return null;
                    }
                }
                return path;
            }

            for (String neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        System.out.println("No path found between " + start + " and " + target);
        return null;
    }

    private List<String> getNeighbors(String word) {
        List<String> neighbors = new ArrayList<>();
        for (String dictWord : dictionary) {
            if (isOneLetterDifferent(word, dictWord)) {
                neighbors.add(dictWord);
            }
        }
        return neighbors;
    }
}