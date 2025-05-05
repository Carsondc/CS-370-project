package cs370_term_project;
import java.io.IOException;
import java.net.ServerSocket;

public class ChatServer {
	private static String password;
	private static boolean locked = false;
	
	private static ServerSocket servSocket;
	private static ConnectionHandler handler;
	private static CommandParser parser;

	public static boolean isLocked() {
		return locked;
	}
	public static void lock() {
		locked = true;
		handler.closeAllPending();
	}
	public static void unlock() {
		locked = false;
	}

	
	public static void begin(int port, String password) {
		if (port <= 1024 || port > 65535) System.exit(0);
		try {
			servSocket = new ServerSocket(port);
			servSocket.setSoTimeout(1000);
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
	
	public static void close() {
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