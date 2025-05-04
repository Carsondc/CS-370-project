package cs370_term_project;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Base64;

public class Connection implements Runnable {
	private static final String ENCRYPTION_KEY = "SimpleEncryptionKey";
	protected Socket socket;
	protected BufferedReader in;
	protected BufferedWriter out;
	protected String username;
	private ConnectionHandler caster;
	public Connection(Socket socket, ConnectionHandler caster) {
		try {
			this.socket = socket;
			this.caster = caster;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			username = socket.getInetAddress().toString() + ":" + socket.getPort();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String encrypt(String message) {
    try {
        byte[] key = ENCRYPTION_KEY.getBytes();
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
private String decrypt(String encryptedMessage) {
    try {
        byte[] key = ENCRYPTION_KEY.getBytes();
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
	//May be called from various sources
	public synchronized void disconnect() {
		if (socket.isClosed()) return;
		try {
			caster.remove(this);
			in.close();
			out.close();
			socket.close();
			System.out.println(username + " disconnected.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendMessage(String message) {
		try {
			String encryptedMessage = encrypt(message);
			out.write(encryptedMessage);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String readMessage() {
		try {
			String encryptedMessage = in.readLine();
			if (encryptedMessage == null) return null;
			// Special case for "cease" command that should not be encrypted
			if (encryptedMessage.equals("cease")) return encryptedMessage;
			return decrypt(encryptedMessage);
		} catch (IOException e) {
			disconnect();
		}
		return null;
	}
	@Override
	public void run() {
		while(true) {
			String message = readMessage();
			if (socket.isClosed() || message == null) break;
			caster.broadcast(message, this);
		}
		disconnect();
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String newUsername) {
		username = newUsername;
	}
}