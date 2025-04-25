package cs370_term_project;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Connection implements Runnable{
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private ConnectionHandler caster;
	public Connection(Socket socket, ConnectionHandler caster) {
		try {
			this.socket = socket;
			this.caster = caster;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void disconnect() {
		try {
			in.close();
			out.close();
			socket.close();
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
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void run() {
		String message = readMessage();
		while(!socket.isClosed() && message != null) {
			caster.broadcast(message, this);
			message = readMessage();
		}
		caster.remove(this);
	}
}