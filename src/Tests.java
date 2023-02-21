import java.util.List;

public class Tests {
    public static void main(String[] args) {
        run();
    }
    public static void run() {
        testConstructFromFEN();
        testShowBoard();
        testBestMove();
        testLegality();
        testBug();
    }

    public static void testBug() {
        String fen = "1rbqkbnr/pppppppp/2n2P2/2P5/P3P3/1P1P4/6PP/RNBQKBNR w KQkq - 0 1";
        Chess engine = new Chess(fen, new NeuralNetwork());

        List<Move> moves = engine.pseudoLegalMoves();
        Asserts.AssertMoveListEqual(moves, List.of(
                new Move("f6e7"),
                new Move("f6g7"),
                new Move("a4a5"),
                new Move("e4e5"),
                new Move("b3b4"),
                new Move("d3d4"),
                new Move("g2g3"),
                new Move("g2g4"),
                new Move("h2h3"),
                new Move("h2h4"),
                new Move("a1a2"),
                new Move("a1a3"),
                new Move("b1d2"),
                new Move("b1c3"),
                new Move("b1a3"),
                new Move("c1d2"),
                new Move("c1b2"),
                new Move("c1e3"),
                new Move("c1a3"),
                new Move("c1f4"),
                new Move("c1g5"),
                new Move("c1h6"),
                new Move("d1e2"),
                new Move("d1c2"),
                new Move("d1f3"),
                new Move("d1g4"),
                new Move("d1h5"),
                new Move("d1d2"),
                new Move("e1d2"),
                new Move("e1e2"),
                new Move("e1f2"),
                new Move("f1e2"),
                new Move("g1e2"),
                new Move("g1h3"),
                new Move("g1f3")
        ));
    }

    public static void testLegality() {
        Chess engine = new Chess();
        Asserts.AssertFalse(engine.isLegal(new Move("a2b3")));
        Asserts.AssertFalse(engine.isLegal(new Move("a2e8")));
    }

    public static void testBestMove() {
        Chess engine = new Chess();
        Asserts.AssertEqual(engine.bestMove(1).move, new Move("a2a3"));   // This is not the best move...
    }

    public static void testShowBoard() {
        Chess engine = new Chess();
        Asserts.AssertEqual(engine.showBoard(),
                ""
                        .concat("r | n | b | q | k | b | n | r\n")
                        .concat("--+---+---+---+---+---+---+---\n")
                        .concat("p | p | p | p | p | p | p | p\n")
                        .concat("--+---+---+---+---+---+---+---\n")
                        .concat("  |   |   |   |   |   |   |  \n")
                        .concat("--+---+---+---+---+---+---+---\n")
                        .concat("  |   |   |   |   |   |   |  \n")
                        .concat("--+---+---+---+---+---+---+---\n")
                        .concat("  |   |   |   |   |   |   |  \n")
                        .concat("--+---+---+---+---+---+---+---\n")
                        .concat("  |   |   |   |   |   |   |  \n")
                        .concat("--+---+---+---+---+---+---+---\n")
                        .concat("P | P | P | P | P | P | P | P\n")
                        .concat("--+---+---+---+---+---+---+---\n")
                        .concat("R | N | B | Q | K | B | N | R")
        );
    }

    public static void testConstructFromFEN() {
        Chess engine = new Chess("r1bqk2r/ppp2ppp/2n5/2b1p3/PnPpP3/3B4/1P1PNPPP/1RBQKR2 b kq c3 0 9", new NeuralNetwork());

        Asserts.AssertFalse(engine.whiteToMove);
        Asserts.AssertTrue(engine.blackCastleShort);
        Asserts.AssertTrue(engine.blackCastleLong);
        Asserts.AssertFalse(engine.whiteCastleShort);
        Asserts.AssertFalse(engine.whiteCastleLong);
        Asserts.AssertEqual(engine.enPassant, 2);
    }

}
