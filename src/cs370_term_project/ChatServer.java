package cs370_term_project;
import java.io.IOException;
import java.net.ServerSocket;

public class ChatServer {
	private static String password;
	
	private ServerSocket servSocket;
	private ConnectionHandler handler;
	private CommandParser parser;

	private static boolean locked = false;

public static boolean isLocked() {
	return locked;
}

public static void setLocked(boolean lock) {
	locked = lock;
}

	
	public ChatServer(int port, String password) {
		if (port <= 1024 || port > 65535) System.exit(0);
		try {
			servSocket = new ServerSocket(port);
			ChatServer.password = password;
			handler = new ConnectionHandler(servSocket);
			new Thread(handler).start();
			parser = new CommandParser(handler);
			Thread parserTh = new Thread(parser);
			parserTh.start();
			//Command Parser will cease to run if "close" is entered
			parserTh.join();
			close();
		} catch (IOException | InterruptedException e) {}
	}
	
	public void close() {
		try {
			servSocket.close();
			handler.closeAll();
		} catch (IOException e) {}
	}
	
	public static boolean verifyPassword(String p) {
		return p.equals(password);
	}
	public static void changePassword(String p) {
		password = p;
	}
	public static String getPassword() {
		return password;
	}
}