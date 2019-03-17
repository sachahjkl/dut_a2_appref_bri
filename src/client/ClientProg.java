package client;

public class ClientProg extends AClient {

	private final static int PORT_SERVICE = 1958;
	private final static String HOST = "localhost";

	public static void main(String[] args) {
		run(HOST, PORT_SERVICE);
	}
}