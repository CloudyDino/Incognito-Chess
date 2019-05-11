package main;

public class AsciiPrinter extends BoardPrinter {

    private char NULLCHAR = '\0';

    AsciiPrinter(Board board) {
        super(board);
    }

     public void printBoard() {
        char[][] arr = board.getBoard();

        System.out.println("Turn: " + (board.getTurn() ? "White" : "Black"));

        for (int y = arr[0].length - 1; y >= 0; y--) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < arr.length; x++) {
                sb.append(getPrintableChar(arr[x][y]));
                sb.append(" ");

            }
            System.out.println(sb);
        }
    }

    private char getPrintableChar(char c) {
        if (c == NULLCHAR) {
            return ' ';
        }
        return c;
    }
}
