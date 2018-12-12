import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class asciiChess {

    public static void main(String[] args) {
        Board board = new Board();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            System.out.println();
            printBoard(board);

            try {
                String x = br.readLine();
                String y = br.readLine();
                int[] start = {Integer.parseInt(x), Integer.parseInt(y)};

                String x2 = br.readLine();
                String y2 = br.readLine();
                int[] dest = {Integer.parseInt(x2), Integer.parseInt(y2)};

                if (!board.move(start, dest)) {
                    System.out.println("Invalid move");
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void printBoard(Board board) {
        char[][] arr = board.getBoard();

        System.out.println("Turn: " + (board.getTurn()? "White":"Black"));

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

}