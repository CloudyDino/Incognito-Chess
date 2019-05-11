package main;

public enum Piece {
    NOT_A_PIECE, PAWN,KNIGHT, BISHOP, ROOK, QUEEN, KING;

    public static Piece fromChar(char piece) {
        switch (Character.toLowerCase(piece)) {
            case 'p':
                return PAWN;
            case 'n':
                return KNIGHT;
            case 'b':
                return BISHOP;
            case 'r':
                return ROOK;
            case 'q':
                return QUEEN;
            case 'k':
                return KING;
        }
        return NOT_A_PIECE;
    }
}
