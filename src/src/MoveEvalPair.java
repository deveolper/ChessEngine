import java.util.Locale;

public class MoveEvalPair {
    public Move move;
    public double evaluation;

    public MoveEvalPair(Move move, double evaluation) {
        this.move = move;
        this.evaluation = evaluation;
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s (%.1f)", this.move, this.evaluation);
    }
}
