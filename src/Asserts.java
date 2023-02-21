import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Asserts {
    public static void fail() {
        throw new TestCaseFailure("ASSERTION FAILED!");
    }

    public static boolean MoveListEqual(List<Move> first, List<Move> second) {
        List<String> firstCopy = new ArrayList<>(first.stream().map(Move::toString).toList());
        List<String> secondCopy = new ArrayList<>(second.stream().map(Move::toString).toList());

        firstCopy.removeAll(new ArrayList<>(second.stream().map(Move::toString).toList()));
        secondCopy.removeAll(new ArrayList<>(first.stream().map(Move::toString).toList()));

        return firstCopy.isEmpty() && secondCopy.isEmpty();
    }

    public static void AssertTrue(boolean condition, String message) {
        if (!condition) {
            System.out.println(message);
            fail();
        }
        System.out.print(".");
    }
    public static void AssertTrue(boolean condition) {
        if (!condition) {
            fail();
        }
        System.out.print(".");
    }

    public static void AssertFalse(boolean condition, String message) {
        if (condition) {
            System.out.println(message);
            fail();
        }
        System.out.print(".");
    }

    public static void AssertFalse(boolean condition) {
        if (condition) {
            fail();
        }
        System.out.print(".");
    }

    public static void AssertEqual(Move first, Move second) {
        AssertTrue(first.toString().equals(second.toString()), "Failed asserting that %s == %s".formatted(first, second));
    }

    public static void AssertEqual(MoveEvalPair first, MoveEvalPair second) {
        AssertTrue(first.toString().equals(second.toString()), "Failed asserting that %s == %s".formatted(first, second));
    }

    public static void AssertEqual(Object first, Object second) {
        AssertTrue(first.equals(second), "Failed asserting that %s == %s".formatted(first, second));
    }

    public static void AssertMoveListEqual(List<Move> first, List<Move> second) {
        AssertTrue(MoveListEqual(first, second), "Lists differ: %s".formatted(MoveListDiff(first, second)));
    }

    private static List<String> MoveListDiff(List<Move> first, List<Move> second) {
        List<String> firstCopy = new ArrayList<>(first.stream().map(Move::toString).toList());
        List<String> secondCopy = new ArrayList<>(second.stream().map(Move::toString).toList());

        firstCopy.removeAll(new ArrayList<>(second.stream().map(Move::toString).toList()));
        secondCopy.removeAll(new ArrayList<>(first.stream().map(Move::toString).toList()));

        firstCopy.addAll(secondCopy);
        return firstCopy;
    }
}
