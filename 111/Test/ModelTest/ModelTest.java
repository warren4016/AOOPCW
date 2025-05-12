package ModelTest;

import Model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private Model model;

    /**
     * Initializes the Model object before each test case.
     * Loads a simple dictionary file for the model.
     */
    @BeforeEach
    void setUp() {
        model = new Model("dictionary.txt");
    }

    /**
     * Scene 1: Tests the random word generation functionality.
     *
     * Verifies that when random words are enabled:
     * - The start word is not null.
     * - The target word is not null.
     * - The start word and target word are different.
     */
    @Test
    void testRandomWordsGeneration() {
        model.setRandomWords(true);

        // Assert that the start word is not null
        assertNotNull(model.getStartWord());

        // Assert that the target word is not null
        assertNotNull(model.getTargetWord());

        // Assert that the start word and target word are not the same
        assertNotEquals(model.getStartWord(), model.getTargetWord());
    }

    /**
     * Scene 2: Tests making a valid move in the game.
     *
     * Verifies that when trying to make a move with the same word (no change):
     * - The game does not accept the move.
     * - An appropriate error message "No change!" is returned.
     */
    @Test
    void testMakeMoveValid() {
        model.setRandomWords(false); // Use fixed start and target words
        model.setStartWord("WORD");
        model.setTargetWord("WORT");

        // Try making a move with the same word (should not change)
        assertFalse(model.makeMove("WORD"));

        // Assert that the error message is "No change!" for an invalid move
        assertEquals("No change!", model.getErrorMessage());
    }

    /**
     * Scene 3: Tests restarting the game.
     *
     * Verifies that when the game is restarted:
     * - The move history is cleared.
     * - The game state is reset.
     */
    @Test
    void testRestartGame() {
        model.setRandomWords(false); // Use fixed start and target words
        model.setStartWord("WAST");
        model.setTargetWord("WEST");

        // Make a move
        model.makeMove("WAST");

        // Restart the game
        model.restartGame();

        // Assert that the move history is empty after the game is restarted
        assertTrue(model.getMoveHistory().isEmpty());
    }
}
