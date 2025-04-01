import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    public static void main(String[] args) {
        try {
            Thread.sleep(1000); //10 second delay
            //First arg is server address, second arg is port. Use localhost for testing, this works across lab machines for now
            Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            String message;
            System.out.println("Connected");
            Scanner scan = new Scanner(System.in);
            System.out.print("Please enter password: ");
            message = scan.nextLine();
            out.write(message);
            out.newLine();
            out.flush();
            if (in.read() == 0) {
                System.out.println("Incorrect");
                scan.close();
                socket.close();
                return;
            }
            System.out.println("Correct");
            do {
                message = scan.nextLine();
                out.write(message);
                out.newLine();
                out.flush();
            } while (!message.isBlank());

            scan.close();
            socket.close();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
