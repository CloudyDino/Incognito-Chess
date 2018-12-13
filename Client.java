import java.net.*;
import java.io.*;

public class Client implements Runnable {

	private Socket socket		 = null;
	private DataInputStream input = null;
    private DataOutputStream out	 = null;
    private String address;
    private int port;

    public Client(String address, int port) {
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
		} catch(UnknownHostException u) {
			u.printStackTrace();
		} catch(IOException i) {
			i.printStackTrace();
		}

		// // keep reading until "Over" is input
        // while (UIMain.sending.length == 4) {
        //     while (!UIMain.moveSend) {
        //     }

        //     try {
        //         for (int i = 0; i < UIMain.sending.length; i++) {
        //             out.writeInt(UIMain.sending[i]);
        //         }
        //     } catch(IOException i) {
        //         System.out.println(i);
        //     }

        //     UIMain.moveSend = false;
        // }
    }

    public void sendDouble(double d) {
        try {
            out.writeDouble(d);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(int[] move) {
        for (int i = 0; i < move.length; i++) {
            try {
                out.writeInt(move[i]);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
		// close the connection
		try {
			input.close();
			out.close();
			socket.close();
		} catch(IOException i) {
			i.printStackTrace();
		}
	}
}
