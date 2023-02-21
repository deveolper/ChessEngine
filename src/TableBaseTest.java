import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TableBaseTest {
    @Test
    public void testInit() {
        // Arrange
        Map<Integer, String> letters = new HashMap<>();

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

        // Act
        TableBase.init();

        // Assert
        Assert.assertEquals(TableBase.letters, letters);
    }
}
