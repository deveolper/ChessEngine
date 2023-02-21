import java.util.*;

public class Chess {
    public int[][] board;
    public int enPassant;
    public boolean whiteToMove;
    public boolean whiteCastleShort;
    public boolean whiteCastleLong;
    public boolean blackCastleShort;
    public boolean blackCastleLong;

    public static final int WHITE_KING = 0;
    public static final int WHITE_QUEEN = 1;
    public static final int WHITE_ROOK = 2;
    public static final int WHITE_BISHOP = 3;
    public static final int WHITE_KNIGHT = 4;
    public static final int WHITE_PAWN = 5;
    public static final int EMPTY = 6;
    public static final int BLACK_PAWN = 7;
    public static final int BLACK_KNIGHT = 8;
    public static final int BLACK_BISHOP = 9;
    public static final int BLACK_ROOK = 10;
    public static final int BLACK_QUEEN = 11;
    public static final int BLACK_KING = 12;

    public static final String START_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public NeuralNetwork network;

    public int rule50move;

    /**
     * Constructor from FEN.
     *
     * @param fen the position to convert to a Chess-object.
     */
    Chess(String fen, NeuralNetwork network) {
        this.network = network;
        this.board = new int[8][8];
        String[] rows = fen.split("/");
        for (int i = 0; i < 8; i++) {
            int index = 0;
            for (int j = 0; j < 8; j++) {
                if (j >= rows[i].length()) {
                    continue;
                }
                char c = rows[i].charAt(j);
                if (Character.isSpaceChar(c)) {
                    break;
                }
                if (Character.isDigit(c)) {
                    int emptySquares = c - '0';
                    for (int k = 0; k < emptySquares; k++) {
                        this.board[i][index++] = EMPTY;
                    }
                } else {
                    switch (c) {
                        case 'K' -> this.board[i][index++] = WHITE_KING;
                        case 'Q' -> this.board[i][index++] = WHITE_QUEEN;
                        case 'R' -> this.board[i][index++] = WHITE_ROOK;
                        case 'B' -> this.board[i][index++] = WHITE_BISHOP;
                        case 'N' -> this.board[i][index++] = WHITE_KNIGHT;
                        case 'P' -> this.board[i][index++] = WHITE_PAWN;
                        case 'k' -> this.board[i][index++] = BLACK_KING;
                        case 'q' -> this.board[i][index++] = BLACK_QUEEN;
                        case 'r' -> this.board[i][index++] = BLACK_ROOK;
                        case 'b' -> this.board[i][index++] = BLACK_BISHOP;
                        case 'n' -> this.board[i][index++] = BLACK_KNIGHT;
                        case 'p' -> this.board[i][index++] = BLACK_PAWN;
                        default -> throw new RuntimeException(String.format("Unrecognised char: %s", c));
                    }
                }
            }
        }
        this.whiteToMove = fen.split(" ")[1].equals("w");
        this.whiteCastleLong = false;
        this.whiteCastleShort = false;
        this.blackCastleLong = false;
        this.blackCastleShort = false;

        String data = fen.split(" ")[2];

        if (data.matches(".*k.*")) {
            this.blackCastleShort = true;
        }
        if (data.matches(".*K.*")) {
            this.whiteCastleShort = true;
        }
        if (data.matches(".*q.*")) {
            this.blackCastleLong = true;
        }
        if (data.matches(".*Q.*")) {
            this.whiteCastleLong = true;
        }

        this.enPassant = fen.split(" ")[3].charAt(0);

        if (this.enPassant == '-') {
            this.enPassant = -1;
        } else {
            this.enPassant = this.enPassant - 'a';
        }

        this.rule50move = 0;    // Todo: extract from FEN
    }

    /**
     * Copy a Chess-object.
     *
     * @param old the object to copy.
     */
    Chess(Chess old) {
        this.board = new int[8][8];
        for (int i = 0; i < 8; i++) {
            this.board[i] = old.board[i].clone();
        }
        this.whiteToMove = old.whiteToMove;
        this.enPassant = old.enPassant;
        this.whiteCastleLong = old.whiteCastleLong;
        this.whiteCastleShort = old.whiteCastleShort;
        this.blackCastleLong = old.blackCastleLong;
        this.blackCastleShort = old.blackCastleShort;
        this.network = old.network;
        this.rule50move = old.rule50move;
    }

    /**
     * Default constructor. This constructs using the starting-position.
     */
    public Chess() {
        this(Chess.START_POSITION, new NeuralNetwork());
    }

    /**
     * Find the best move.
     *
     * @param maxDepth The maximum search-depth.
     * @return The best move with its evaluation.
     */
    public MoveEvalPair bestMove(int maxDepth) {
        if (this.rule50move >= 100) {
            return new MoveEvalPair(this.pseudoLegalMoves().get(0), 0.0);
        }
        Move indexedMove = TableBase.get(this.board, this.whiteToMove);

        if (indexedMove != null) {
            if (this.whiteToMove) {
                return new MoveEvalPair(indexedMove, Evaluator.MAX);
            } else {
                return new MoveEvalPair(indexedMove, Evaluator.MIN);
            }
        }

        if (maxDepth == 0) {
            List<Move> moves = this.legalMoves();
            Chess copy = new Chess(this);
            if (moves.size() == 0) {
                for (Move move : copy.pseudoLegalMoves()) {
                    if (!copy.whiteToMove) {
                        if (copy.board[move.dstRow][move.dstCol] == WHITE_KING) {
                            return new MoveEvalPair(new Move(), Evaluator.MIN);
                        }
                    } else {
                        if (copy.board[move.dstRow][move.dstCol] == BLACK_KING) {
                            return new MoveEvalPair(new Move(), Evaluator.MAX);
                        }
                    }
                }
                return new MoveEvalPair(new Move(), 0.0);
            }
            return new MoveEvalPair(this.legalMoves().get(0), new Evaluator().evaluateBoard(this.board, this.network));
        }

        List<Move> moves = this.legalMoves();

        if (moves.size() == 0) {
            this.whiteToMove = !this.whiteToMove;
            for (Move move : this.pseudoLegalMoves()) {
                if (!this.whiteToMove) {
                    if (this.board[move.dstRow][move.dstCol] == WHITE_KING) {
                        return new MoveEvalPair(new Move(), Evaluator.MIN);
                    }
                } else {
                    if (this.board[move.dstRow][move.dstCol] == BLACK_KING) {
                        return new MoveEvalPair(new Move(), Evaluator.MAX);
                    }
                }
            }
            return new MoveEvalPair(new Move(), 0.0);
        }
        Move currentBestMove = moves.get(0);
        double currentBestEval;

        currentBestEval = this.whiteToMove ? Chess.WHITE_KING : Chess.BLACK_KING;

        for (Move move : moves) {
            Chess newBoard = new Chess(this);
            newBoard.makeMove(move);
            
            MoveEvalPair data = newBoard.bestMove(maxDepth-1);

            if (
                    (this.whiteToMove && (data.evaluation > currentBestEval)) || (!this.whiteToMove && (data.evaluation < currentBestEval))
            ) {
                currentBestEval = data.evaluation;
                currentBestMove = move;
            }
        }

        return new MoveEvalPair(currentBestMove, currentBestEval);
    }

    /**
     * Convert to a FEN-string.
     *
     * @return the FEN-string of the current position.
     */
    public String toString() {
        StringBuilder fen = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int emptySquares = 0;
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j] == EMPTY) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }
                    fen.append(
                            switch (this.board[i][j]) {
                                case WHITE_KING -> "K";
                                case WHITE_QUEEN -> "Q";
                                case WHITE_ROOK -> "R";
                                case WHITE_BISHOP -> "B";
                                case WHITE_KNIGHT -> "N";
                                case WHITE_PAWN -> "P";
                                case BLACK_KING -> "k";
                                case BLACK_QUEEN -> "q";
                                case BLACK_ROOK -> "r";
                                case BLACK_BISHOP -> "b";
                                case BLACK_KNIGHT -> "n";
                                case BLACK_PAWN -> "p";
                                default -> throw new RuntimeException("OOPS");
                            }
                    );
                }
            }
            if (emptySquares > 0) {
                fen.append(emptySquares);
            }
            fen.append("/");
        }
        fen.deleteCharAt(fen.length() - 1); // Remove the last "/"
        if (this.whiteToMove) {
            fen.append(" w ");
        } else {
            fen.append(" b ");
        }
        boolean appended = false;
        if (this.whiteCastleShort) {
            fen.append("K");
            appended = true;
        }
        if (this.whiteCastleLong) {
            fen.append("Q");
            appended = true;
        }
        if (this.blackCastleShort) {
            fen.append("k");
            appended = true;
        }
        if (this.blackCastleLong) {
            fen.append("q");
            appended = true;
        }
        fen.append(appended ? " " : "- ");
        if (this.enPassant != -1) {
            fen.append("abcdefgh".charAt(this.enPassant)).append(this.whiteToMove ? "3" : "6");
        } else {
            fen.append("-");
        }
        fen.append(" 0 1");
        return fen.toString();
    }

    /**
     * Make a move.
     *
     * @param move the move to make.
     * @throws IllegalMoveException when the move is not legal in the current position.
     */
    public void makeMove(Move move) throws IllegalMoveException {
        if ((move.srcRow == move.dstRow) && (move.srcCol == move.dstCol)) {
            throw new IllegalMoveException("Cannot move to the same square you are already at. Current FEN: %s".formatted(this.toString()));
        }
        if ((this.board[move.srcRow][move.srcCol] == WHITE_PAWN) || (this.board[move.srcRow][move.srcCol] == BLACK_PAWN)) {
            if ((move.dstRow == 0) || (move.dstRow == 7)) {
                if (move.promotion == EMPTY) {
                    throw new IllegalMoveException("Cannot move pawn to the final rank without promotion.");
                }
            }
            if (move.srcCol != move.dstCol) {
                if (!this.isOpponentPiece(move.dstRow, move.dstCol)) {
                    throw new IllegalMoveException("Pawn can only move diagonally if there is an opponent piece to capture.");
                }
            }
        }
        if ((this.board[move.srcRow][move.srcCol] == WHITE_KING) || (this.board[move.srcRow][move.srcCol] == BLACK_KING)) {
            if (Math.abs(move.srcCol - move.dstCol) == 2) {
                // We are castling
                if (move.dstCol == 2) {
                    if (this.board[move.srcRow][move.srcCol] == WHITE_KING) {
                        this.whiteCastleLong = false;
                        this.whiteCastleShort = false;
                    } else {
                        this.blackCastleLong = false;
                        this.blackCastleShort = false;
                    }
                    this.board[move.srcRow][3] = this.board[move.srcRow][0];
                } else if (move.dstCol == 6) {
                    if (this.board[move.srcRow][move.srcCol] == WHITE_KING) {
                        this.whiteCastleLong = false;
                        this.whiteCastleShort = false;
                    } else {
                        this.blackCastleLong = false;
                        this.blackCastleShort = false;
                    }
                    this.board[move.srcRow][5] = this.board[move.srcRow][7];
                } else {
                    throw new IllegalMoveException("King can only move 2 squares in case of castling.");
                }
            }
        }
        if (
                this.board[move.srcRow][move.srcCol] == WHITE_PAWN || this.board[move.srcRow][move.srcCol] == BLACK_PAWN ||
                        this.board[move.dstRow][move.dstCol] != EMPTY
        ) {
            this.rule50move = 0;
        } else {
            this.rule50move++;
        }

        this.board[move.dstRow][move.dstCol] = move.promotion == EMPTY ? this.board[move.srcRow][move.srcCol] : move.promotion;
        this.board[move.srcRow][move.srcCol] = EMPTY;
        this.whiteToMove = !this.whiteToMove;
        if (this.board[0][4] != BLACK_KING) {
            this.blackCastleLong = false;
            this.blackCastleShort = false;
        }
        if (this.board[7][4] != WHITE_KING) {
            this.whiteCastleLong = false;
            this.whiteCastleShort = false;
        }
        if (this.board[7][0] != WHITE_ROOK) {
            this.whiteCastleLong = false;
        }
        if (this.board[7][7] != WHITE_ROOK) {
            this.whiteCastleShort = false;
        }
        if (this.board[0][0] != BLACK_ROOK) {
            this.blackCastleLong = false;
        }
        if (this.board[0][7] != BLACK_ROOK) {
            this.blackCastleShort = false;
        }
    }

    /**
     * Check if there is a black piece at a given square.
     *
     * @param row the row the square is at.
     * @param col the column the square is at.
     * @return true if a black piece is on that square.
     *         false if there is a white piece or no piece on that square.
     */
    public boolean isBlack(int row, int col) {
        return this.board[row][col] > EMPTY;
    }

    /**
     * Check if there is a white piece at a given square.
     *
     * @param row the row the square is at.
     * @param col the column the square is at.
     * @return true if a white piece is on that square.
     *         false if there is a black piece or no piece on that square.
     */
    public boolean isWhite(int row, int col) {
        return this.board[row][col] < EMPTY;
    }

    /**
     * Check if there is a piece from the opponent at a given square.
     *
     * @param row the row the square is at.
     * @param col the column the square is at.
     * @return true if an opponent piece is on that square.
     *         false if that square is empty or from your own army.
     */
    public boolean isOpponentPiece(int row, int col) {
        if (this.whiteToMove) {
            return this.isBlack(row, col);
        } else {
            return this.isWhite(row, col);
        }
    }

    /**
     * Check if a square is empty.
     *
     * @param row the row the square is at.
     * @param col the column the square is at.
     * @return true if the square is empty.
     *         false if there is a piece on that square.
     */
    public boolean isEmpty(int row, int col) {
        return this.board[row][col] == EMPTY;
    }

    /**
     * Add pawn-moves for the current player to the moves-list.
     *
     * @param row the row the pawn is at.
     * @param col the column the pawn is at.
     * @param moves the list to add the moves to.
     * @note this method assumes there is a pawn from the current player at the indicated square.
     */
    public void addPawnMoves(int row, int col, List<Move> moves) {
        if (this.whiteToMove) {
            // Check for pawn-pushes
            if ((row != 0) && this.isEmpty(row - 1, col)) {
                // Square in front of the pawn is empty
                if (row == 1) {
                    // Promotion
                    moves.add(new Move(row, col, 0, col, WHITE_KNIGHT));
                    moves.add(new Move(row, col, 0, col, WHITE_BISHOP));
                    moves.add(new Move(row, col, 0, col, WHITE_ROOK));
                    moves.add(new Move(row, col, 0, col, WHITE_QUEEN));
                } else {
                    moves.add(new Move(row, col, row - 1, col));
                    if (row == 6) {
                        // Pawn at starting square
                        if (this.isEmpty(4, col)) {
                            moves.add(new Move(row, col, 4, col));
                        }
                    }
                }
            }


            if (col >= 1) {
                // Check captures to the left
                if (row != 0) {
                    if (this.isBlack(row - 1, col - 1)) {
                        if (row == 1) {
                            moves.add(new Move(row, col, 0, col - 1, WHITE_KNIGHT));
                            moves.add(new Move(row, col, 0, col - 1, WHITE_BISHOP));
                            moves.add(new Move(row, col, 0, col - 1, WHITE_ROOK));
                            moves.add(new Move(row, col, 0, col - 1, WHITE_QUEEN));
                        } else {
                            moves.add(new Move(row, col, row - 1, col - 1));
                        }
                    }
                }
            }
            if (col <= 6) {
                // Check captures to the right
                if (row != 0) {
                    if (this.isBlack(row - 1, col + 1)) {
                        if (row == 1) {
                            moves.add(new Move(row, col, 0, col + 1, WHITE_KNIGHT));
                            moves.add(new Move(row, col, 0, col + 1, WHITE_BISHOP));
                            moves.add(new Move(row, col, 0, col + 1, WHITE_ROOK));
                            moves.add(new Move(row, col, 0, col + 1, WHITE_QUEEN));
                        } else {
                            moves.add(new Move(row, col, row - 1, col + 1));
                        }
                    }
                }
            }

            // En Passant
            if (row == 3) {
                if ((col != 7) && (this.enPassant == col + 1)) {
                    moves.add(new Move(row, col, 2, col + 1, true));
                } else if ((col != 0) && (this.enPassant == col - 1)) {
                    moves.add(new Move(row, col, 2, col - 1, true));
                }
            }
        } else {
            // Check for pawn-pushes
            if (row != 7 && this.isEmpty(row + 1, col)) {
                // Square in front of the pawn is empty
                if (row == 6) {
                    // Promotion
                    moves.add(new Move(row, col, 7, col, BLACK_KNIGHT));
                    moves.add(new Move(row, col, 7, col, BLACK_BISHOP));
                    moves.add(new Move(row, col, 7, col, BLACK_ROOK));
                    moves.add(new Move(row, col, 7, col, BLACK_QUEEN));
                } else {
                    moves.add(new Move(row, col, row + 1, col));
                    if (row == 1) {
                        // Pawn at starting square
                        if (this.isEmpty(3, col)) {
                            moves.add(new Move(row, col, 3, col));
                        }
                    }
                }
            }

            if (col >= 1) {
                // Check captures to the left
                if (row != 7 ) {
                    if (this.isWhite(row + 1, col - 1)) {
                        if (row == 6) {
                            moves.add(new Move(row, col, 7, col - 1, BLACK_KNIGHT));
                            moves.add(new Move(row, col, 7, col - 1, BLACK_BISHOP));
                            moves.add(new Move(row, col, 7, col - 1, BLACK_ROOK));
                            moves.add(new Move(row, col, 7, col - 1, BLACK_QUEEN));
                        } else {
                            moves.add(new Move(row, col, row + 1, col - 1));
                        }
                    }
                }
            }
            if (col <= 6) {
                // Check captures to the right
                if (row != 7) {
                    if (this.isWhite(row + 1, col + 1)) {
                        if (row == 6) {
                            moves.add(new Move(row, col, 7, col + 1, BLACK_KNIGHT));
                            moves.add(new Move(row, col, 7, col + 1, BLACK_BISHOP));
                            moves.add(new Move(row, col, 7, col + 1, BLACK_ROOK));
                            moves.add(new Move(row, col, 7, col + 1, BLACK_QUEEN));
                        } else {
                            moves.add(new Move(row, col, row + 1, col + 1));
                        }
                    }
                }
            }

            // En Passant
            if (row == 4) {
                if ((col != 7) && (this.enPassant == col + 1)) {
                    moves.add(new Move(row, col, 5, col + 1, true));
                } else if ((col != 0) && (this.enPassant == col - 1)) {
                    moves.add(new Move(row, col, 5, col - 1, true));
                }
            }
        }
    }

    /**
     * Add bishop-moves for the current player to the moves-list.
     *
     * @param row the row the bishop is at.
     * @param col the column the bishop is at.
     * @param moves the list to add the moves to.
     * @note this method assumes there is a bishop from the current player at the indicated square.
     */
    public void addBishopMoves(int row, int col, List<Move> moves) {
        boolean allowNw = true;
        boolean allowSw = true;
        boolean allowNe = true;
        boolean allowSe = true;

        for (int offset = 1; offset < 8; offset++) {
            if (allowNw) {
                if ((row + offset < 8) && (col + offset < 8)) {
                    if (this.isEmpty(row + offset, col + offset)) {
                        moves.add(new Move(row, col, row + offset, col + offset));
                    } else if (this.isOpponentPiece(row + offset, col + offset)) {
                        moves.add(new Move(row, col, row + offset, col + offset));
                        allowNw = false;
                    } else {
                        allowNw = false;
                    }
                }
            }
            if (allowSw) {
                if ((row + offset < 8) && (col - offset >= 0)) {
                    if (this.isEmpty(row + offset, col - offset)) {
                        moves.add(new Move(row, col, row + offset, col - offset));
                    } else if (this.isOpponentPiece(row + offset, col - offset)) {
                        moves.add(new Move(row, col, row + offset, col - offset));
                        allowSw = false;
                    } else {
                        allowSw = false;
                    }
                }
            }
            if (allowNe) {
                if ((row - offset >= 0) && (col + offset < 8)) {
                    if (this.isEmpty(row - offset, col + offset)) {
                        moves.add(new Move(row, col, row - offset, col + offset));
                    } else if (this.isOpponentPiece(row - offset, col + offset)) {
                        moves.add(new Move(row, col, row - offset, col + offset));
                        allowNe = false;
                    } else {
                        allowNe = false;
                    }
                }
            }
            if (allowSe) {
                if ((row - offset >= 0) && (col - offset >= 0)) {
                    if (this.isEmpty(row - offset, col - offset)) {
                        moves.add(new Move(row, col, row - offset, col - offset));
                    } else if (this.isOpponentPiece(row - offset, col - offset)) {
                        moves.add(new Move(row, col, row - offset, col - offset));
                        allowSe = false;
                    } else {
                        allowSe = false;
                    }
                }
            }
        }
    }

    /**
     * Add knight-moves for the current player to the moves-list.
     *
     * @param row the row the knight is at.
     * @param col the column the knight is at.
     * @param moves the list to add the moves to.
     * @note this method assumes there is a knight from the current player at the indicated square.
     */
    public void addKnightMoves(int row, int col, List<Move> moves) {
        if ((row <= 6) && (col <= 5)) {
            if (this.isOpponentPiece(row + 1, col + 2) || this.isEmpty(row + 1, col + 2)) {
                moves.add(new Move(row, col, row + 1, col + 2));
            }
        }
        if ((row <= 6) && (col >= 2)) {
            if (this.isOpponentPiece(row + 1, col - 2) || this.isEmpty(row + 1, col - 2)) {
                moves.add(new Move(row, col, row + 1, col - 2));
            }
        }
        if ((row >= 1) && (col <= 5)) {
            if (this.isOpponentPiece(row - 1, col + 2) || this.isEmpty(row - 1, col + 2)) {
                moves.add(new Move(row, col, row - 1, col + 2));
            }
        }
        if ((row >= 1) && (col >= 2)) {
            if (this.isOpponentPiece(row - 1, col - 2) || this.isEmpty(row - 1, col - 2)) {
                moves.add(new Move(row, col, row - 1, col - 2));
            }
        }
        if ((col <= 6) && (row <= 5)) {
            if (this.isOpponentPiece(row + 2, col + 1) || this.isEmpty(row + 2, col + 1)) {
                moves.add(new Move(row, col, row + 2, col + 1));
            }
        }
        if ((col <= 6) && (row >= 2)) {
            if (this.isOpponentPiece(row - 2, col + 1) || this.isEmpty(row - 2, col + 1)) {
                moves.add(new Move(row, col, row - 2, col + 1));
            }
        }
        if ((col >= 1) && (row <= 5)) {
            if (this.isOpponentPiece(row + 2, col - 1) || this.isEmpty(row + 2, col - 1)) {
                moves.add(new Move(row, col, row + 2, col - 1));
            }
        }
        if ((col >= 1) && (row >= 2)) {
            if (this.isOpponentPiece(row - 2, col - 1) || this.isEmpty(row - 2, col - 1)) {
                moves.add(new Move(row, col, row - 2, col - 1));
            }
        }
    }

    /**
     * Add king-moves for the current player to the moves-list.
     *
     * @param row the row the king is at.
     * @param col the column the king is at.
     * @param moves the list to add the moves to.
     * @note this method assumes there is a king from the current player at the indicated square.
     */
    public void addKingMoves(int row, int col, List<Move> moves) {
        if (this.whiteToMove) {
            if (this.whiteCastleLong) {
                if (this.isEmpty(row, col - 1) && this.isEmpty(row, col - 2) && this.isEmpty(row, col - 3)) {
                    moves.add(new Move(row, col, row, col - 2));
                }
            }
            if (this.whiteCastleShort) {
                if (this.isEmpty(row, col + 1) && this.isEmpty(row, col + 2)) {
                    moves.add(new Move(row, col, row, col + 2));
                }
            }
        } else {
            if (this.blackCastleLong) {
                if (this.isEmpty(row, col - 1) && this.isEmpty(row, col - 2) && this.isEmpty(row, col - 3)) {
                    moves.add(new Move(row, col, row, col - 2));
                }
            }
            if (this.blackCastleShort) {
                if (this.isEmpty(row, col + 1) && this.isEmpty(row, col + 2)) {
                    moves.add(new Move(row, col, row, col + 2));
                }
            }
        }
        for (int i = -1; i < 2; i++) {
            if (0 <= (row + i) && (row + i) < 8) {
                for (int j = -1; j < 2; j++) {
                    if (0 <= (col + j) && (col + j) < 8) {
                        if ((i != 0) || (j != 0)) {
                            if (this.isOpponentPiece(row + i, col + j) || this.isEmpty(row + i, col + j)) {
                                moves.add(new Move(row, col, row + i, col + j));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Add rook-moves for the current player to the moves-list.
     *
     * @param row the row the rook is at.
     * @param col the column the rook is at.
     * @param moves the list to add the moves to.
     * @note this method assumes there is a rook from the current player at the indicated square.
     */
    public void addRookMoves(int row, int col, List<Move> moves) {
        boolean first = true;
        boolean second = true;
        boolean third = true;
        boolean fourth = true;
        for (int i = 1; i <= 7; i++) {
            if ((row - i) >= 0 && first) {
                if (!this.isEmpty(row - i, col)) {
                    if (this.isOpponentPiece(row - i, col)) {
                        moves.add(new Move(row, col, row - i, col));
                    }
                    first = false;
                } else {
                    moves.add(new Move(row, col, row - i, col));
                }
            }
            if ((row + i) <= 7 && second) {
                if (!this.isEmpty(row + i, col)) {
                    if (this.isOpponentPiece(row + i, col)) {
                        moves.add(new Move(row, col, row + i, col));
                    }
                    second = false;
                } else {
                    moves.add(new Move(row, col, row + i, col));
                }
            }

            if ((col - i) >= 0 && third) {
                if (!this.isEmpty(row, col - i)) {
                    if (this.isOpponentPiece(row, col - i)) {
                        moves.add(new Move(row, col, row, col - i));
                    }
                    third = false;
                } else {
                    moves.add(new Move(row, col, row, col - i));
                }
            }
            if ((col + i) <= 7 && fourth) {
                if (!this.isEmpty(row, col + i)) {
                    if (this.isOpponentPiece(row, col + i)) {
                        moves.add(new Move(row, col, row, col + i));
                    }
                    fourth = false;
                } else {
                    moves.add(new Move(row, col, row, col + i));
                }
            }
        }
    }

    /**
     * Add queen-moves for the current player to the moves-list.
     *
     * @param row the row the queen is at.
     * @param col the column the queen is at.
     * @param moves the list to add the moves to.
     * @note this method assumes there is a queen from the current player at the indicated square.
     */
    public void addQueenMoves(int row, int col, List<Move> moves) {
        this.addBishopMoves(row, col, moves);
        this.addRookMoves(row, col, moves);
    }

    /**
     * Get all pseudo-legal moves for the current player.
     *
     * @return all pseudo-legal moves for the current player.
     * @note this does not check if you put yourself in check.
     */
    public List<Move> pseudoLegalMoves() {
        List<Move> moves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                switch (this.board[row][col]) {
                    case WHITE_PAWN:
                        if (!this.whiteToMove) {
                            break;
                        }
                        this.addPawnMoves(row, col, moves);
                        break;
                    case WHITE_BISHOP:
                        if (!this.whiteToMove) {
                            break;
                        }
                        this.addBishopMoves(row, col, moves);
                        break;
                    case WHITE_KNIGHT:
                        if (!this.whiteToMove) {
                            break;
                        }
                        this.addKnightMoves(row, col, moves);
                        break;
                    case WHITE_ROOK:
                        if (!this.whiteToMove) {
                            break;
                        }
                        this.addRookMoves(row, col, moves);
                        break;
                    case WHITE_QUEEN:
                        if (!this.whiteToMove) {
                            break;
                        }
                        this.addQueenMoves(row, col, moves);
                        break;
                    case WHITE_KING:
                        if (!this.whiteToMove) {
                            break;
                        }
                        this.addKingMoves(row, col, moves);
                        break;
                    case BLACK_PAWN:
                        if (this.whiteToMove) {
                            break;
                        }
                        this.addPawnMoves(row, col, moves);
                        break;
                    case BLACK_BISHOP:
                        if (this.whiteToMove) {
                            break;
                        }
                        this.addBishopMoves(row, col, moves);
                        break;
                    case BLACK_KNIGHT:
                        if (this.whiteToMove) {
                            break;
                        }
                        this.addKnightMoves(row, col, moves);
                        break;
                    case BLACK_ROOK:
                        if (this.whiteToMove) {
                            break;
                        }
                        this.addRookMoves(row, col, moves);
                        break;
                    case BLACK_QUEEN:
                        if (this.whiteToMove) {
                            break;
                        }
                        this.addQueenMoves(row, col, moves);
                        break;
                    case BLACK_KING:
                        if (this.whiteToMove) {
                            break;
                        }
                        this.addKingMoves(row, col, moves);
                        break;
                    case EMPTY:
                        break;
                    default:
                        throw new RuntimeException("OOPS");
                }
            }
        }
        return moves;
    }

    /**
     * Get all the legal moves for the current player.
     *
     * @return all the legal moves for the current player.
     *
     * @implNote generates all pseudo-legal moves, then removes all illegal moves from it.
     */
    public List<Move> legalMoves() {
        List<Move> moves = this.pseudoLegalMoves();

        this.removeIllegalMoves(moves);

        return moves;
    }

    /**
     * Remove all illegal moves from the moves-list.
     *
     * @param moves the moves to filter.
     * @implNote uses isLegal for every move.
     */
    public void removeIllegalMoves(List<Move> moves) {
        moves.removeIf(move -> !this.isLegal(move));
    }

    /**
     * Check if the move is legal.
     *
     * @param move the move to check.
     * @return true if the move is legal.
     *         false if the move is not legal.
     * @bug this method returns false too often.
     */
    public boolean isLegal(Move move) {
        Chess board = new Chess(this);

        try {
            board.makeMove(move);
        } catch (IllegalMoveException e) {
            return false;
        }

        List<Move> pseudoLegalOpponentMoves = board.pseudoLegalMoves();

        for (Move pseudoLegalMove : pseudoLegalOpponentMoves) {
            Chess boardCopy = new Chess(board);
            try {
                boardCopy.makeMove(pseudoLegalMove);
            } catch (IllegalMoveException e) {
                continue;
            }
            if (!boardCopy.kingLives()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if both kings are still alive.
     *
     * @return true if both kings are alive.
     *         false if one or more kings are dead.
     */
    public boolean kingLives() {
        boolean white = false;
        boolean black = false;

        for (int[] row : this.board) {
            for (int piece : row) {
                switch (piece) {
                    case BLACK_KING -> black = true;
                    case WHITE_KING -> white = true;
                    default -> {}
                }
            }
        }

        return black && white;
    }

    /**
     * Get a string-representation of the board that can be passed into System.out.print to get the board cleanly on
     * the console.
     *
     * @return the string-representation of the board.
     */
    public String showBoard() {
        boolean first = true;
        StringBuilder s = new StringBuilder();
        for (int[] row : this.board) {
            if (!first) {
                s.append("\n--+---+---+---+---+---+---+---\n");
            }
            first = true;
            for (int square : row) {
                if (first) {
                    first = false;
                } else {
                    s.append(" | ");
                }

                s.append(
                        switch (square) {
                            case WHITE_PAWN -> "P";
                            case WHITE_BISHOP -> "B";
                            case WHITE_KNIGHT -> "N";
                            case WHITE_ROOK -> "R";
                            case WHITE_QUEEN -> "Q";
                            case WHITE_KING -> "K";
                            case BLACK_PAWN -> "p";
                            case BLACK_BISHOP -> "b";
                            case BLACK_KNIGHT -> "n";
                            case BLACK_ROOK -> "r";
                            case BLACK_QUEEN -> "q";
                            case BLACK_KING -> "k";
                            case EMPTY -> " ";
                            default -> throw new RuntimeException("OOPS");
                        }
                );
            }
            first = false;
        }
        return s.toString();
    }

    public boolean inProgress() {
        return this.kingLives() && this.rule50move < 100;
    }
}
