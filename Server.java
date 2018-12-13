import java.net.*;
import java.io.*;

public class Server implements Runnable
{
	//initialize socket and input stream
	private Socket		 socket = null;
	private ServerSocket server = null;
    private DataInputStream in	 = null;
    private int port;

	// constructor with port
	public Server(int port) {
        this.port = port;
    }

    public void run() {
		// starts server and waits for a connection
		try
		{
			server = new ServerSocket(port);
			System.out.println("Server started");

			System.out.println("Waiting for a client ...");

			socket = server.accept();
			System.out.println("Client accepted");

			// takes input from the client socket
			in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));


            boolean handshake = false;

            UIMain.initHandshake();

            while (!handshake) {
                System.out.println("Handshaking...");
                double d = in.readDouble();
                System.out.println("d");
                handshake = UIMain.handshake(d);
            }


            UIMain.startgame();


			int line = 1;

			// reads message from client until "Over" is sent
			while (line != -1)
			{
                int[] move = new int[4];
                for (int i = 0; i < 4; i++) {
                    try
                    {
                        line = in.readInt();
                        System.out.println(line);
                        move[i] = line;

                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                System.out.println("end move\n");

                UIMain.recieveMove(move);
			}
			System.out.println("Closing connection");

			// close connection
			socket.close();
			in.close();
		}
		catch(IOException i)
		{
			i.printStackTrace();
		}
	}
}
