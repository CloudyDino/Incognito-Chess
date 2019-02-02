import java.util.Scanner;

public class TerminalChess {
    public static void main(String[] args) {
        Board board = new Board();
        Scanner in = new Scanner(System.in);

        BoardPrinter bp;

        if (args.length > 0 && args[0].equals("-u")) {
            bp = new UnicodePrinter(board);
        } else {
            bp = new AsciiPrinter(board);
        }

        while (board.getGameStatus() == GameStatus.IN_PROGRESS) {

            System.out.println();
            bp.printBoard();

            int x1 = in.nextInt();
            int y1 = in.nextInt();

            int x2 = in.nextInt();
            int y2 = in.nextInt();

            if (!board.move(x1, y1, x2, y2)) {
                System.out.println("Invalid move");
            }
        }
        in.close();
    }
}
