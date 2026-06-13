# 🧠 TicTacToeAI
## Q-Learning: Tabular Reinforcement Learning in Java

This project implements a Q-Learning agent that learns to master the game of **Tic-Tac-Toe** via self-play.

Built entirely in standard Java (including a Swing-based GUI), this project demonstrates how classical tabular reinforcement learning can solve discrete state-space environments without relying on external machine learning libraries.

## 📋 Table of Contents
Project Purpose
Reinforcement Learning Fundamentals
The Bellman Equation
Hyperparameters Explained
Implementation in Tic-Tac-Toe
State Space & The Q-Table
Training Architecture (Self-Play)
Interpreting the GUI & Q-Matrix
Getting Started (How to Run)

## 🎯 Project Purpose
The primary goal of this project is educational: to bridge the gap between theoretical reinforcement learning mathematics and practical software engineering.

While modern AI often relies on Deep Neural Networks (Deep Q-Learning), standard tabular Q-learning remains the most mathematically transparent way to understand how an AI learns temporal credit assignment (learning from delayed rewards).

By mapping an AI's brain onto a visual Tic-Tac-Toe board, this project provides a real-time window into the machine's decision-making process.

## 🤖 Reinforcement Learning Fundamentals
**Q-Learning** is a model-free reinforcement learning algorithm. The agent explores an environment, takes actions, and receives rewards or penalties. Over time, it populates a **"Q-Table"** (Quality Table), which acts as a cheat sheet. For any given state, the Q-Table tells the agent the expected future value of every possible action.

### The Bellman Equation
The core of the learning algorithm is driven by the **Bellman Equation**, which updates the Q-value of a past action based on the newly discovered information.

$$Q(S,A)←Q(S,A)+α[R+γ max_aQ(S′,a) − Q(S,A)]$$

- $$S$$: Current State (the board layout).
- $$A$$: Action taken (placing a mark on an index).
- $$R$$: Reward received after taking action A.
- $$S′$$: The resulting next state.
- $$max_a Q(S′,a)$$: The highest possible Q-value achievable from the next state (looking one step ahead).

### Hyperparameters Explained
The training loop is governed by several critical parameters:
- **Learning Rate (α / Alpha = 0.1):** Determines to what extent newly acquired information overrides old information. At 0.1, the AI gently shifts its beliefs rather than wildly overwriting them based on a single game.
- **Discount Factor (γ / Gamma = 0.9):** Determines the importance of future rewards. A factor of 0.9 means the AI values long-term strategy (setting up a fork) almost as highly as an immediate reward (getting 3-in-a-row).
- **Exploration Rate (ϵ / Epsilon):** Governs the Exploration vs. Exploitation dilemma. During training, ϵ starts high (e.g., 1.0) so the AI plays entirely randomly to map the board. It decays dynamically to near 0.01 so the AI can practice exploiting its learned strategies. During GUI gameplay, ϵ is strictly set to 0.0 to ensure the AI plays flawlessly.

## ⚙️ Implementation in Tic-Tac-Toe
### State Space & The Q-Table
The environment is represented natively in Java:
- **State** (`S`): A 9-character String representing the board (e.g., `"X_O__X___"`).`
- **Action** (`A`): An integer from 0 to 8 representing the chosen cell.
- **Data Structure**: A `HashMap<String, double[]>` where the Key is the state string, and the Value is an array of 9 floating-point numbers representing the Q-values for each index.

**The State Space Reality:** While $$3^9$$ equals 19,683 theoretical combinations, there are exactly 5,478 legally reachable board states in a standard game. A successful training run will populate the HashMap with precisely this number of unique states.

### Training Architecture (Self-Play)
If an AI is trained against a purely random opponent, it never learns how to defend against high-level tactics. If trained against a flawless opponent, it never sees what human mistakes look like.

This project utilizes Self-Play. The AI plays hundreds of thousands of games against an exact clone of itself, sharing the same Q-Table.

- **Symmetrical Learning:** The AI learns how to play effectively whether going first ('X') or second ('O').
- **Delayed Rewards:** Rewards are only issued at the end of the game (+10 for a win, -10 for a loss, +2 for a draw). The Bellman equation handles trickling these rewards backward to the early game moves.

## 📊 Interpreting the GUI & Q-Matrix
The GUI is not just a game interface; it is an active visualizer of the Q-Table. When you play against the AI, the empty cells on the board will display numerical values.

### How to read the Matrix:
The numbers represent the AI's internal evaluation of your possible moves (since the AI assumes you want to maximize your own score).
- **High Positive Values (e.g., 7.50, 9.10)**: The AI calculates that playing in this cell is highly advantageous and leads toward a win.
- **Near Zero Values (e.g., 0.15, 1.80)**: The AI calculates that playing here will likely result in a draw, assuming both players play optimally from that point forward.
- **High Negative Values (e.g., -8.00, -9.50)**: The AI knows that playing in this cell is a critical blunder that opens up a guaranteed win for the opponent.

When it is the AI's turn, it simply scans these values and picks the cell with the highest mathematical score.

## 🚀 Getting Started
### Prerequisites
Java Development Kit (JDK) 8 or higher installed on your machine.
### Installation & Execution
1. Clone the repository:
```Bash
git clone https://github.com/vahand/TicTacToeAI.git
cd TicTacToeAI/
```
2. Compile the Java source code:
```Bash
javac Main.java
```
3. Run the application:
```Bash
java Main
```

### What to Expect upon Execution
1. **Headless Training:** The console will immediately begin simulating a certain amount of games of self-play.
2. **Terminal Output:** The terminal will output the total number of unique states mastered (verifying the training success).
3. **GUI Launch:** The Swing GUI will launch, prompting you to choose your turn order. Prepare to face an unbreakable opponent!
