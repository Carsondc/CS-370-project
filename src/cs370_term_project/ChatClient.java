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
	private static String decrypt(String encryptedMessage) {
		try {
			byte[] key = "SimpleEncryptionKey".getBytes();
			// Decode from Base64
			byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
			byte[] decryptedBytes = new byte[encryptedBytes.length];
			// Use XOR to decrypt
			for (int i = 0; i < encryptedBytes.length; i++) {
				decryptedBytes[i] = (byte) (encryptedBytes[i] ^ key[i % key.length]);
			}
			return new String(decryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return encryptedMessage; // Fallback to the original message
		}
	}
	private static String encrypt(String message) {
		try {
			byte[] key = "SimpleEncryptionKey".getBytes();
			// Use simple XOR encryption
			byte[] messageBytes = message.getBytes();
			byte[] encryptedBytes = new byte[messageBytes.length];
			for (int i = 0; i < messageBytes.length; i++) {
				encryptedBytes[i] = (byte) (messageBytes[i] ^ key[i % key.length]);
			}
			// Convert to Base64 for safe transmission
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return message; // Fallback to plain text
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
			
			System.out.print("Please enter password: ");
			String message = scan.nextLine();
			//out.write(message);
			out.write(encrypt(message));
			out.newLine();
			out.flush();
			if (in.readLine().equals("Password incorrect")) {
				System.out.println("Incorrect");
				scan.close();
				socket.close();
				return;
			}
			//Print out incoming messages
			new Thread(() -> {
				String incoming;
				while(!socket.isClosed()) {
					try {
						incoming = in.readLine();
						if (incoming == null) continue;
						if (incoming.equals("cease")) {
							System.err.println("You were kicked.");
							System.exit(0);
						}
						// Decrypt the incoming message unless it's a special command
						if (incoming.equals("Password correct") || 
							incoming.equals("Password incorrect") ||
							incoming.equals("Would you like to enter a username? (y)") ||
							incoming.equals("Please enter a username (no spaces): ")) {
							System.out.println(incoming);
						} else {
							System.out.println(decrypt(incoming));
						}
					} catch (IOException e) {}
				}
			}).start();
			
			while (true) {
				message = scan.nextLine();
				if (message.equals("exit")) break;
				if (message.isEmpty()) continue;
				//out.write(encrypt(message));
				if (message.equals("Password correct") ||
	message.equals("Password incorrect") ||
	message.equals("Would you like to enter a username? (y)") ||
	message.equals("Please enter a username (no spaces): ") ||
	message.equals("cease")) {
	out.write(message);
} else {
	out.write(encrypt(message));
}
				out.newLine();
				out.flush();
			}
			scan.close();
			socket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}