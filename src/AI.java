import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AI {
    // --- TRAINING PARAMETERS ---
    static final int TRAINING_EPISODES = 10000;
    static final double ALPHA = 0.1;
    static final double GAMMA = 0.9;
    static final double EPSILON_TRAINING = 1.0;

    // Q-Table - built during training phase
    HashMap<String, double[]> qTable = new HashMap<>();
    static Random rand = new Random();

    // --- THE TRAINING LOOP (SELF-PLAY VERSION) ---
    public void runTrainingLoop() {
        System.out.println("AI is practicing via Self-Play... Training over " + TRAINING_EPISODES + " games.");
        long startTime = System.currentTimeMillis();

        for (int ep = 1; ep <= TRAINING_EPISODES; ep++) {
            char[] tBoard = {'_', '_', '_', '_', '_', '_', '_', '_', '_'};
            boolean gameOver = false;

            int playerIndex = 0; // 0 for 'X' (First), 1 for 'O' (Second)
            char[] players = {'X', 'O'};

            // Track the previous state and action for BOTH players independently
            String[] prevState = new String[2];
            int[] prevAction = {-1, -1};

            // EPSILON starts at 1.0 (full exploration). Decrease alongside training loop to use more and more Q-values (exploitation)
            double currentEpsilon = Math.max(0.01, EPSILON_TRAINING - ((double) ep / (TRAINING_EPISODES * 0.8)));

            while (!gameOver) {
                char currentPlayer = players[playerIndex];
                String currentStateStr = new String(tBoard);
                List<Integer> availableMoves = Utils.getAvailableMoves(tBoard);

                // Check for Draw
                if (availableMoves.isEmpty()) {
                    // Update rewards in Q-table if game overs with draw (reward = 2.0 for draw)
                    if (prevState[0] != null)
                        updateQ(prevState[0], prevAction[0], 2.0, currentStateStr, availableMoves);
                    if (prevState[1] != null)
                        updateQ(prevState[1], prevAction[1], 2.0, currentStateStr, availableMoves);
                    break;
                }

                // Both players use the exact same AI logic, sharing the same Q-Table
                int chosenAction;

                // Choose between exploration (random) VS exploitation (maxQValue) based on EPSILON
                if (rand.nextDouble() < currentEpsilon) {
                    chosenAction = availableMoves.get(rand.nextInt(availableMoves.size())); // Explore
                } else {
                    chosenAction = chooseAIAction(currentStateStr, availableMoves, 0.0); // Exploit
                }

                // Update the Q-value for THIS player's PREVIOUS turn
                // Use 'currentStateStr' to look at how good the board looks for them NOW
                if (prevState[playerIndex] != null) {
                    updateQ(prevState[playerIndex], prevAction[playerIndex], 0.0, currentStateStr, availableMoves);
                }

                // Save state and action so we can update it on this player's NEXT turn
                prevState[playerIndex] = currentStateStr;
                prevAction[playerIndex] = chosenAction;

                // Apply the move
                tBoard[chosenAction] = currentPlayer;

                // Check for Win/Loss
                if (Utils.checkWin(tBoard, currentPlayer)) {
                    // The player who just moved WINS (+10)
                    updateQ(prevState[playerIndex], prevAction[playerIndex], 10.0, new String(tBoard), new ArrayList<>());

                    // The other player LOSES (-10)
                    int otherPlayer = 1 - playerIndex;
                    if (prevState[otherPlayer] != null) {
                        updateQ(prevState[otherPlayer], prevAction[otherPlayer], -10.0, new String(tBoard), new ArrayList<>());
                    }
                    gameOver = true;
                }

                // Switch turn (0 becomes 1, 1 becomes 0)
                playerIndex = 1 - playerIndex;
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Training completed in " + (endTime - startTime) + " ms!");
        System.out.println("Total unique board states mastered: " + qTable.size());
        System.out.println("Opening GUI window... Good luck!");
    }

    public double[] getQValues(String state) {
        if (!qTable.containsKey(state)) {
            // The state is not known by AI in its Q-table. Add it.
            double[] initialQ = new double[9];
            for (int i = 0; i < 9; i++) initialQ[i] = rand.nextDouble() * 0.1;
            qTable.put(state, initialQ);
        }
        return qTable.get(state);
    }

    public int chooseAIAction(String state, List<Integer> availableMoves, double epsilon) {
        if (rand.nextDouble() < epsilon) {
            return availableMoves.get(rand.nextInt(availableMoves.size())); // Explore
        }

        // Exploit
        double[] qValues = getQValues(state);
        double bestValue = -1e9;
        int bestAction = availableMoves.get(0);

        for (int action : availableMoves) {
            if (qValues[action] > bestValue) {
                bestValue = qValues[action];
                bestAction = action;
            }
        }
        return bestAction;
    }

    private void updateQ(String state, int action, double reward, String nextState, List<Integer> nextMoves) {
        if (state == null || action == -1) return;

        double[] qValues = getQValues(state);
        double oldQ = qValues[action];

        double maxNextQ = 0.0;
        if (!nextMoves.isEmpty()) {
            double[] nextQValues = getQValues(nextState);
            maxNextQ = -1e9;
            for (int a : nextMoves) {
                maxNextQ = Math.max(maxNextQ, nextQValues[a]);
            }
        }

        // Bellman Update Rule
        qValues[action] = oldQ + ALPHA * (reward + GAMMA * maxNextQ - oldQ);
    }
}
