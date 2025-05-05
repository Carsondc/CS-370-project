package cs370_term_project;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Base64;

public class ChatClient {
	private static final String ENCRYPTION_KEY = "SimpleEncryptionKey";
	private static final byte[] key = ENCRYPTION_KEY.getBytes();
	private static String decrypt(String encryptedMessage) {
		try {
			byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
			byte[] decryptedBytes = new byte[encryptedBytes.length];
			for (int i = 0; i < encryptedBytes.length; i++) {
				decryptedBytes[i] = (byte) (encryptedBytes[i] ^ key[i % key.length]);
			}
			return new String(decryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return encryptedMessage; 
		}
	}
	private static String encrypt(String message) {
		try {
			byte[] messageBytes = message.getBytes();
			byte[] encryptedBytes = new byte[messageBytes.length];
			for (int i = 0; i < messageBytes.length; i++) {
				encryptedBytes[i] = (byte) (messageBytes[i] ^ key[i % key.length]);
			}
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return message; 
		}
	}
	public static void main(String[] args) {
		try {
			Scanner scan = new Scanner(System.in);
			System.out.print("Enter Hostname: ");
			String host = scan.nextLine();
			System.out.print("Enter Remote Port Number (4880): ");
			String temp = scan.nextLine();
			int port = 4880;
			if (!temp.isEmpty()) port = Integer.parseInt(temp);
			
			Socket socket = new Socket(host, port);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			Thread incomingMessages  = new Thread(() -> {
				while(!socket.isClosed()) {
					try {
						String inc = in.readLine();
						if (inc == null) throw new NullPointerException();
						String incoming = decrypt(inc);
						System.out.println(incoming);
						if (incoming.equals("You were kicked.")) System.exit(0);
					} catch (IOException | NullPointerException e) {
						System.err.println("You disconnected.");
						break;
					}
				}
			});
			incomingMessages.start();
			while (!socket.isClosed()) {
				try {
					String message = scan.nextLine();
					if (message.equals("exit")) break;
					if (message.isEmpty()) continue;
					out.write(encrypt(message));
					out.newLine();
					out.flush();
				} catch (NullPointerException n) {
					System.err.println("You disconnected.");
					break;
				}
			}
			scan.close();
			socket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}