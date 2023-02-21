public class Evaluator {
    public static final double MAX = Double.MAX_VALUE;
    public static final double MIN = Double.MIN_VALUE;

    public double evaluateBoard(int[][] board, NeuralNetwork network) {
        double[] flatBoard = new double[64];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                flatBoard[i*8+j] = board[i][j];
            }
        }

        return network.evaluate(flatBoard);
    }
}