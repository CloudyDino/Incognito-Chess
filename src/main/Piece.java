package main;

public enum Piece {
    PAWN('P', 'p'),
    KNIGHT('N', 'n'),
    BISHOP('B', 'b'),
    ROOK('R', 'r'),
    QUEEN('Q', 'q'),
    KING('K', 'k');

    private char white;
    private char black;

    Piece(char white, char black) {
        this.white = white;
        this.black = black;
    }

    public static boolean isWhite(char piece) {
        if (!Character.isLetter(piece)) {
            throw new IllegalArgumentException(String.format("Can't determine if %c is white or not", piece));
        }
        return Character.isUpperCase(piece);
    }

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
        throw new IllegalArgumentException(String.format("Can't convert %c into a Piece", piece));
    }

    public char toChar(boolean isWhite) {
        return isWhite ? white : black;
    }
}
