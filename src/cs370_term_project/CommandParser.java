package cs370_term_project;

import java.util.TreeMap;
import java.util.Map;
import java.util.Scanner;

public class CommandParser implements Runnable {
	public static final Map<String, String> CMDLIST;
	static {
		//This would work better as a JSON file but that would require importing with tools
		CMDLIST = new TreeMap<>();
		CMDLIST.put("close", "Kills the server, disconnecting all clients");
		CMDLIST.put("exit", "Alias for close");
		CMDLIST.put("help", "Displays this list of commands");
		CMDLIST.put("kick", "Takes a username as an argument. Kicks that user from the server.");
		CMDLIST.put("password", "Takes a new server password as an argument. If no argument is given, returns the current one.");
		CMDLIST.put("list", "Lists all online users.");

	}
	public ConnectionHandler handler;
	public CommandParser(ConnectionHandler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		boolean running = true;
		Scanner serverIn = new Scanner(System.in);
		while(running) {
			//Base command is the first word
			String[] command = serverIn.nextLine().split(" ");
			switch(command[0]) {
			case "" :
				break;
			case "help" :
				help();
				break;
			case "password" :
				if (command.length > 2)
					log("Passwords cannot contain spaces.");
				else if (command.length == 1)
					log("Current server password is: " + ChatServer.getPassword());
				else {
					ChatServer.changePassword(command[1]);
					log("New server password accepted");
					if (command[1].length() < 8) log("Note: Good passwords should usually be at least 8 characters.");
				}
				break;
			case "kick" :
				if (command.length != 2)
					log("Missing argument: username");
				else
					log(handler.kick(command[1]));
				break;
			case "list" :
				list();
				break;
			case "exit" :
			case "close" :
				running = false;
				break;
			default :
				log("Unknown command, use \"help\" for a list of availible commmands.");
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
	//Shortening of print
	public void log(String s) {
		System.out.println(s);
	}
}
