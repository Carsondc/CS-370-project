package cs370_term_project;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
	final private static String PASSWORD = "potato";
	
	private int port;
	private ServerSocket servSocket;
	private ConnectionHandler handler;
	
	public ChatServer(int port) {
		this.port = port;
		if (port <= 1024 || port > 65535) System.exit(0);
		try {
			servSocket = new ServerSocket(port);
			handler = new ConnectionHandler(servSocket);
			new Thread(handler).start();
		} catch (IOException e) {}
	}
	
	public void close() {
		try {
			servSocket.close();
			handler.closeAll();
		} catch (IOException e) {}
	}
	
	public static boolean verifyPassword(String p) {
		return p.equals(PASSWORD);
	}
}