public class Board {

    private char[][] spaces = new char[8][8];
    private boolean currentTurn;

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

        currentTurn = true;
    }

    public boolean move(int[] start, int[] dest) {
        // Check for validity
        if (!isValid(start, dest)) {
            return false;
        }

        // Make the update
        spaces[dest[0]][dest[1]] = spaces[start[0]][start[1]];
        spaces[start[0]][start[1]] = 0;

        currentTurn = !currentTurn;

        return true;
    }

    public boolean isValid(int[] start, int[] dest) {
        if (spaces[start[0]][start[1]] == 0) {
            return false;
        }

        if (currentTurn != Character.isUpperCase(spaces[start[0]][start[1]])) {
            return false;
        }

        if (spaces[dest[0]][dest[1]] != 0) {
            if (currentTurn == Character.isUpperCase(spaces[dest[0]][dest[1]])) {
                return false;
            }
        }

        switch(Character.toLowerCase(spaces[start[0]][start[1]])) {
            case 'r':
                // ROOK
                return ((dest[0] == start[0]) ^ (dest[1] == start[1]));

            case 'n':
                // KNIGHT
                boolean check = false;
                int[] signs = {-1, 1};
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        if (dest[0] == (start[0] + (signs[i] * 1)) && dest[1] == (start[1] + (signs[j] * 2))) {
                            check = true;
                        }
                        if (dest[0] == (start[0] + (signs[i] * 2)) && dest[1] == (start[1] + (signs[j] * 1))) {
                            check = true;
                        }
                    }
                }
                return check;

            case 'b':
                // BISHOP
                return (Math.abs(start[0] - dest[0]) == Math.abs(start[1] - dest[1]));

            case 'q':
                // QUEEN
                return ((dest[0] == start[0]) ^ (dest[1] == start[1])) || (Math.abs(start[0] - dest[0]) == Math.abs(start[1] - dest[1]));

            case 'k':
                // KING
                return (Math.abs(dest[0] - start[0]) <= 1) || (Math.abs(dest[1] - start[1]) <= 1);

            case 'p':
                // PAWN
                boolean check2 = false;
                if (currentTurn) {
                    if (start[0] - dest[0] == 1 && start[1] == dest[1]) {
                        if (spaces[dest[0]][dest[1]] == 0) {
                            check2 = true;
                        }
                    } else if (start[0] - dest[0] == 2 && start[1] == dest[1]) {
                        if (spaces[dest[0]][dest[1]] == 0) {
                            if (start[0] == 6) {
                                check2 = true;
                            }
                        }
                    } else if (start[0] - dest[0] == 1 && Math.abs(start[1] - dest[1]) == 1) {
                        if (spaces[dest[0]][dest[1]] != 0) {
                            check2 = true;
                        }
                    }
                } else {
                    if (dest[0] - start[0] == 1 && start[1] == dest[1]) {
                        if (spaces[dest[0]][dest[1]] == 0) {
                            check2 = true;
                        }
                    }  else if (dest[0] - start[0] == 2 && start[1] == dest[1]) {
                        if (spaces[dest[0]][dest[1]] == 0) {
                            if (start[0] == 1) {
                                check2 = true;
                            }
                        }
                    } else if (dest[0] - start[0] == 1 && Math.abs(start[1] - dest[1]) == 1) {
                        if (spaces[dest[0]][dest[1]] != 0) {
                            check2 = true;
                        }
                    }
                }
                return check2;
        }

        return false;
    }

    public char[][] getBoard() {
        return spaces;
    }

    public boolean getTurn() {
        return currentTurn;
    }

}