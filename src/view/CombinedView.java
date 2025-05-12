package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.TitledBorder;

import model.ModelInterface;
import java.util.Observable;
import java.util.Observer;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import controller.GameController;

public class CombinedView extends JFrame implements Observer, ActionListener, KeyListener, FocusListener {

    private final ModelInterface model;
    private final WordGridPanel gridPanel;
    private final KeyboardPanel keyboard;
    private final JButton newGameButton;
    private final JButton showPathButton;
    private final JButton undoButton;
    private final JButton initializeButton;
    private String currentGuess = "";
    private String previousWord = "";
    private boolean isFirstMove = true;
    private String startWord;
    private String targetWord;
    private String initialStartWord;
    private String initialTargetWord;
    private JDialog pathDialog;
    private JTextArea pathTextArea;
    private JDialog messageDialog;
    private JTextArea messageDialogTextArea;
    private JButton okButton;
    private boolean gameWon = false;
    private JButton settingsButton;
    private JDialog settingsDialog;
    private JCheckBox showErrorMessagesCheckBox;
    private JCheckBox showAnswerPathCheckBox;
    private JCheckBox enableRandomWordCheckBox;
    private boolean showErrorMessages = true;
    private boolean enableRandomWord = true;
    private ActionListener controller;
    private JButton saveButton; // saveButton


    public CombinedView(ModelInterface model, ActionListener controller) {
        super("Word Ladder");

        this.model = model;
        this.controller = controller;
        model.addObserver(this);

        if (controller instanceof GameController) {
            ((GameController) controller).setView(this);
        }

        initialStartWord = model.getStartWord();
        initialTargetWord = model.getTargetWord();

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel gameNameLabel = new JLabel("Weaver");
        gameNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(gameNameLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        newGameButton = new JButton("New Game");
        newGameButton.setActionCommand("NEW_GAME");
        newGameButton.addActionListener(controller);
        newGameButton.setFocusable(false);
        buttonPanel.add(newGameButton);

        showPathButton = new JButton("Show Path");
        showPathButton.setActionCommand("SHOW_PATH");
        showPathButton.addActionListener(this);
        showPathButton.setFocusable(false);
        buttonPanel.add(showPathButton);

        undoButton = new JButton("Undo");
        undoButton.setActionCommand("UNDO");
        undoButton.addActionListener(this);
        undoButton.setEnabled(false);
        undoButton.setFocusable(false);
        buttonPanel.add(undoButton);

        initializeButton = new JButton("Initialize");
        initializeButton.setActionCommand("INITIALIZE");
        initializeButton.addActionListener(this);
        initializeButton.setFocusable(false);
        buttonPanel.add(initializeButton);

        settingsButton = new JButton("Settings");
        settingsButton.setActionCommand("SETTINGS");
        settingsButton.addActionListener(this);
        settingsButton.setFocusable(false);
        buttonPanel.add(settingsButton);

        topPanel.add(buttonPanel);
        container.add(topPanel, BorderLayout.NORTH);

        JPanel gridContainer = new JPanel(new BorderLayout());
        gridContainer.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Word Ladder Moves");
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        gridContainer.setBorder(titledBorder);

        JPanel startWordPanel = new JPanel();
        startWordPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        startWordPanel.setBackground(Color.WHITE);

        JPanel movesPanel = new JPanel();
        movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
        movesPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(movesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JPanel targetWordPanel = new JPanel();
        targetWordPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        targetWordPanel.setBackground(Color.WHITE);
        targetWordPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        gridPanel = new WordGridPanel(model);
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.Y_AXIS));
        gridPanel.add(startWordPanel);
        gridPanel.add(scrollPane);
        gridPanel.add(targetWordPanel);

        gridContainer.add(gridPanel, BorderLayout.CENTER);
        container.add(gridContainer, BorderLayout.CENTER);

        keyboard = new KeyboardPanel(this);
        container.add(keyboard, BorderLayout.SOUTH);

        setContentPane(container);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        startWord = model.getStartWord();
        targetWord = model.getTargetWord();
        updateGrid();

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        addFocusListener(this);

        pathDialog = new JDialog(this, "Word Ladder Path", false);
        pathDialog.setSize(300, 200);
        pathDialog.setLocationRelativeTo(this);

        pathTextArea = new JTextArea();
        pathTextArea.setEditable(false);
        JScrollPane scrollPane2 = new JScrollPane(pathTextArea);
        pathDialog.add(scrollPane2);

        messageDialog = new JDialog(this, "Message", false);
        messageDialog.setSize(300, 200);
        messageDialog.setLocationRelativeTo(this);
        messageDialog.setLayout(new BorderLayout());

        messageDialogTextArea = new JTextArea();
        messageDialogTextArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageDialogTextArea);
        messageDialog.add(messageScrollPane, BorderLayout.CENTER);

        okButton = new JButton("OK");
        okButton.addActionListener(e -> messageDialog.setVisible(false));
        JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel2.add(okButton);
        messageDialog.add(buttonPanel2, BorderLayout.SOUTH);

        settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setSize(300, 200);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setLayout(new BorderLayout());

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        checkBoxPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        showErrorMessagesCheckBox = new JCheckBox("Show Error Messages", showErrorMessages);
        showAnswerPathCheckBox = new JCheckBox("Disable Show Path", false);
        enableRandomWordCheckBox = new JCheckBox("Enable Random Word", enableRandomWord);

        checkBoxPanel.add(showErrorMessagesCheckBox);
        checkBoxPanel.add(showAnswerPathCheckBox);
        checkBoxPanel.add(enableRandomWordCheckBox);

        settingsDialog.add(checkBoxPanel, BorderLayout.CENTER);

        saveButton = new JButton("Save"); // saveButton
        saveButton.addActionListener(e -> {
            showErrorMessages = showErrorMessagesCheckBox.isSelected();
            showPathButton.setEnabled(!showAnswerPathCheckBox.isSelected());
            enableRandomWord = enableRandomWordCheckBox.isSelected();

            model.setRandomWords(enableRandomWord);

            if (!enableRandomWord) {
                model.getMoveHistory().clear();
                model.setStartWord("WAST");
                model.setTargetWord("WEST");
            }

            settingsDialog.setVisible(false);
            newGameButton.setEnabled(enableRandomWord);
        });

        JPanel saveButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButtonPanel.add(saveButton);
        settingsDialog.add(saveButtonPanel, BorderLayout.SOUTH);

        newGameButton.setEnabled(enableRandomWord);
    }

    public void updateGrid() {
        gridPanel.updateGrid(model.getMoveHistory(), model.getStartWord(), model.getTargetWord(), currentGuess);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("CombinedView.update() called with arg: " + arg);
        startWord = model.getStartWord();
        targetWord = model.getTargetWord();
        updateGrid(); // Call updateGrid method

        if (model.getMoveHistory().size() > 0) {
        } else {
        }

        undoButton.setEnabled(!model.getMoveHistory().isEmpty());

        if (arg instanceof String) {
            String message = (String) arg;
            switch (message) {
                case "NO CHANGE":
                case "NOT IN DICTIONARY":
                case "NOT ONE LETTER DIFFERENT":
                    if (showErrorMessages) {
                        showMessageDialog(message); // Show message dialog
                    }
                    break;
                case "VALID MOVE":
                    break;
                case "NEW_GAME": // Listen for "NEW_GAME"
                    currentGuess = "";
                    updateGrid();
                    break;
                case "GAME WON":
                    break;
                default:
                    break;
            }
        }
    }

    public void showMessage(String message) {
    }

    // Show message dialog
    private void showMessageDialog(String message) {
        messageDialogTextArea.setText(message);
        messageDialog.setVisible(true);
    }

    public void appendGuess(String letter) {
        if (currentGuess.length() < 4) {
            currentGuess += letter;
            updateGrid();
            requestFocusInWindow();
        }
    }

    public void removeLastLetter() {
        if (currentGuess.length() > 0) {
            currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
            updateGrid();
            requestFocusInWindow();
        }
    }

    public void submitGuess() {
        if (currentGuess.length() == 4) {
            if (model.makeMove(currentGuess)) {
                previousWord = currentGuess;
                currentGuess = "";
                updateGrid();
                isFirstMove = false;

                if (model.isWin()) {
                    showMessageDialog("Congratulations! You won!");
                    gameWon = true; // Set game won flag
                    disableKeyboard(); // Disable keyboard
                    updateGrid(); // Update grid again
                }
            } else {
                showMessage(model.getErrorMessage());
            }
        } else {
            showMessage("Please enter a 4-letter word.");
        }
        requestFocusInWindow();
    }

    // Disable keyboard panel
    private void disableKeyboard() {
        for (Component component : keyboard.getComponents()) {
            if (component instanceof JPanel) {
                for (Component button : ((JPanel) component).getComponents()) {
                    if (button instanceof JButton) {
                        button.setEnabled(false);
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.println("CombinedView.actionPerformed() called with command: " + command);

        switch (command) {
            case "NEW_GAME":
                enableKeyboard();
                currentGuess = ""; // Clear the current guess
                gameWon = false; // Reset game won flag
                updateGrid();
                break;
            case "SHOW_PATH":
                List<String> path = model.findPath(model.getStartWord(), model.getTargetWord());
                if (path != null && !path.isEmpty()) {
                    StringBuilder pathString = new StringBuilder();
                    for (int i = 0; i < path.size(); i++) {
                        pathString.append(path.get(i));
                        if (i < path.size() - 1) {
                            pathString.append(" -> ");
                        }
                    }
                    pathTextArea.setText(pathString.toString()); // Update text area
                    pathDialog.setVisible(true); // Show dialog
                } else {
                    showMessage("No path found!");
                }
                requestFocusInWindow();
                break;
            case "UNDO":
                model.undoMove();
                currentGuess = "";
                updateGrid();
                requestFocusInWindow();
                break;
            case "INITIALIZE":
                // Reset game to initial state
                if (enableRandomWord) {
                    model.setStartWord(initialStartWord);
                    model.setTargetWord(initialTargetWord);
                }
                model.restartGame();
                currentGuess = "";
                updateGrid();
                break;
            case "SETTINGS":
                settingsDialog.setVisible(true);
                break;
            default:
                if (command.length() == 1) {
                    appendGuess(command);
                }
                break;
        }
    }

    // Enable keyboard panel
    private void enableKeyboard() {
        for (Component component : keyboard.getComponents()) {
            if (component instanceof JPanel) {
                for (Component button : ((JPanel) component).getComponents()) {
                    if (button instanceof JButton) {
                        button.setEnabled(true);
                    }
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if (Character.isLetter(keyChar) && currentGuess.length() < 4) {
            appendGuess(String.valueOf(keyChar).toUpperCase());
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            removeLastLetter();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            submitGuess();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void focusGained(FocusEvent e) {
        requestFocusInWindow(); // Request focus when focus is gained
    }

    @Override
    public void focusLost(FocusEvent e) {
        // Optional: Handle focus lost
    }

    public class KeyboardPanel extends JPanel {
        private ActionListener controller;

        public KeyboardPanel(ActionListener controller) {
            this.controller = controller;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            String[] row1Letters = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
            for (String letter : row1Letters) {
                JButton button1 = new JButton(letter);
                button1.addActionListener(e -> controller.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, letter)));
                button1.setFocusable(false); // Make button unfocusable
                row1.add(button1);
            }
            add(row1);

            JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            String[] row2Letters = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
            for (String letter : row2Letters) {
                JButton button2 = new JButton(letter);
                button2.addActionListener(e -> controller.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, letter)));
                button2.setFocusable(false); // Make button unfocusable
                row2.add(button2);
            }
            add(row2);

            JPanel row3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            String[] row3Letters = {"Z", "X", "C", "V", "B", "N", "M"};
            for (String letter : row3Letters) {
                JButton button3 = new JButton(letter);
                button3.addActionListener(e -> controller.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, letter)));
                button3.setFocusable(false); // Make button unfocusable
                row3.add(button3);
            }

            JButton deleteButton = new JButton("⌫");
            deleteButton.addActionListener(e -> controller.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "BACKSPACE")));
            deleteButton.setFocusable(false); // Make button unfocusable
            row3.add(deleteButton);

            JButton submitButton = new JButton("→");
            submitButton.addActionListener(e -> controller.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "ENTER")));
            submitButton.setFocusable(false); // Make button unfocusable
            row3.add(submitButton);

            add(row3);
        }
    }

    public class WordGridPanel extends JPanel implements Observer {

        private String startWord;
        private String targetWord;
        private String currentWord;
        private List<String> moveHistory;
        private final JLabel messageLabel;
        private int currentRowIndex;
        private ModelInterface model;
        private List<JPanel> wordPanels;
        private String submittedGuess; // Add a variable to store the submitted guess
        private boolean gameWon = false;
        public WordGridPanel(ModelInterface model) {
            this.model = model;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(5, 20, 20, 20));
            setBackground(Color.WHITE);
            moveHistory = new ArrayList<>();
            currentWord = "";
            messageLabel = new JLabel("", SwingConstants.CENTER);
            currentRowIndex = 0;
            wordPanels = new ArrayList<>();
            model.addObserver(this);
            this.submittedGuess = ""; // Initialize to empty string
        }

        public void updateGrid(List<String> moveHistory, String startWord, String targetWord, String currentWord) {
            System.out.println("WordGridPanel.updateGrid() called");
            SwingUtilities.invokeLater(() -> {
                this.startWord = startWord;
                this.targetWord = targetWord;
                this.currentWord = currentWord;
                this.moveHistory = new ArrayList<>(moveHistory);
                this.gameWon = model.isWin();

                removeAll();
                wordPanels.clear();


                // Add start word
                JPanel startWordPanel = createWordPanel(startWord != null ? startWord : " ", false); // startWord should not be highlighted
                add(startWordPanel);

                // Create a JPanel to contain moveHistory and currentWord
                JPanel movesPanel = new JPanel();
                movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
                movesPanel.setBackground(Color.WHITE);

                // Add moveHistory and currentWord to movesPanel
                for (String word : moveHistory) {
                    addWordPanelToPanel(word != null ? word : " ", true, movesPanel); // History moves should be highlighted
                }

                // Only add currentWord if the game is not won
                if (!gameWon) {
                    addWordPanelToPanel(currentWord, false, movesPanel); // currentWord should not be highlighted
                }

                // Put movesPanel into JScrollPane
                JScrollPane scrollPane = new JScrollPane(movesPanel);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setPreferredSize(new Dimension(300, 350)); // Set preferred size
                add(scrollPane);

                // Add target word
                JPanel targetWordPanel = createWordPanel(targetWord != null ? targetWord : " ", true); // targetWord should be highlighted
                targetWordPanel.setBorder(new EmptyBorder(0, 0, 20, 0)); // Reduce top margin, increase bottom margin
                add(targetWordPanel);

                revalidate();
                repaint();
            });
        }

        private void addWordPanelToPanel(String word, boolean shouldHighlight, JPanel panel) {
            JPanel wordPanel = createWordPanel(word, shouldHighlight);
            wordPanels.add(wordPanel);
            panel.add(wordPanel);
        }

        private JPanel createWordPanel(String word, boolean shouldHighlight) {
            System.out.println("WordGridPanel.createWordPanel() called with word: " + word + ", shouldHighlight: " + shouldHighlight);
            JPanel wordPanel = new JPanel(new FlowLayout());
            wordPanel.setBackground(Color.WHITE);

            Border border = BorderFactory.createLineBorder(Color.GRAY);

            for (int i = 0; i < 4; i++) {
                String letter = (i < word.length()) ? String.valueOf(word.charAt(i)).toUpperCase() : " ";
                JButton letterButton = new JButton(letter);
                letterButton.setPreferredSize(new Dimension(50, 50));
                letterButton.setFont(new Font("Arial", Font.BOLD, 20));
                letterButton.setFocusPainted(false);
                letterButton.setEnabled(false);
                letterButton.setBorder(border);

                if (shouldHighlight && targetWord != null && i < targetWord.length() && letter.equals(String.valueOf(targetWord.charAt(i)).toUpperCase())) {
                    letterButton.setBackground(Color.GREEN);
                    letterButton.setForeground(Color.WHITE);
                }

                wordPanel.add(letterButton);
            }

            return wordPanel;
        }

        @Override
        public void update(Observable o, Object arg) {
            if (o == model) {
                if (arg instanceof String && arg.equals("VALID_MOVE")) {
                    this.submittedGuess = model.getMoveHistory().get(model.getMoveHistory().size() - 1);
                } else {
                    this.submittedGuess = "";
                }
                updateGrid(model.getMoveHistory(), model.getStartWord(), model.getTargetWord(), currentWord);
            }
        }
    }
}