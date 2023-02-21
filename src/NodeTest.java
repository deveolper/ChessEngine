import org.junit.Assert;
import org.junit.Test;

public class NodeTest {
    @Test
    public void testInputWithTriggeredNode() {
        // Arrange
        Node node = new Node(0.3, new double[]{0.1, 0.2, 0.3, 0.4});

        // Act
        node.input(new double[]{1.0, 0.0, 1.0, 0.0});

        // Assert
        Assert.assertEquals(1.0, node.output, 0.001);
    }

    @Test
    public void testInputWithNotTriggeredNode() {
        // Arrange
        Node node = new Node(0.5, new double[]{0.1, 0.2, 0.3, 0.4});

        // Act
        node.input(new double[]{1.0, 0.0, 1.0, 0.0});

        // Assert
        Assert.assertEquals(0.0, node.output, 0.001);
    }
}
