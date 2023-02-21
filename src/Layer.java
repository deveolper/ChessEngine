public class Layer {
    private final Node[] nodes;
    private double[] inputs;

    public Layer(Node[] nodes) {
        this.nodes = nodes;
    }

    public double[] output() {
        double[] outputs = new double[nodes.length];
        int i = 0;
        for (Node node : this.nodes) {
            node.input(this.inputs);
            outputs[i] = node.output;
            i++;
        }
        return outputs;
    }

    public void input(double[] inputs) {
        this.inputs = inputs;
    }
}
