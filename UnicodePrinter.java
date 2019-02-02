public class UnicodePrinter extends BoardPrinter {

    UnicodePrinter(Board board) {
        super(board);
    }

    public void printBoard() {
        char[][] arr = board.getBoard();

        System.out.println("Turn: " + (board.getTurn() ? "White" : "Black"));

        for (int y = arr[0].length - 1; y >= 0; y--) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < arr.length; x++) {
                sb.append(getUnicode(arr[x][y]));
                sb.append(" ");

            }
            System.out.println(sb);
        }
    }

    private String getUnicode(char c) {
        switch (c) {
            case 'p': return "♟";
            case 'n': return "♞";
            case 'b': return "♝";
            case 'r': return "♜";
            case 'q': return "♛";
            case 'k': return "♚";
            case 'P': return "♙";
            case 'N': return "♘";
            case 'B': return "♗";
            case 'R': return "♖";
            case 'Q': return "♕";
            case 'K': return "♔";
        }
        return " ";
    }

}
