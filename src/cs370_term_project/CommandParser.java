package cs370_term_project;

import java.util.TreeMap;
import java.util.Map;
import java.util.Scanner;

import java.util.*;

public class CommandParser implements Runnable {
	public static final Map<String, String> CMDLIST;
	static {
		CMDLIST = new TreeMap<>();
		CMDLIST.put("close", "Kills the server, disconnecting all clients");
		CMDLIST.put("exit", "Alias for close");
		CMDLIST.put("help", "Displays this list of commands");
		CMDLIST.put("kick", "Takes a username as an argument. Kicks that user from the server.");
		CMDLIST.put("password", "Takes a new server password as an argument. If no argument is given, returns the current one.");
		CMDLIST.put("list", "Lists all online users.");
		CMDLIST.put("broadcast", "Sends a message to all connected users.");
		CMDLIST.put("lock", "Prevents new users from joining the server.");
		CMDLIST.put("unlock", "Allows new users to join again.");
	}

	public ConnectionHandler handler;

	public CommandParser(ConnectionHandler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		boolean running = true;
		Scanner serverIn = new Scanner(System.in);
		while (running) {
			String[] command = serverIn.nextLine().split(" ", 2);
			String base = command[0].trim();

			switch (base) {
				case "":
					break;

				case "help":
					help();
					break;

				case "password":
					String[] pwdArgs = command.length > 1 ? command[1].split(" ") : new String[0];
					if (pwdArgs.length > 1) {
						log("Passwords cannot contain spaces.");
					} else if (pwdArgs.length == 0) {
						log("Current server password is: " + ChatServer.getPassword());
					} else {
						ChatServer.changePassword(pwdArgs[0]);
						log("New server password accepted.");
						if (pwdArgs[0].length() < 8)
							log("Note: Good passwords should usually be at least 8 characters.");
					}
					break;

				case "kick":
					if (command.length < 2 || command[1].isBlank())
						log("Missing argument: username");
					else
						log(handler.kick(command[1].trim()));
					break;

				case "list":
					list();
					break;

				case "broadcast":
					if (command.length < 2 || command[1].isBlank()) {
						log("Missing message to broadcast.");
					} else {
						handler.broadcast("[SERVER]: " + command[1].trim());
						log("Message broadcasted.");
					}
					break;

				case "lock":
					ChatServer.setLocked(true);
					log("Server is now locked. New users cannot join.");
					break;

				case "unlock":
					ChatServer.setLocked(false);
					log("Server is now unlocked. New users may join.");
					break;

				case "exit":
				case "close":
					running = false;
					break;

				default:
					log("Unknown command, use \"help\" for a list of available commands.");
			}
		}
		serverIn.close();
	}
	

	public void help() {
		for (String s : CMDLIST.keySet()) {
			log(s + " - " + CMDLIST.get(s));
		}
	}

	public void list() {
		for (String s : handler.getUsers()) {
			System.out.print(s + " ");
		}
		log("");
	}

	public void log(String s) {
		System.out.println(s);
	}
}