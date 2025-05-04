package cs370_term_project;
public class Main {
	public static void main(String args[]) {
		System.out.println("test 2");
		ChatServer server = new ChatServer(Integer.parseInt(args[0]), args[1]);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	            server.close();
	        }
	    }, "Shutdown-thread"));
	}
}