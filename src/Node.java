public class Node {
    private final double[] weights;
    private final double bias;
    public double output;

    public Node(double bias, double[] weights) {
        this.bias = bias;
        this.weights = weights;
    }

    public void input(double[] inputs) {
        this.output = 0.0;
        if (inputs.length != this.weights.length) {
            throw new RuntimeException("Inputs should be the the same length as weights.%nInputs: %d,%nWeights: %d".formatted(inputs.length, this.weights.length));
        }
        for (int i = 0; i < inputs.length; i++) {
            this.output += inputs[i] * this.weights[i];
        }
        if (this.output >= bias) {
            this.output = 1.0;
        } else {
            this.output = 0.0;
        }
    }
}
