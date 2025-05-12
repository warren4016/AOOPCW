package controller;

import Model.ModelInterface;
import view.CombinedView;

import java.util.List;
import java.util.Observer;

/**
 * The GameController class acts as the intermediary between the model and the view in the Weaver game.
 * It is responsible for handling user inputs, interacting with the model, and updating the view.
 *
 * <p>The controller provides methods to retrieve game state information, make moves,
 * check for a win condition, and handle other game operations like restarting the game,
 * undoing moves, and managing error messages. Additionally, it enables random word generation
 * and finds a path between two words. The controller also handles setting start and target words
 * and observing changes in the model.</p>
 *
 * <p>Key methods include:</p>
 *
 * <p>The GameController implements the Model-View-Controller (MVC) design pattern,
 * allowing for easy separation of concerns between the game logic, user interface, and user inputs.</p>
 */

public class GameController{
    private final ModelInterface model;
    private CombinedView view;

    public GameController(ModelInterface model) { // Remove view parameter
        this.model = model;
    }

    public void setView(CombinedView view) {
        this.view = view;
    }

    public String getStartWord(){return model.getStartWord();};
    public String getTargetWord(){return model.getTargetWord();};

    public List<String> getMoveHistory(){return model.getMoveHistory();}

    public boolean makeMove(String word){return model.makeMove(word);};
    public boolean isWin(){return model.isWin();};
    public void restartGame(){model.restartGame();};
    public void undoMove(){model.undoMove();};
    public String getErrorMessage(){return model.getErrorMessage();};
    public void addObserver(Observer o){model.addObserver(o);};
    public List<String> findPath(String startWord, String targetWord){return model.findPath(startWord, targetWord);};
    public void setRandomWords(boolean enableRandomWord){model.setRandomWords(enableRandomWord);};
    public void setStartWord(String east){model.setStartWord(east);};
    public void setTargetWord(String west){model.setTargetWord(west);};

}