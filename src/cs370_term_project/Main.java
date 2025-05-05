package cs370_term_project;
public class Main {
	public static void main(String args[]) {
		ChatServer.begin(Integer.parseInt(args[0]), args[1]);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	        	ChatServer.close();
	        }
	    }, "Shutdown-thread"));
	}
}