package cs370_term_project;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionHandler implements Runnable{	
	private Map<String, Connection> users;
	private Set<Connection> connections;
	private final ServerSocket servSocket;
	
	public ConnectionHandler(ServerSocket servSocket) {
		connections = new HashSet<>();
		users = new TreeMap<>();
		this.servSocket = servSocket;
	}
	protected void remove(Connection c) {
		connections.remove(c);
		users.remove(c.getUsername());
	}
	private Set<String> bannedUsers = new HashSet<>();

public String ban(String username) {
	if (bannedUsers.contains(username)) return username + " is already banned.";
	bannedUsers.add(username);
	kick(username); // Optional: immediately disconnect user
	return username + " has been banned.";
}

public String unban(String username) {
	if (!bannedUsers.contains(username)) return username + " is not banned.";
	bannedUsers.remove(username);
	return username + " has been unbanned.";
}

public Set<String> getBannedUsers() {
	return Collections.unmodifiableSet(bannedUsers);
}


	public String kick(String username) {
		Connection c = users.get(username);
		if (c == null) return "User " + username + " not found.";
		users.remove(username);
		connections.remove(c);
		c.sendMessage("cease");
		c.disconnect();
		return username + " has been kicked.";
	}
	public void closeAll() {
		connections.forEach((c) -> c.disconnect());
	}
	@Override
	public void run() {
		while(!servSocket.isClosed()) {
			if (!ChatServer.isLocked() || connections.isEmpty()) {
				awaitConnection();
			} 
		}
	}
	public void awaitConnection() {
		try {
			Socket newClient = servSocket.accept();
			//Run as a separate thread due to waiting for user input.
			new Thread(() -> {
				Connection newConn = new Connection(newClient, this);
				// check if server is locked
				System.out.println("Is sever locked? " + ChatServer.isLocked());
				if (ChatServer.isLocked()) {
					newConn.sendMessage("Server is locked. No new connections will be accepted.");
					newConn.disconnect();
					return;
				}
				System.out.println(newConn.getUsername() + " connected, awaiting password");
				if (ChatServer.verifyPassword(newConn.readMessage())) {
					newConn.sendMessage("Password correct");
					System.out.println(newConn.getUsername() + " has verified w. password.");
					newConn.sendMessage("Would you like to enter a username? (y)");
					//Take the first word the user enters
					String answer = newConn.readMessage().split(" ", 2)[0].toLowerCase();
					if (answer.contains("y") || answer.isEmpty()) {
						newConn.sendMessage("Please enter a username (no spaces): ");
						newConn.setUsername(newConn.readMessage());
					}
					connections.add(newConn);
					users.put(newConn.getUsername(), newConn);
					new Thread(newConn).start();
				} else {
					newConn.sendMessage("Password incorrect");
					System.out.println(newConn.getUsername() + " gave an incorrect password.");
					newConn.disconnect();
				}
			}).start();
		} catch (IOException e) {
			//Socket.accept will throw this due to being interrupted.
		}
	}
	public void broadcast(String message, Connection c) {
		String fullMessage = "<" + c.getUsername() + "> " + message;
		for (Connection co : connections) if (!co.equals(c)) co.sendMessage(fullMessage);
		System.out.println(fullMessage);
	}
	public void broadcast(String message) {
		for (Connection co : connections) co.sendMessage(message);
		System.out.println(message);
	}
	public Set<String> getUsers() {
		return users.keySet();
	}
}
