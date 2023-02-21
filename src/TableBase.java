import java.util.HashMap;
import java.util.Map;

public class TableBase {
    public static Map<String, Move> data = new HashMap<>();
    public static Map<Integer, String> letters = new HashMap<>();

    public static void initDatabase() {

    }

    public static void init() {
        letters.put(Chess.EMPTY, "");
        letters.put(Chess.WHITE_PAWN, "P");
        letters.put(Chess.WHITE_KNIGHT, "N");
        letters.put(Chess.WHITE_BISHOP, "B");
        letters.put(Chess.WHITE_ROOK, "R");
        letters.put(Chess.WHITE_QUEEN, "Q");
        letters.put(Chess.WHITE_KING, "K");
        letters.put(Chess.BLACK_PAWN, "p");
        letters.put(Chess.BLACK_KNIGHT, "n");
        letters.put(Chess.BLACK_BISHOP, "b");
        letters.put(Chess.BLACK_ROOK, "r");
        letters.put(Chess.BLACK_QUEEN, "q");
        letters.put(Chess.BLACK_KING, "k");

        initDatabase();
    }

    public static Move get(int[][] board, boolean white) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int emptySquares = 0;
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        sb.append(emptySquares);
                        emptySquares = 0;
                    }
                    sb.append(letters.get(board[i][j]));
                }
            }
            if (emptySquares > 0) {
                sb.append(emptySquares);
            }
            if (i < 7) {
                sb.append("/");
            }
        }
        if (white) {
            sb.append(" w KQkq -");
        } else {
            sb.append(" b KQkq -");
        }
        String fen = sb.toString();

        return data.get(fen);
    }
}
