package Cli;

import Model.Model;
import Model.ModelInterface;

import java.util.List;
import java.util.Scanner;

public class WeaverCLI {

    private static final String DICTIONARY_FILE = "dictionary.txt";
    private static final int EXIT_CODE = 0;
    private static final int RESTART_CODE = 1;
    private static final int UNDO_CODE = 2;
    private static final int HINT_CODE = 3;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ModelInterface model = new Model(DICTIONARY_FILE);
        boolean gameRunning = true;

        System.out.println("Welcome to the Word Chain Game!");

        while (gameRunning) {
            displayGameStatus(model);
            String userInput = getValidInput(scanner); // Get input as String

            if (isNumeric(userInput)) {
                int command = Integer.parseInt(userInput); // Try parsing as integer
                switch (command) {
                    case EXIT_CODE:
                        gameRunning = false;
                        System.out.println("Exiting the game.");
                        break;
                    case RESTART_CODE:
                        model.restartGame();
                        System.out.println("Restarting the game...");
                        break;
                    case UNDO_CODE:
                        undoMove(model);
                        break;
                    case HINT_CODE:
                        showPathHint(model);
                        break;
                    default:
                        System.out.println("Invalid numeric command.");
                        break;
                }
            } else {
                if (!model.makeMove(userInput)) {
                    System.out.println("Invalid operation: " + model.getErrorMessage());
                } else {
                    // If input is valid, print the current word
                    System.out.println("The current word has been updated to: " + model.getCurrentWord());
                }
            }

            if (model.isWin()) {
                System.out.println("Congratulations, you win!");
                gameRunning = false;
            }
        }

        scanner.close();
    }

    private static void displayGameStatus(ModelInterface model) {
        System.out.println("\nCurrent game status:");
        System.out.println("Start word: " + model.getStartWord());
        System.out.println("Target word: " + model.getTargetWord());
        System.out.println("Current word: " + model.getpreviousword());
        System.out.println("Number of moves: " + model.getMoveHistory().size());
        System.out.println("Status message: " + model.getStatusMessage());
        if (!model.getErrorMessage().isEmpty()) {
            System.out.println("Error message: " + model.getErrorMessage());
        }
        System.out.println("Available operations:  0(Exit), 1(Restart), 2(Undo), 3(Hint path), Others(Enter a word to play)");
    }

    private static String getValidInput(Scanner scanner) {
        System.out.print("Please enter your operation: ");
        return scanner.nextLine().trim(); // Get input as String
    }

    private static void undoMove(ModelInterface model) {
        model.undoMove();
        System.out.println("The last move has been undone.");
        // After undoing the move, print the current word
        System.out.println("The current word has been updated to: " + model.getCurrentWord());
    }

    private static void showPathHint(ModelInterface model) {
        List<String> path = model.findPath(model.getStartWord(), model.getTargetWord());
        if (path != null) {
            System.out.println("Path hint: " + String.join(" -> ", path));
        } else {
            System.out.println("No path found.");
        }
    }

    private static boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }
}