package Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Model extends Observable implements ModelInterface {

    private String startWord;
    private String targetWord;
    private String currentWord;
    private List<String> moveHistory;
    private String statusMessage;
    private String errorMessage;
    private String dictionaryFile;
    private boolean useRandomWords = false;
    private List<String> dictionary;

    public Model(String dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
        this.currentWord = "";
        this.moveHistory = new ArrayList<>();
        this.statusMessage = "";
        this.errorMessage = "";
        this.dictionary = readWordsFromDictionary();
        setRandomWords(true);
    }

    /**
     * @param useRandomWords whether to randomly choose start and target words
     * @pre dictionary must contain at least 2 words if useRandomWords is true
     * @post startWord and targetWord are initialized and observers are notified
     */
    public void setRandomWords(boolean useRandomWords) {
        this.useRandomWords = useRandomWords;

        if (useRandomWords) {
            assert dictionary != null && dictionary.size() >= 2 : "Dictionary must contain at least 2 words";
            setRandomStartAndTargetWords();
        } else {
            startWord = "WAST";
            targetWord = "WEST";
        }

        assert startWord != null && targetWord != null : "Words must not be null";

        setChanged();
        notifyObservers("NEW_GAME");
    }

    /**
     * @pre dictionary must contain at least 2 words
     * @post startWord and targetWord are assigned to two different words from dictionary
     */
    private void setRandomStartAndTargetWords() {
        assert dictionary != null && dictionary.size() >= 2 : "Not enough words for random selection";

        Random random = new Random();
        this.startWord = dictionary.get(random.nextInt(dictionary.size()));
        this.targetWord = dictionary.get(random.nextInt(dictionary.size()));
        while (this.targetWord.equals(this.startWord)) {
            this.targetWord = dictionary.get(random.nextInt(dictionary.size()));
        }

        assert !startWord.equals(targetWord) : "Start and target words must be different";

        System.out.println("Generated random words: startWord=" + startWord + ", targetWord=" + targetWord);
        setChanged();
        notifyObservers("NEW_GAME");
    }

    public String getStartWord() {
        return startWord;
    }

    public String getTargetWord() {
        return targetWord;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public List<String> getMoveHistory() {
        return moveHistory;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @param word the word to attempt moving to
     * @pre word must not be null; must be same length as previous word; dictionary must be loaded
     * @post If valid, word is added to moveHistory; observers are notified
     * @return true if move is valid, false otherwise
     */
    public boolean makeMove(String word) {
        assert word != null : "Input word must not be null";
        assert dictionary != null : "Dictionary must be initialized";

        String lowerCaseWord = word.toLowerCase();
        String previousWord = moveHistory.isEmpty() ? startWord : moveHistory.get(moveHistory.size() - 1);
        assert previousWord != null : "Previous word must not be null";
        assert lowerCaseWord.length() == previousWord.length() : "Word must be same length as previous";

        if (lowerCaseWord.equals(previousWord.toLowerCase())) {
            errorMessage = "No change!";
            statusMessage = "NO CHANGE";
            setChanged();
            notifyObservers("NO CHANGE");
            return false;
        }

        int diff = 0;
        for (int i = 0; i < previousWord.length(); i++) {
            if (i < lowerCaseWord.length() && lowerCaseWord.charAt(i) != previousWord.charAt(i)) {
                diff++;
            } else if (i >= lowerCaseWord.length()) {
                diff++;
            }
        }

        if (diff == 0) {
            errorMessage = "Must change at least one letter!";
            statusMessage = "NO LETTER CHANGED";
            setChanged();
            notifyObservers("NO LETTER CHANGED");
            return false;
        }

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

        if (!isOneLetterDifferent(lowerCaseWord, previousWord)) {
            errorMessage = "Must change only one letter!";
            statusMessage = "NOT ONE LETTER DIFFERENT";
            setChanged();
            notifyObservers("NOT ONE LETTER DIFFERENT");
            return false;
        }

        moveHistory.add(lowerCaseWord);
        currentWord = "";
        errorMessage = "";
        statusMessage = lowerCaseWord.equals(targetWord.toLowerCase()) ? "GAME WON" : "VALID_MOVE";

        assert moveHistory.contains(lowerCaseWord) : "Move history must contain the new word";

        setChanged();
        notifyObservers(statusMessage);
        return true;
    }

    /**
     * @param word the word to check
     * @pre word is not null
     * @post Returns true if word is in dictionary
     * @return whether word exists in dictionary
     */
    private boolean isValidWord(String word) {
        assert word != null : "Word must not be null";
        return dictionary.contains(word);
    }

    /**
     * @pre dictionaryFile must be a valid path
     * @post Returns list of lower-case words from dictionary
     * @return list of dictionary words
     */
    private List<String> readWordsFromDictionary() {
        assert dictionaryFile != null : "Dictionary file must not be null";
        List<String> words = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(dictionaryFile))) {
            ((Stream<?>) lines).forEach(word -> words.add(word.toString().toLowerCase()));
        } catch (IOException e) {
            System.err.println("Error reading dictionary file: " + e.getMessage());
        }
        return words;
    }

    /**
     * @param word first word
     * @param previousWord second word
     * @pre Both words must not be null and must be the same length
     * @post Returns true if words differ by exactly one letter
     * @return whether only one letter differs
     */
    private boolean isOneLetterDifferent(String word, String previousWord) {
        assert word != null && previousWord != null : "Words must not be null";
        word = word.toLowerCase();
        previousWord = previousWord.toLowerCase();
        if (word.length() != previousWord.length()) return false;

        int diff = 0;
        for (int i = 0; i < previousWord.length(); i++) {
            if (word.charAt(i) != previousWord.charAt(i)) diff++;
        }
        return diff == 1;
    }

    /**
     * @post Returns true if the last move matches the target word
     * @return whether the game has been won
     */
    public boolean isWin() {
        if (moveHistory.isEmpty()) return false;
        return moveHistory.get(moveHistory.size() - 1).equalsIgnoreCase(targetWord);
    }

    /**
     * @post Game state is reset and observers are notified
     */
    public void restartGame() {
        moveHistory.clear();
        currentWord = "";
        statusMessage = "";
        errorMessage = "";
        setChanged();
        notifyObservers("NEW_GAME");
    }

    /**
     * @post Last move is undone if any
     */
    public void undoMove() {
        if (!moveHistory.isEmpty()) {
            moveHistory.remove(moveHistory.size() - 1);
            currentWord = "";
            statusMessage = "";
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @param startWord the new start word
     * @pre startWord must not be null
     * @post startWord is updated and observers are notified
     */
    public void setStartWord(String startWord) {
        assert startWord != null : "Start word must not be null";
        this.startWord = startWord;
        setChanged();
        notifyObservers();
    }

    /**
     * @param targetWord the new target word
     * @pre targetWord must not be null
     * @post targetWord is updated and observers are notified
     */
    public void setTargetWord(String targetWord) {
        assert targetWord != null : "Target word must not be null";
        this.targetWord = targetWord;
        setChanged();
        notifyObservers();
    }

    /**
     * @pre moveHistory may be empty
     * @post Returns last word or empty string if none
     * @return last word in history or empty string
     */
    public String getpreviousword() {
        return (moveHistory == null || moveHistory.isEmpty()) ? "" : moveHistory.get(moveHistory.size() - 1);
    }

    /**
     * @post Returns latest error message
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param start start word
     * @param target target word
     * @pre Both words must be valid dictionary words and same length
     * @post Returns a valid transformation path from start to target or null
     * @return path from start to target
     */
    public List<String> findPath(String start, String target) {
        assert isValidWord(start) && isValidWord(target) : "Words must be valid dictionary words";
        assert start.length() == target.length() : "Words must be the same length";

        start = start.toLowerCase();
        target = target.toLowerCase();

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
                }

                assert path.get(0).equals(start) && path.get(path.size() - 1).equals(target) : "Path must be valid";

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

        return null;
    }

    /**
     * @param word the word to get neighbors for
     * @pre word must not be null
     * @post Returns a list of one-letter-different words in dictionary
     * @return list of neighbor words
     */
    private List<String> getNeighbors(String word) {
        assert word != null : "Word must not be null";
        List<String> neighbors = new ArrayList<>();
        for (String dictWord : dictionary) {
            if (isOneLetterDifferent(word, dictWord)) {
                neighbors.add(dictWord);
            }
        }
        return neighbors;
    }
}
