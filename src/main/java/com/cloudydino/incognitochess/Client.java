package com.cloudydino.incognitochess;

import java.net.*;
import java.io.*;

public class Client implements Runnable {

    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private String address;
    private int port;

    Client(String address, int port) {
        this.address = address;
        this.port = port;
    }

    // constructor to put ip address and port
    public void run() {
        // establish a connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException u) {
            u.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }

        UiMain.initHandshake();
    }

    public void sendLong(long l) {

        while (out == null) {

        }

        try {
            System.out.println("Sent: " + l);
            out.writeLong(l);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(int[] move, char promotion) {
        for (int i = 0; i < move.length; i++) {
            try {
                out.writeInt(move[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out.writeChar(promotion);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("Sent:\t\t(%d, %d) to (%d, %d) - %c%n", move[0], move[1], move[2], move[3], promotion);
    }

    public void disconnect() {
        try {
            input.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
