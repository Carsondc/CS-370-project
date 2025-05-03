package cs370_term_project;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Connection implements Runnable {
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
			out.write(message);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String readMessage() {
		try {
			return in.readLine();
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