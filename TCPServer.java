import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private static int port;
    public static void main(String[] args) {
        port = Integer.parseInt(args[0]);

        if (port <= 1024 || port > 65535) //Valid port values
            System.exit(0);
        try {
            ServerSocket servSocket = new ServerSocket(port);
            Socket clientSocket = servSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Clients Connected!");
            String message = "";
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
            System.out.println("Finished listening for client messages.");
            servSocket.close();
            clientSocket.close();
            in.close();

        } catch (IOException i) {
            System.out.println(i.getMessage());
            System.exit(0);
        }
    }
}