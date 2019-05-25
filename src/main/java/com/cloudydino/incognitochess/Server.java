package com.cloudydino.incognitochess;

import java.net.*;
import java.io.*;

public class Server implements Runnable {
    //initialize socket and input stream
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private int port;

    // constructor with port
    public Server(int port) {
        this.port = port;
    }

    public void run() {
        // starts server and waits for a connection
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket
            in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));

            boolean handshake = false;

            while (!handshake) {
                System.out.println("Handshaking...");
                long l = in.readLong();
                System.out.println("Recieved: " + l);
                handshake = UiMain.handshake(l);
            }

            UiMain.startGame();

            int line = 1;

            // reads message from client until "Over" is sent
            while (line != -1) {
                int[] move = new int[4];
                for (int i = 0; i < 4; i++) {
                    try {
                        line = in.readInt();
                        move[i] = line;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                char promotion = 'Q';
                try {
                    promotion = in.readChar();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.printf("Received:\t(%d, %d) to (%d, %d) - %c%n", move[0], move[1], move[2], move[3], promotion);

                UiMain.receiveMove(move, promotion);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            System.out.println("Closing connection");

            // close connection
            socket.close();
            in.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
