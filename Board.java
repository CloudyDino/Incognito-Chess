import java.util.HashSet;
import java.util.Set;

public class Board {

    public final int SIZE = 8;

    private char[][] spaces = new char[SIZE][SIZE];
    private boolean whiteTurn;
    private boolean castleWK, castleWQ, castleBK, castleBQ;
    private int enPassant;
    private Set<Integer> whiteAttack, blackAttack;
    private boolean whiteInCheck, blackInCheck;
    int movesSincePawnOrCapture;

    public Board() {

        char[] setup = {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'};
        for (int i = 0; i < 8; i++) {
            spaces[i][7] = setup[i];
            spaces[i][6] = 'p';
            spaces[i][1] = 'P';
            spaces[i][0] = Character.toUpperCase(setup[i]);
        }

        // Results in:
        //
        // 7) r n b q k b n r
        // 6) p p p p p p p p
        // 5) - - - - - - - -
        // 4) - - - - - - - -
        // 3) - - - - - - - -
        // 2) - - - - - - - -
        // 1) P P P P P P P P
        // 0) R N B Q K B N R
        //    0 1 2 3 4 5 6 7
        //
        // Where spaces[4][0] == 'K'

        whiteTurn = true;
        castleWK = true;
        castleWQ = true;
        castleBK = true;
        castleBQ = true;
        enPassant = -1;
        updateAttack();
        movesSincePawnOrCapture = 0;
    }

    public char[][] getBoard() {
        return spaces;
    }

    public boolean getTurn() {
        return whiteTurn;
    }

    public void setTurn(boolean turn) {
        whiteTurn = turn;
    }

    public int squareToInteger(int x, int y) {
        if (x < SIZE && y < SIZE) {
            return x * SIZE + y;
        }
        return -1;
    }

    public int squareToInteger(int[] xy) {
        if (xy.length >= 2 && xy[0] < SIZE && xy[1] < SIZE) {
            return xy[0]*SIZE + xy[1];
        }
        return -1;
    }

    public int[] integerToSquare(int i) {
        int[] square = {i/SIZE, i%SIZE};
        return square;
    }

    public boolean onBoard(int x, int y) {
        return 0 <= x && x < SIZE &&
               0 <= y && y < SIZE;
    }

    public GameStatus getGameStatus() {

        // If there exist possible moves for the current color then the game is
        // either drawn by the 50 move rule or is still in progress
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (spaces[x][y] != 0 && whiteTurn == Character.isUpperCase(spaces[x][y]) && getLegalMoves(x, y).size() > 0) {
                    if (movesSincePawnOrCapture == 100) {
                        return GameStatus.DRAW;
                    }
                    return GameStatus.IN_PROGRESS;
                }
            }
        }

        // No moves for current color,
        if (whiteTurn && whiteInCheck) {
            return GameStatus.BLACK_WON;
        } else if (!whiteTurn && blackInCheck) {
            return GameStatus.WHITE_WON;
        }
        return GameStatus.STALEMATE;
    }

    public void updateAttack() {
        int whiteKing = -1;
        int blackKing = -1;
        whiteAttack = new HashSet<>();
        blackAttack = new HashSet<>();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                switch (spaces[x][y]){
                    case 'K':
                        whiteKing = squareToInteger(x, y);
                        break;
                    case 'k':
                        blackKing = squareToInteger(x, y);
                        break;
                }

                Set<Integer> moves = getPossibleMoves(x, y);
                if (moves != null) {
                    if (Character.isUpperCase(spaces[x][y])) {
                        whiteAttack.addAll(moves);
                    } else {
                        blackAttack.addAll(moves);
                    }
                }
            }
        }

        whiteInCheck = blackAttack.contains(whiteKing);
        blackInCheck = whiteAttack.contains(blackKing);
    }

    public boolean doesPromote(int startX, int startY, int destX, int destY) {
        return (Character.toLowerCase(spaces[destX][destY]) == 'p') && (destY == 0 || destY == SIZE - 1);
    }

    public boolean move(int startX, int startY, int destX, int destY) {
        return move(startX, startY, destX, destY, 'Q');
    }

    public boolean move(int startX, int startY, int destX, int destY, char promoteTo) {
        // Check for validity
        if (!isValidMove(startX, startY, destX, destY)) {
            return false;
        }

        // Change castling if king moves or rook moves from original position
        if (startY == 0) {
            if (spaces[startX][startY] == 'K') {
                castleWK = false;
                castleWQ = false;
            } else if (startX == 0) {
                castleWQ = false;
            } else if (startX == SIZE) {
                castleWK = false;
            }
        } else if (startY == SIZE) {
            if (spaces[startX][startY] == 'k') {
                castleBK = false;
                castleBQ = false;
            } else if (startX == 0) {
                castleBQ = false;
            } else if (startX == SIZE) {
                castleBK = false;
            }
        }

        // Make the update
        enPassant = -1;
        movesSincePawnOrCapture++;
        if (spaces[destX][destY] != 0) {
            movesSincePawnOrCapture = 0;
        }
        spaces[destX][destY] = spaces[startX][startY];
        spaces[startX][startY] = 0;

        if (Character.toLowerCase(spaces[destX][destY]) == 'k' && Math.abs(startX - destX) == 2) {
            // castle
            int rookX = (startX > destX ? 0 : SIZE - 1);
            spaces[(startX + destX)/2][startY] = spaces[rookX][startY];
            spaces[rookX][startY] = 0;

        } else if (Character.toLowerCase(spaces[destX][destY]) == 'p') {
            movesSincePawnOrCapture = 0;
            if (Math.abs(startY - destY) == 2) {
                // moved two spaces
                enPassant = squareToInteger(destX, destY);
            } else if (destX != startX && spaces[destX][destY] == 0) {
                // en passant
                spaces[destX][startY] = 0;
            } else if (destY == 0 || destY == SIZE - 1) {
                // pawn promotion
                spaces[destX][destY] = (whiteTurn ? Character.toUpperCase(promoteTo) : Character.toLowerCase(promoteTo));
            }
        }

        whiteTurn = !whiteTurn;
        updateAttack();

        return true;
    }

    public boolean isValidMove(int startX, int startY, int destX, int destY) {

        if (spaces[startX][startY] == 0 ||
                whiteTurn != Character.isUpperCase(spaces[startX][startY])) {
            return false;
        }

        Set<Integer> legalMoves = getLegalMoves(startX, startY);
        if (legalMoves.contains(squareToInteger(destX, destY))) {
            return true;
        }

        return false;
    }

    /**
     * @return Set<Integer> a nonnull set of integers corresponding to the
     *         squares that the starting square can legally go to
     */
    private Set<Integer> getLegalMoves(int startX, int startY) {
        Set<Integer> possibleMoves = getPossibleMoves(startX, startY);
        Set<Integer> legalMoves = new HashSet<>();

        for (int pm : possibleMoves) {
            int[] pmSquare = integerToSquare(pm);
            int destX = pmSquare[0];
            int destY = pmSquare[1];

            char[][] temp = new char[SIZE][];
            for(int x = 0; x < SIZE; x++) {
                temp[x] = spaces[x].clone();
            }

            temp[destX][destY] = temp[startX][startY];
            temp[startX][startY] = 0;
            if (Character.toLowerCase(temp[destX][destY]) == 'p' &&
                    destX != startX && temp[destX][destY] == 0) {
                // en passant
                temp[destX][startY] = 0;
            }

            int king = -1;
            Set<Integer> attacked = new HashSet<>();
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    if (temp[x][y] == (whiteTurn ? 'K' : 'k')) {
                        king = squareToInteger(x, y);
                    }

                    Set<Integer> moves = getPossibleMoves(x, y);
                    if (moves != null &&
                            whiteTurn != Character.isUpperCase(temp[x][y])) {
                        attacked.addAll(moves);
                    }
                }
            }
            if (!attacked.contains(king)) {
                legalMoves.add(pm);
            }
        }
        return legalMoves;
    }


    private Set<Integer> getPossibleMoves(int x, int y) {
        switch (Character.toLowerCase(spaces[x][y])) {
            case 'p':
                return getPossiblePawnMoves(x,y);
            case 'r':
                return getPossibleRookMoves(x,y);
            case 'n':
                return getPossibleKnightMoves(x,y);
            case 'b':
                return getPossibleBishopMoves(x,y);
            case 'q':
                return getPossibleQueenMoves(x,y);
            case 'k':
                return getPossibleKingMoves(x,y);
        }
        return null;
    }

    private Set<Integer> getPossiblePawnMoves(int x, int y) {
        Set<Integer> possibleMoves = new HashSet<>();
        int dy = (whiteTurn ? 1 : -1);
        int startY = (whiteTurn ? 1 : 6);
        int currY = y + dy;
        if (onBoard(x, currY) && spaces[x][currY] == 0) {
            possibleMoves.add(squareToInteger(x, currY));
        }
        if (possibleMoves.size() > 0 && y == startY && spaces[x][y + 2*dy] == 0) {
            possibleMoves.add(squareToInteger(x, currY + dy));
        }

        for (int dx = -1; dx <= 1; dx += 2) {
            int currX = x + dx;
            if (onBoard(currX, currY) &&
                    ((spaces[currX][currY] != 0 && whiteTurn != Character.isUpperCase(spaces[currX][currY])) ||
                    (spaces[currX][currY] == 0 && squareToInteger(currX, y) == enPassant))) {
                possibleMoves.add(squareToInteger(currX, currY));
            }
        }
        return possibleMoves;
    }

    private Set<Integer> getPossibleKnightMoves(int x, int y) {
        Set<Integer> possibleMoves = new HashSet<>();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx != dy && dx != 0 && dy != 0 && dx + dy != 0) {
                    int currX = x + dx;
                    int currY = y + dy;
                    if (onBoard(currX, currY) && (spaces[currX][currY] == 0 ||
                            whiteTurn != Character.isUpperCase(spaces[currX][currY]))) {
                        possibleMoves.add(squareToInteger(currX, currY));
                    }
                }

            }
}
        return possibleMoves;
    }

    private Set<Integer> getPossibleRookMoves(int x, int y) {
        return getPossibleBishopRookQueenMoves(x, y, false, true);
    }

    private Set<Integer> getPossibleBishopMoves(int x, int y) {
        return getPossibleBishopRookQueenMoves(x, y, true, false);
    }

    private Set<Integer> getPossibleQueenMoves(int x, int y) {
        return getPossibleBishopRookQueenMoves(x, y, true, true);
    }

    private Set<Integer> getPossibleBishopRookQueenMoves(int x, int y, boolean isBishop, boolean isRook) {
        Set<Integer> possibleMoves = new HashSet<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if ((dx != 0 || dy != 0) &&
                        ((isBishop && Math.abs(dx) == Math.abs(dy)) ||
                        (isRook && Math.abs(dx) != Math.abs(dy)))) {
                    int currX = x;
                    int currY = y;
                    boolean stop = false;
                    while (!stop) {
                        currX += dx;
                        currY += dy;
                        if (onBoard(currX, currY)) {
                            if (spaces[currX][currY] == 0) {
                                possibleMoves.add(squareToInteger(currX, currY));
                            } else {
                                stop = true;
                                if (whiteTurn != Character.isUpperCase(spaces[currX][currY])) {
                                    possibleMoves.add(squareToInteger(currX, currY));
                                }
                            }
                        } else {
                            stop = true;
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    private Set<Integer> getPossibleKingMoves(int x, int y) {
        Set<Integer> possibleMoves = new HashSet<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int currX = x + dx;
                int currY = y + dy;
                if ((dx != 0 || dy != 0) &&
                        onBoard(currX, currY) &&
                        (spaces[currX][currY] == 0 ||
                        whiteTurn != Character.isUpperCase(spaces[currX][currY]))) {
                    possibleMoves.add(squareToInteger(currX, currY));
                }
            }
        }

        Set<Integer> attacked = (whiteTurn ? blackAttack : whiteAttack);

        boolean kingInCheck = (whiteTurn ? whiteInCheck : blackInCheck);
        boolean castleK = !kingInCheck && (whiteTurn ? castleWK : castleBK);
        boolean castleQ = !kingInCheck && (whiteTurn ? castleWQ : castleBQ);

        for (int currX = x + 1; castleK && currX < SIZE-1; currX++) {
            if (spaces[currX][y] != 0 ||
                    (currX - x <= 2 && attacked.contains(squareToInteger(currX, y)))) {
                castleK = false;
            }
        }

        for (int currX = x - 1; castleQ && currX > 0; currX--) {
            if (spaces[currX][y] != 0 ||
                    (x - currX <= 2 && attacked.contains(squareToInteger(currX, y)))) {
                castleQ = false;
            }
        }

        if (castleK) {
            possibleMoves.add(squareToInteger(x+2, y));
        }
        if (castleQ) {
            possibleMoves.add(squareToInteger(x-2, y));
        }

        return possibleMoves;
    }

}