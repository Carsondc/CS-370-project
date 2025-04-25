package cs370_term_project;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
	private static String username;
	public static void main(String[] args) {
		try {
			Thread.sleep(1000); //1 second delay
			Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			Scanner scan = new Scanner(System.in);
			
			String message;
			System.out.println("Connected");
			System.out.print("Please enter password: ");
			message = scan.nextLine();
			out.write(message);
			out.newLine();
			out.flush();
			if (in.readLine().equals("Password incorrect")) {
				System.out.println("Incorrect");
				scan.close();
				socket.close();
				return;
			}
//			System.out.println("Correct");
//			System.out.print("Please enter chosen username: ");
//			username = scan.nextLine();
//			out.write(message);
//			out.newLine();
//			out.flush();
			new Thread(() -> {
				String incoming = "";
				while(!socket.isClosed()) {
					try {
						incoming = in.readLine();
						System.out.println(incoming);
					} catch (IOException e) {}
				}
			}).start();
			message = scan.nextLine();
			while (!message.equals("exit")) {
				out.write(message);
				out.newLine();
				out.flush();
				message = scan.nextLine();
			}
			scan.close();
			socket.close();
		} catch (IOException | InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
}
