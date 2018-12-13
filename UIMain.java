import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.Image;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import javafx.scene.control.Button;

class ClickListener implements ActionListener {

    public static JButton lastPressed;

    public void actionPerformed(ActionEvent e) {
        if (UIMain.startColor == UIMain.b.getTurn()) {
            JButton curr = (JButton) e.getSource();
            lastPressed = curr;
            UIMain.takeTurn();
        }
        //curr.setText("LMAO");
    }
}

class UIMain extends JFrame {

    public static Board b;
    public static JButton[][] buttonArr;
    public static ArrayList<JButton> presses = new ArrayList<>();
    static Server server;
    static Client client;
    public static double currentval;
    public static boolean startColor;

    // Constructor:
    public UIMain() {
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

        currentval = (new Random()).nextDouble();

        client = new Client(args[0], 5000);

        Thread clientThread = new Thread(client);
        clientThread.start();

        server = new Server(5000);

        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public static void initHandshake() {
        client.sendDouble(currentval);
    }

    public static boolean handshake(double d) {
        if (currentval == d) {
            initHandshake();
            return false;
        } else if (currentval > d) {
            startColor = true;
        } else {
            startColor = false;
        }

        return true;
    }

    public static void startgame() {
        System.out.println("You are: " + (startColor ? "White":"Black"));

        JFrame f = new UIMain();
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
                    c = b.getBoard()[7-j][7-i];
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
                    square.setActionCommand((7-j) + " " + (7-i));
                }
                square.setFocusPainted(false);
                square.setRolloverEnabled(true);
                square.setBorderPainted(false);

                if (startColor) {
                    buttonArr[j][i] = square;
                } else {
                    buttonArr[7-j][7-i] = square;
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

            int startx = (start.charAt(0) - '0');
            int starty = (start.charAt(2) - '0');
            int endx = (end.charAt(0) - '0');
            int endy = (end.charAt(2) - '0');

            int[] sending = {startx, starty, endx, endy};

            boolean check = b.move(startx, starty, endx, endy);

            if (check) {
                refreshBoard();

                presses.clear();
                System.out.println("Send initializing...");
                client.sendMove(sending);
            } else {
                presses.clear();
            }
        }
    }

    public static void recieveMove(int[] move) {
        b.move(move[0], move[1], move[2], move[3]);

        refreshBoard();
    }

    public static void refreshBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                char c;
                if (startColor) {
                    c = b.getBoard()[i][j];
                } else {
                    c = b.getBoard()[7-i][7-j];
                }
                ImageIcon icon = new ImageIcon(getImageFile(c));
                Image piece = icon.getImage();
                Image newimg = piece.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH);
                icon = new ImageIcon(newimg);
                if (startColor) {
                    buttonArr[i][j].setIcon(icon);
                } else {
                    buttonArr[7-i][7-j].setIcon(icon);
                }

            }
        }
    }

    private static String getImageFile(char c) {
        StringBuilder s = new StringBuilder("pieces/.png");
        s.insert(7, (Character.isUpperCase(c) ? 'w' : 'b'));
        s.insert(8, Character.toLowerCase(c));
        return s.toString();
    }
}