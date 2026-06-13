import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TicTacToeGame extends JFrame {
    AI ai;

    final private JButton[] buttons = new JButton[9];
    private char[] board = new char[9];
    private JLabel statusLabel;
    private JButton newGameButton;

    boolean humanPlaysFirst = true;

    public TicTacToeGame(AI ai) {
        this.ai = ai;

        initGUI();

        initTurns();
        setVisible(true);
    }

    private void initGUI() {
        setTitle("Q-Learning Tic-Tac-Toe AI");
        setSize(450, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen
        setLayout(new BorderLayout());

        // Header status bar
        statusLabel = new JLabel("Your turn! Play as 'O'", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);

        // 3x3 Grid Panel
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(3, 3, 5, 5));
        gridPanel.setBackground(Color.DARK_GRAY);

        // Initialize board logic and buttons
        for (int i = 0; i < 9; i++) {
            board[i] = '_';
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 50));
            buttons[i].setFocusPainted(false);
            buttons[i].setBackground(Color.WHITE);

            final int index = i;
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        handleHumanMove(index, humanPlaysFirst ? 'X' : 'O');
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            gridPanel.add(buttons[i]);
        }
        add(gridPanel, BorderLayout.CENTER);

        // Bottom control bar
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16));
        newGameButton.addActionListener(e -> resetGame());
        controlPanel.add(newGameButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void initTurns() {
        int response = JOptionPane.showOptionDialog(
                null,
                "Do you want to play first ('X') or second ('O')?",
                "Choose Turn Order",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Play First (X)", "Play Second (O)"},
                "Play First (X)"
        );

        boolean humanPlaysFirst = (response == 0);
        this.humanPlaysFirst = humanPlaysFirst;

        // If human plays second, trigger the AI to make the opening move
        if (!humanPlaysFirst) {
            // Human is 'O', AI is 'X'
            handleAIMove('X');
        } else {
            statusLabel.setText("Your turn! Play as 'X'");
            refreshBoardDisplay();
        }
    }

    private void handleHumanMove(int index, char symbol) throws InterruptedException {
        if (board[index] != '_') return; // Prevent clicking occupied buttons

        board[index] = symbol;
        refreshBoardDisplay();

        if (evaluateGameStatus(symbol)) return;

        handleAIMove(symbol == 'X' ? 'O' : 'X');
    }

    private void handleAIMove(char symbol) {
        statusLabel.setText("AI is thinking...");

        String currentStateStr = new String(board);
        List<Integer> available = Utils.getAvailableMoves(board);

        int aiAction = ai.chooseAIAction(currentStateStr, available, 0.0);

        board[aiAction] = symbol;
        refreshBoardDisplay();

        if (evaluateGameStatus(symbol)) return;

        statusLabel.setText("Your turn! Play as '" + (symbol == 'X' ? 'O' : 'X') + "'");
    }

    private boolean evaluateGameStatus(char player) {
        if (Utils.checkWin(board, player)) {
            statusLabel.setText("Game Over! Player " + player + " wins!");
            endGameDisplay();
            return true;
        }
        if (Utils.getAvailableMoves(board).isEmpty()) {
            statusLabel.setText("Game Over! It's a draw!");
            endGameDisplay();
            return true;
        }
        return false;
    }

    private void endGameDisplay() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(false);
            // Clear the Q-values on empty cells when the game is over
            if (board[i] == '_') {
                buttons[i].setText("");
            }
        }
    }

    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            board[i] = '_';
            buttons[i].setText("");
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setEnabled(true);
        }
        initTurns();
        newGameButton.setEnabled(true);
    }

    private void refreshBoardDisplay() {
        String currentStateStr = new String(board);

        // Fetch the AI's evaluation of the current board state
        double[] qValues = ai.getQValues(currentStateStr);

        for (int i = 0; i < 9; i++) {
            if (board[i] == 'X') {
                buttons[i].setText("X");
                buttons[i].setFont(new Font("Arial", Font.BOLD, 50));
                buttons[i].setForeground(Color.RED);
            } else if (board[i] == 'O') {
                buttons[i].setText("O");
                buttons[i].setFont(new Font("Arial", Font.BOLD, 50));
                buttons[i].setForeground(Color.BLUE);
            } else {
                // It is an empty cell. Show the Q-value for taking this action!
                buttons[i].setText(String.format("%.2f", qValues[i]));
                buttons[i].setFont(new Font("Arial", Font.PLAIN, 18));
                buttons[i].setForeground(Color.GRAY);
            }
        }
    }
}