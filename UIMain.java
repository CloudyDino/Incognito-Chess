import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

class UiMain extends JFrame {

    public static Board b;
    public static JButton[][] buttonArr;
    public static ArrayList<JButton> presses = new ArrayList<>();
    static Server server;
    static Client client;
    public static long currentval;
    public static boolean startColor;
    public static JFrame f;

    // Constructor:
    public UiMain() {
        setTitle("Incognito Chess");
        setSize(960, 960);
        setLocation(200, 50);

        // Window Listeners
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                client.disconnect();
                server.disconnect();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        b = new Board();

        currentval = (new Random()).nextLong();

        client = new Client(args[0], 5000);

        Thread clientThread = new Thread(client);
        clientThread.start();

        server = new Server(5000);

        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public static void initHandshake() {
        client.sendLong(currentval);
    }

    public static boolean handshake(long l) {
        if (currentval == l) {
            initHandshake();
            return false;
        } else if (currentval > l) {
            startColor = true;
        } else {
            startColor = false;
        }

        return true;
    }

    public static void startgame() {
        System.out.println("You are: " + (startColor ? "White" : "Black"));

        f = new UiMain();
        f.setResizable(false);

        Container contentPane = f.getContentPane();
        JPanel chessPanel = new JPanel(new GridLayout(8, 8));

        buttonArr = new JButton[8][8];

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                //Icon warnIcon = new ImageIcon("pawn.png");
                JButton square = new JButton();
                char c;
                if (startColor) {
                    c = b.getBoard()[j][i];
                } else {
                    c = b.getBoard()[7 - j][7 - i];
                }
                ImageIcon icon = new ImageIcon(getImageFile(c));
                Image piece = icon.getImage();
                Image newimg = piece.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH);
                icon = new ImageIcon(newimg);
                square.setIcon(icon);

                square.addActionListener(new ClickListener());
                if (startColor) {
                    square.setActionCommand(j + " " + i);
                } else {
                    square.setActionCommand((7 - j) + " " + (7 - i));
                }
                square.setFocusPainted(false);
                square.setRolloverEnabled(true);
                square.setBorderPainted(false);

                if (startColor) {
                    buttonArr[j][i] = square;
                } else {
                    buttonArr[7 - j][7 - i] = square;
                }

                if (i % 2 == j % 2) {
                    square.setBackground(new Color(75, 115, 153));
                } else {
                    square.setBackground(new Color(234, 233, 210));
                }
                chessPanel.add(square);
            }
        }

        contentPane.add(chessPanel);

        f.setVisible(true);
    }

    public static void takeTurn() {
        presses.add(ClickListener.lastPressed);

        if (presses.size() >= 2) {
            String start = presses.get(0).getActionCommand();
            String end = presses.get(1).getActionCommand();

            int startX = (start.charAt(0) - '0');
            int startY = (start.charAt(2) - '0');
            int endX = (end.charAt(0) - '0');
            int endY = (end.charAt(2) - '0');
            char promotion = 'Q';

            if (b.doesPromote(startX, startY, endX, endY)) {
                promotion = getPromotionPiece();
            }

            int[] sending = {startX, startY, endX, endY};

            boolean check = b.move(startX, startY, endX, endY, promotion);

            if (check) {
                refreshBoard();

                presses.clear();
                client.sendMove(sending, promotion);
            } else {
                presses.clear();
            }
        }
    }

    public static void recieveMove(int[] move, char promotion) {
        b.move(move[0], move[1], move[2], move[3], promotion);

        refreshBoard();
    }

    public static void refreshBoard() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {

                char c;
                if (startColor) {
                    c = b.getBoard()[i][j];
                } else {
                    c = b.getBoard()[7 - i][7 - j];
                }
                ImageIcon icon = new ImageIcon(getImageFile(c));
                Image piece = icon.getImage();
                Image newimg = piece.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH);
                icon = new ImageIcon(newimg);
                if (startColor) {
                    buttonArr[i][j].setIcon(icon);
                } else {
                    buttonArr[7 - i][7 - j].setIcon(icon);
                }

            }
        }
    }

    public static char getPromotionPiece() {
        // TODO: make popup to ask if player wants Knight, Bishop, Rook, or Queen

        Object[] options = {"Queen", "Rook", "Bishop", "Knight"};

        int chosen = -1;

        while (chosen == -1) {
            chosen = JOptionPane.showOptionDialog(f,
                "Please choose a piece to promote to.",
                "Pawn Promotion",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        }

        if (chosen == 0) {
            return 'Q';
        } else if (chosen == 1) {
            return 'R';
        } else if (chosen == 2) {
            return 'B';
        } else if (chosen == 3) {
            return 'k';
        }

        return 'Q';
    }

    private static String getImageFile(char c) {
        StringBuilder s = new StringBuilder("pieces/.png");
        s.insert(7, (Character.isUpperCase(c) ? 'w' : 'b'));
        s.insert(8, Character.toLowerCase(c));
        return s.toString();
    }
}

class ClickListener implements ActionListener {

    public static JButton lastPressed;

    public void actionPerformed(ActionEvent e) {
        if (UiMain.startColor == UiMain.b.getTurn()) {
            JButton curr = (JButton) e.getSource();
            lastPressed = curr;
            UiMain.takeTurn();
        }
        //curr.setText("LMAO");
    }
}
