package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

class UiMain extends JFrame {

     static Board b;
    private static JButton[][] buttonArr;
    private static ArrayList<JButton> presses = new ArrayList<>();
    private static Server server;
    private static Client client;
    private static long startColorSeed;
    static boolean startColor;
    static boolean localGame;
    private static JFrame f;

    private static final int WINDOW_WIDTH = 960;
    private static final int WINDOW_HEIGHT = 960;
    private static final int WINDOW_START_X = 200;
    private static final int WINDOW_START_Y = 50;

    private static final String IMAGES_FOLDER = "res";
    private static final String IMAGES_FILETYPE = "png";

    private UiMain() {
        setTitle("Incognito Chess");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocation(WINDOW_START_X, WINDOW_START_Y);

        // Window Listeners
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (!localGame) {
                    client.disconnect();
                    server.disconnect();
                }
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        b = new Board();

        localGame = args.length == 0;

        if (localGame) {
            startColor = true;
            startgame();
        } else {
            startColorSeed = (new Random()).nextLong();

            client = new Client(args[0], 5000);
            Thread clientThread = new Thread(client);
            clientThread.start();

            server = new Server(5000);
            Thread serverThread = new Thread(server);
            serverThread.start();
        }
    }

    static void initHandshake() {
        client.sendLong(startColorSeed);
    }

    static boolean handshake(long l) {
        if (startColorSeed == l) {
            initHandshake();
            return false;
        }
        startColor = startColorSeed > l;
        return true;
    }

    static void startgame() {
        System.out.println("You are: " + (startColor ? "White" : "Black"));

        f = new UiMain();
        f.setResizable(false);

        Container contentPane = f.getContentPane();
        JPanel chessPanel = new JPanel(new GridLayout(8, 8));

        buttonArr = new JButton[Board.SIZE][Board.SIZE];

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < Board.SIZE; j++) {
                JButton square = new JButton();
                char c;
                if (startColor) {
                    c = b.getBoard()[j][i];
                } else {
                    c = b.getBoard()[Board.SIZE - 1 - j][Board.SIZE - 1 - i];
                }
                ImageIcon icon = getIcon(c);
                square.setIcon(icon);

                square.addActionListener(new ClickListener());
                if (startColor) {
                    square.setActionCommand(j + " " + i);
                } else {
                    square.setActionCommand((Board.SIZE - 1 - j) + " " + (Board.SIZE - 1 - i));
                }
                square.setFocusPainted(false);
                square.setRolloverEnabled(true);
                square.setBorderPainted(false);

                if (startColor) {
                    buttonArr[j][i] = square;
                } else {
                    buttonArr[Board.SIZE - 1 - j][Board.SIZE - 1 - i] = square;
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

    static void takeTurn() {
        presses.add(ClickListener.lastPressed);

        if (presses.size() >= 2) {
            String start = presses.get(0).getActionCommand();
            String end = presses.get(1).getActionCommand();

            int startX = (start.charAt(0) - '0');
            int startY = (start.charAt(2) - '0');
            int endX = (end.charAt(0) - '0');
            int endY = (end.charAt(2) - '0');
            Piece promotion = Piece.QUEEN;

            if (b.doesPromote(startX, startY, endY)) {
                promotion = getPromotionPiece();
            }

            int[] sending = {startX, startY, endX, endY};

            boolean moveIsLegal = b.move(startX, startY, endX, endY, promotion);
            if (moveIsLegal) {
                refreshBoard();
                if (!localGame) {
                    client.sendMove(sending, promotion.toChar(startColor));
                }
            }

            presses.clear();
        }
    }

    static void receiveMove(int[] move, char promotion) {
        b.move(move[0], move[1], move[2], move[3], Piece.fromChar(promotion));
        refreshBoard();
    }

    private static void refreshBoard() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {

                char c;
                if (startColor) {
                    c = b.getBoard()[i][j];
                } else {
                    c = b.getBoard()[Board.SIZE - 1 - i][Board.SIZE - 1 - j];
                }
                ImageIcon icon = getIcon(c);
                if (startColor) {
                    buttonArr[i][j].setIcon(icon);
                } else {
                    buttonArr[Board.SIZE - 1 - i][Board.SIZE - 1 - j].setIcon(icon);
                }

            }
        }
    }

    private static Piece getPromotionPiece() {
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

        switch (chosen) {
            case 1: return Piece.ROOK;
            case 2: return Piece.BISHOP;
            case 3: return Piece.KNIGHT;
        }
        return Piece.QUEEN;
    }

    private static ImageIcon getIcon(char piece) {
        ImageIcon icon = new ImageIcon(getImageFile(piece));
        Image image = icon.getImage();
        Image smoothImage = image.getScaledInstance(120, 120,  Image.SCALE_SMOOTH);
        return new ImageIcon(smoothImage);
    }

    private static String getImageFile(char piece) {
        char color = Character.isUpperCase(piece) ? 'w' : 'b';
        char pieceLowerCase = Character.toLowerCase(piece);
        return String.format("%s/%s%s.%s", IMAGES_FOLDER, color, pieceLowerCase, IMAGES_FILETYPE);
    }
}

class ClickListener implements ActionListener {

    static JButton lastPressed;

    public void actionPerformed(ActionEvent e) {
        if (UiMain.localGame || UiMain.startColor == UiMain.b.getTurn()) {
            lastPressed = (JButton) e.getSource();
            UiMain.takeTurn();
        }
    }
}
