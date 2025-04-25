package cs370_term_project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionHandler {	
	private HashMap<Connection,String> connections;
	private final ServerSocket servSocket;
	
	public ConnectionHandler(ServerSocket servSocket) {
		connections = new HashMap<>();
		this.servSocket = servSocket;
		new Thread(() -> {
			while(true) {
				awaitConnection();
			}
		}).start();
	}
	public void add(String fullName, Connection client) {
		connections.put(client, fullName);
	}
	public void remove(Connection c) {
		System.out.println(connections.get(c) + " disconnected.");
		connections.remove(c);
		c.disconnect();
	}
	public void closeAll() {
		connections.forEach((connection, name) -> connection.disconnect());
	}
	public void awaitConnection() {
		try {
			Socket newClient = servSocket.accept();
			String clientName = newClient.getInetAddress().toString() + ":" + newClient.getPort();
			System.out.println(clientName + " connected, awaiting password");
			Connection newConn = new Connection(newClient, this);
			if (ChatServer.verifyPassword(newConn.readMessage())) {
				newConn.sendMessage("Password correct");
				System.out.println(clientName + " has verified w. password.");
				add(clientName, newConn);
				new Thread(newConn).start();
			} else {
				newConn.sendMessage("Password incorrect");
				System.out.println(clientName + " gave an incorrect password.");
				newConn.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void broadcast(String message, Connection c) {
		String fullMessage = "<" + connections.get(c) + "> " + message;
		connections.forEach((connection, name) -> {
			if (!connection.equals(c))
				connection.sendMessage(fullMessage);
		});
		System.out.println(fullMessage);
	}
}
