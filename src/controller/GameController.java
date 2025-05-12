package controller;

import model.ModelInterface;
import view.CombinedView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController implements ActionListener {
    private final ModelInterface model;
    private CombinedView view;

    public GameController(ModelInterface model) { // Remove view parameter
        this.model = model;
    }

    public void setView(CombinedView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "NEW_GAME":
                model.setRandomWords(true); // Regenerate random words
                model.restartGame();
                break;
            case "RESET":
                model.undoMove();
                break;
            case "ENTER":
                if (view != null) {
                    view.submitGuess();
                }
                break;
            case "BACKSPACE":
                if (view != null) {
                    view.removeLastLetter();
                }
                break;
            default:
                if (command.length() == 1 && view != null) {
                    view.appendGuess(command);
                }
                break;
        }
    }
}