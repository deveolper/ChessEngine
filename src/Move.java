public class Move {
    public int srcRow;
    public int srcCol;
    public int dstRow;
    public int dstCol;

    public int promotion;
    public boolean isEnPassant;

    public Move(int srcRow, int srcCol, int dstRow, int dstCol, boolean isEnPassant) {
        this(srcRow, srcCol, dstRow, dstCol);
        this.isEnPassant = isEnPassant;
    }
    public Move(int srcRow, int srcCol, int dstRow, int dstCol, int promotion) {
        this(srcRow, srcCol, dstRow, dstCol);
        this.promotion = promotion;
    }

    public Move(int srcRow, int srcCol, int dstRow, int dstCol) {
        this.srcRow = srcRow;
        this.srcCol = srcCol;
        this.dstRow = dstRow;
        this.dstCol = dstCol;
        this.promotion = Chess.EMPTY;
    }

    public Move(String uci) {
        this.srcCol = uci.charAt(0) - 'a';
        this.srcRow = 8 - Integer.parseInt(uci.substring(1, 2));
        this.dstCol = uci.charAt(2) - 'a';
        this.dstRow = 8 - Integer.parseInt(uci.substring(3, 4));
        switch (uci.substring(4)) {
            case "Q" -> this.promotion = Chess.WHITE_QUEEN;
            case "q" -> this.promotion = Chess.BLACK_QUEEN;
            case "R" -> this.promotion = Chess.WHITE_ROOK;
            case "r" -> this.promotion = Chess.BLACK_ROOK;
            case "B" -> this.promotion = Chess.WHITE_BISHOP;
            case "b" -> this.promotion = Chess.BLACK_BISHOP;
            case "N" -> this.promotion = Chess.WHITE_KNIGHT;
            case "n" -> this.promotion = Chess.BLACK_KNIGHT;
            case "" -> this.promotion = Chess.EMPTY;
            default -> throw new RuntimeException("OOPS");
        }
    }

    public Move() {
        this(9, 9, 9, 9);
    }

    public boolean equals(Move other) {
        return this.toString().equals(other.toString());
    }

    public String convertColumn(int col) {
        return switch (col) {
            case 0 -> "a";
            case 1 -> "b";
            case 2 -> "c";
            case 3 -> "d";
            case 4 -> "e";
            case 5 -> "f";
            case 6 -> "g";
            case 7 -> "h";
            default -> throw new RuntimeException("OOPS");
        };
    }

    public String convertPromotion(int promotion) {
        return switch (Math.abs(promotion)) {
            case Chess.EMPTY -> "";
            case Chess.BLACK_KNIGHT, Chess.WHITE_KNIGHT -> "=N";
            case Chess.BLACK_BISHOP, Chess.WHITE_BISHOP -> "=B";
            case Chess.BLACK_ROOK, Chess.WHITE_ROOK -> "=R";
            case Chess.BLACK_QUEEN, Chess.WHITE_QUEEN -> "=Q";
            default -> throw new RuntimeException("OOPS");
        };
    }



    public String toString() {
        if (this.dstRow == 9) {
            return "DEFAULT MOVE";
        }
        return String.format("%s%d%s%d%s", this.convertColumn(this.srcCol), 8-this.srcRow, this.convertColumn(this.dstCol), 8-this.dstRow, this.convertPromotion(this.promotion));
    }

    public boolean isDefault() {
        return this.toString().equals("DEFAULT MOVE");
    }
}
