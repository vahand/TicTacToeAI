import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<Integer> getAvailableMoves(char[] b) {
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (b[i] == '_') moves.add(i);
        }
        return moves;
    }

    public static boolean checkWin(char[] b, char p) {
        return (b[0] == p && b[1] == p && b[2] == p) || // Horizontal
                (b[3] == p && b[4] == p && b[5] == p) ||
                (b[6] == p && b[7] == p && b[8] == p) ||
                (b[0] == p && b[3] == p && b[6] == p) || // Vertical
                (b[1] == p && b[4] == p && b[7] == p) ||
                (b[2] == p && b[5] == p && b[8] == p) ||
                (b[0] == p && b[4] == p && b[8] == p) || // Diagonal
                (b[2] == p && b[4] == p && b[6] == p);
    }
}
