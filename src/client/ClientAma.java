package client;

public class ClientAma extends AClient {
	
	private final static int PORT_SERVICE = 1131;
	private final static String HOST = "localhost";

	public static void main(String[] args) {
		run(HOST, PORT_SERVICE);
	}
}
