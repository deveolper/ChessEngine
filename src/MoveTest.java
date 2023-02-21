import org.junit.Assert;
import org.junit.Test;

public class MoveTest {
    @Test
    public void testConstructFromUCI() {
        // Act
        Move move = new Move("e2e4");

        // Assert
        Assert.assertEquals("e2e4", move.toString());
    }

    @Test
    public void testToStringWithQueenPromotion() {
        // Arrange
        Move move = new Move("e7e8q");

        // Act
        String actual = move.toString();

        // Assert
        Assert.assertEquals("e7e8q", actual);
    }

    @Test
    public void testToStringWithRookPromotion() {
        // Arrange
        Move move = new Move("e7e8r");

        // Act
        String actual = move.toString();

        // Assert
        Assert.assertEquals("e7e8r", actual);
    }

    @Test
    public void testToStringWithBishopPromotion() {
        // Arrange
        Move move = new Move("e7e8b");

        // Act
        String actual = move.toString();

        // Assert
        Assert.assertEquals("e7e8b", actual);
    }

    @Test
    public void testToStringWithKnightPromotion() {
        // Arrange
        Move move = new Move("e7e8n");

        // Act
        String actual = move.toString();

        // Assert
        Assert.assertEquals("e7e8n", actual);
    }

    @Test
    public void testConstructWithInvalidPromotion() {
        // Act / Assert
        Assert.assertThrows("Invalid promotion", RuntimeException.class, () -> new Move("e7e8w"));
    }

    @Test
    public void testDefaultConstructor() {
        // Act
        Move move = new Move();

        // Assert
        Assert.assertEquals(move.toString(), "DEFAULT MOVE");
    }

    @Test
    public void testEquals() {
        // Arrange
        Move first = new Move("e2e4");
        Move second = new Move("d2d4");
        Move third = new Move("d2d4");
        int somethingElse = 34;

        // Act
        boolean firstResult = first.equals(second);
        boolean secondResult = first.equals(third);
        boolean thirdResult = second.equals(third);
        boolean fourthResult = first.equals(somethingElse);

        // Assert
        Assert.assertFalse(firstResult);
        Assert.assertFalse(secondResult);
        Assert.assertTrue(thirdResult);
        Assert.assertFalse(fourthResult);
    }

    @Test
    public void testIsDefault() {
        // Arrange
        Move defaultMove = new Move();
        Move e4 = new Move("e2e4");
        Move d4 = new Move("d2d4");

        // Act
        boolean firstResult = defaultMove.isDefault();
        boolean secondResult = e4.isDefault();
        boolean thirdResult = d4.isDefault();

        // Assert
        Assert.assertTrue(firstResult);
        Assert.assertFalse(secondResult);
        Assert.assertFalse(thirdResult);
    }
}
