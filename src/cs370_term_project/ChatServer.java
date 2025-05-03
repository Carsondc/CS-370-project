package cs370_term_project;
import java.io.IOException;
import java.net.ServerSocket;

public class ChatServer {
	private static String PASSWORD;
	
	private ServerSocket servSocket;
	private ConnectionHandler handler;
	
	public ChatServer(int port, String password) {
		if (port <= 1024 || port > 65535) System.exit(0);
		try {
			servSocket = new ServerSocket(port);
			PASSWORD = password;
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