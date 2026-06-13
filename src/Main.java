import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AI ai = new AI();
        ai.runTrainingLoop();

        // 2. Launch the GUI on the Event Dispatch Thread (standard Swing practice)
        SwingUtilities.invokeLater(() -> {
            new TicTacToeGame(ai);
        });
    }
}