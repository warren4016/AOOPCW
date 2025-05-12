package model;
import java.util.List;
import java.util.Observer;

public interface ModelInterface {
    String getStartWord();
    String getTargetWord();
    String getCurrentWord();
    List<String> getMoveHistory();
    String getStatusMessage();
    boolean makeMove(String word);
    boolean isWin();
    void restartGame();
    void undoMove();
    String getErrorMessage();
    void addObserver(Observer o);
    List<String> findPath(String startWord, String targetWord);
    void setRandomWords(boolean enableRandomWord);
    void setStartWord(String east);
    void setTargetWord(String west);
    String getpreviousword();
}