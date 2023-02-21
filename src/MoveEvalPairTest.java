import org.junit.Assert;
import org.junit.Test;

public class MoveEvalPairTest {
    @Test
    public void testToString() {
        // Arrange
        MoveEvalPair mep = new MoveEvalPair(new Move("e2e4"), 1.3);
        String expected = "e2e4 (1.3)";

        // Act
        String actual = mep.toString();

        // Assert
        Assert.assertEquals(expected, actual);
    }
}
