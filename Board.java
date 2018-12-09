public class Board {

    private char[][] spaces = new char[8][8];
    // Array of characters for pieces on board

    public Board() {
        // Pawns
        for (int i = 0; i < 8; i++) {
            spaces[1][i] = 'p';
            spaces[6][i] = 'P';
        }

        // Other pieces
        char[] setup = {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'};
        for (int i = 0; i < 8; i++) {
            spaces[0][i] = setup[i];
            spaces[7][i] = Character.toUpperCase(setup[i]);
        }

        // Results in:
        //
        //    0 1 2 3 4 5 6 7
        // 0) r n b q k b n r
        // 1) p p p p p p p p
        // 2) - - - - - - - -
        // 3) - - - - - - - -
        // 4) - - - - - - - -
        // 5) - - - - - - - -
        // 6) P P P P P P P P
        // 7) R N B Q K B N R
        //
        // Where spaces[1][4] == 'p'
    }

    public boolean move(int[] start, int[] dest) {
        // Check for validity
        if (!isValid(start, dest)) {
            return false;
        }

        // Make the update
        spaces[dest[0]][dest[1]] = spaces[start[0]][start[1]];
        spaces[start[0]][start[1]] = null;
        return true;
    }

    public boolean isValid(int[] start, int[] dest) {
        // TODO: implement a checker to ensure valid move
    }

}