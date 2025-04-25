package cs370_term_project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ConnectionHandler implements Runnable{	
	//private HashMap<Connection,String> connections;
	private Set<Connection> connections;
	private final ServerSocket servSocket;
	
	public ConnectionHandler(ServerSocket servSocket) {
		connections = new HashSet<>();
		this.servSocket = servSocket;
	}
	public void remove(Connection c) {
		System.out.println(c.getUsername() + " disconnected.");
		connections.remove(c);
		c.disconnect();
	}
	public void closeAll() {
		connections.forEach((c) -> c.disconnect());
	}
	@Override
	public void run() {
		while(!servSocket.isClosed()) {
			awaitConnection();
		}
	}
	public void awaitConnection() {
		try {
			Socket newClient = servSocket.accept();
			//Run as a separate thread due to waiting for user input.
			new Thread(() -> {
				Connection newConn = new Connection(newClient, this);
				System.out.println(newConn.getUsername() + " connected, awaiting password");
				if (ChatServer.verifyPassword(newConn.readMessage())) {
					newConn.sendMessage("Password correct");
					System.out.println(newConn.getUsername() + " has verified w. password.");
					newConn.sendMessage("Would you like to enter a username? (y)");
					String answer = newConn.readMessage().toLowerCase();
					if (answer.contains("y") || answer.isBlank()) {
						newConn.sendMessage("Please enter a username: ");
						newConn.setUsername(newConn.readMessage());
					}
					connections.add(newConn);
					new Thread(newConn).start();
				} else {
					newConn.sendMessage("Password incorrect");
					System.out.println(newConn.getUsername() + " gave an incorrect password.");
					newConn.disconnect();
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void broadcast(String message, Connection c) {
		String fullMessage = "<" + c.getUsername() + "> " + message;
		for (Connection co : connections) if (!co.equals(c)) co.sendMessage(fullMessage);
		System.out.println(fullMessage);
	}
}