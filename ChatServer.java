import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private static int port;
    final private static String PASSWORD = "potato";
    public static void main(String[] args) {
        port = Integer.parseInt(args[0]);

        if (port <= 1024 || port > 65535) //Valid port values
            System.exit(0);
        try {
            ServerSocket servSocket = new ServerSocket(port);
            Socket clientSocket = servSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            System.out.println("Client Connected");
            if (!in.readLine().equals(PASSWORD)) {
                out.write(0);
                out.flush();
                System.out.println("Password incorrect");
                servSocket.close();
                return;
            }
            out.write(1);
            out.flush();
            System.out.println("Correct");
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
            System.out.println("Finished listening for messages.");
            servSocket.close();
            clientSocket.close();
            in.close();

        } catch (IOException i) {
            System.out.println(i.getMessage());
            System.exit(0);
        }
    }
}