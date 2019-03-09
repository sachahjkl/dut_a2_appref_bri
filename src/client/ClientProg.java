package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientProg {

	private final static int PORT_SERVICE = 1958;
	private final static String HOST = "localhost";
	private static final String stop = "#30#";
	private static final String end = "#31#";

	public static void main(String[] args) {
		Socket s = null;
		try {
			s = new Socket(HOST, PORT_SERVICE);

			BufferedReader sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter sout = new PrintWriter(s.getOutputStream(), true);
			BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Connecté au serveur " + s.getInetAddress() + ":" + s.getPort());

			String line;
			while (true) {
				line = sin.readLine();
				while (!line.equals(stop)) {
					if (line.equals(end)) {
						break;
					}
					System.out.println(line);
					line = sin.readLine();
				}
				if (line.equals(end)) {
					System.err.println("Fin de la connexion");
					return;
				}
				sout.println(clavier.readLine());
			}
		} catch (IOException e) {
			System.err.println("Fin de la connexion");
		}
		try {
			if (s != null)
				s.close();
		} catch (IOException e2) {
		}
	}

}
