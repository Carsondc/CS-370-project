package cs370_term_project;
public class Main {
	public static void main(String args[]) {
		ChatServer server = new ChatServer(Integer.parseInt(args[0]), args[1]);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	            server.close();
	        }
	    }, "Shutdown-thread"));
	}
}