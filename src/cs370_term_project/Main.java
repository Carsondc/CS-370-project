package cs370_term_project;

import java.io.IOException;

public class Main {
	public static void main(String args[]) {
		ChatServer server = new ChatServer(4880);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	            server.close();
	        }
	    }, "Shutdown-thread"));
	}
}