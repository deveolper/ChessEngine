import java.util.Scanner;

public class Main {
    private static final int MAX_DEPTH = 3;

    public static void main(String[] args) {
        TableBase.init();

        Scanner scanner = new Scanner(System.in);

        String fen = Chess.START_POSITION;
        Chess engine = new Chess(fen, new NeuralNetwork(new Layer[]{
                new Layer(new Node[]{
                        new Node(0.0, new double[]{0.0,0.1,0.0,0.0,0.0,0.0,0.0,0.0,0.4,0.1,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.1,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0}),
                        new Node(0.0, new double[]{0.0,0.2,0.1,0.0,0.0,0.4,1.0,0.3,0.5,0.0,1.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.2,0.0,0.2,0.0,0.5,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0}),
                        new Node(0.0, new double[]{0.0,0.3,0.0,0.2,0.3,0.0,0.2,0.5,1.0,0.0,6.0,3.0,0.2,0.0,0.2,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.3,0.1,0.4,0.1,0.0,0.8,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0}),
                        new Node(0.0, new double[]{0.0,2.1,0.0,0.0,0.0,0.1,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.4,0.0,0.0,0.0,0.0,0.5,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0})
                }),
                new Layer(new Node[]{
                        new Node(0.0, new double[]{0.0, 0.3, 0.2, 0.0}),
                        new Node(0.0, new double[]{0.0, 0.4, 0.1, 0.1}),
                        new Node(0.0, new double[]{0.0, 1.2, 0.0, 0.2}),
                        new Node(0.0, new double[]{0.5, 0.0, 0.0, 0.0}),
                        new Node(0.0, new double[]{0.0, 0.0, 0.5, 0.4}),
                        new Node(0.0, new double[]{0.6, 1.3, 0.0, 0.0}),
                        new Node(0.0, new double[]{0.0, 0.0, 0.0, 0.0}),
                        new Node(0.0, new double[]{0.0, 0.0, 0.0, 0.0})
                }),
                new Layer(new Node[]{
                        new Node(0.0, new double[]{0.0, 0.1, 0.2, 0.3, 0.0, 0.0, 0.0, 0.0})
                })
        }));

        System.out.print("What color do you what the engine to play with (w|b|x)? ");
        String color = scanner.nextLine();

        if (color.equals("x")) {
            while (engine.kingLives()) {
                System.out.println(engine.showBoard());
                MoveEvalPair best = engine.bestMove(MAX_DEPTH);
                System.out.printf("%n%s%n", best.move);
                engine.makeMove(best.move);
            }
        }

        if (!color.equals("w") && !color.equals("b")) {
            System.err.println("OOPS: invalid color.");
            return;
        }

        if ((color.equals("w") && engine.whiteToMove) || (color.equals("b") && !engine.whiteToMove)) {
            MoveEvalPair best = engine.bestMove(MAX_DEPTH);
            System.out.println(best);
            engine.makeMove(best.move);
        }

        while (true) {
            System.out.print("Move:");
            String move = scanner.nextLine();

            if (move.equals("exit")) {
                break;
            }

            engine.makeMove(new Move(move));

            MoveEvalPair best = engine.bestMove(MAX_DEPTH);
            System.out.println(best);
            engine.makeMove(best.move);
        }
    }
}
