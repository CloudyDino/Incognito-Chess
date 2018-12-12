public class Board {

    private char[][] spaces = new char[8][8];
    private boolean currentTurn;

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

        currentTurn = true;
    }

    public boolean move(int startX, int startY, int destX, int destY) {
        // Check for validity
        if (!isValid(startX, startY, destX, destY)) {
            return false;
        }

        // Make the update
        spaces[destX][destY] = spaces[startX][startY];
        spaces[startX][startY] = 0;

        currentTurn = !currentTurn;

        return true;
    }

    public boolean isValid(int startX, int startY, int destX, int destY) {
        return true;

        // if (spaces[startX][startY] == 0) {
        //     return false;
        // }

        // if (currentTurn != Character.isUpperCase(spaces[startX][startY])) {
        //     return false;
        // }

        // if (spaces[destX][destY] != 0) {
        //     if (currentTurn == Character.isUpperCase(spaces[destX][destY])) {
        //         return false;
        //     }
        // }

        // switch(Character.toLowerCase(spaces[startX][startY])) {
        //     case 'r':
        //         // ROOK
        //         return ((destX == startX) ^ (destY == startY));

        //     case 'n':
        //         // KNIGHT
        //         boolean check = false;
        //         int[] signs = {-1, 1};
        //         for (int i = 0; i < 2; i++) {
        //             for (int j = 0; j < 2; j++) {
        //                 if (destX == (startX + (signs[i] * 1)) && destY == (startY + (signs[j] * 2))) {
        //                     check = true;
        //                 }
        //                 if (destX == (startX + (signs[i] * 2)) && destY == (startY + (signs[j] * 1))) {
        //                     check = true;
        //                 }
        //             }
        //         }
        //         return check;

        //     case 'b':
        //         // BISHOP
        //         return (Math.abs(startX - destX) == Math.abs(startY - destY));

        //     case 'q':
        //         // QUEEN
        //         return ((destX == startX) ^ (destY == startY)) || (Math.abs(startX - destX) == Math.abs(startY - destY));

        //     case 'k':
        //         // KING
        //         return (Math.abs(destX - startX) <= 1) || (Math.abs(destY - startY) <= 1);

        //     case 'p':
        //         // PAWN
        //         boolean check2 = false;
        //         if (currentTurn) {
        //             if (startX - destX == 1 && startY == destY) {
        //                 if (spaces[destX][destY] == 0) {
        //                     check2 = true;
        //                 }
        //             } else if (startX - destX == 2 && startY == destY) {
        //                 if (spaces[destX][destY] == 0) {
        //                     if (startX == 6) {
        //                         check2 = true;
        //                     }
        //                 }
        //             } else if (startX - destX == 1 && Math.abs(startY - destY) == 1) {
        //                 if (spaces[destX][destY] != 0) {
        //                     check2 = true;
        //                 }
        //             }
        //         } else {
        //             if (destX - startX == 1 && startY == destY) {
        //                 if (spaces[destX][destY] == 0) {
        //                     check2 = true;
        //                 }
        //             }  else if (destX - startX == 2 && startY == destY) {
        //                 if (spaces[destX][destY] == 0) {
        //                     if (startX == 1) {
        //                         check2 = true;
        //                     }
        //                 }
        //             } else if (destX - startX == 1 && Math.abs(startY - destY) == 1) {
        //                 if (spaces[destX][destY] != 0) {
        //                     check2 = true;
        //                 }
        //             }
        //         }
        //         return check2;
        // }

        // return false;
    }

    public char[][] getBoard() {
        return spaces;
    }

    public boolean getTurn() {
        return currentTurn;
    }

}