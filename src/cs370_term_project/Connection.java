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
	private static final byte[] key = ENCRYPTION_KEY.getBytes();;
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
	private String decrypt(String encryptedMessage) {
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
		} catch (IOException e) {}
	}
	
	public String readMessage() {
		try {
			return decrypt(in.readLine());
		} catch (IOException | NullPointerException e) {
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