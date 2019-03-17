package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class AClient {

	public static final String stop = "#30#";
	public static final String end = "#31#";

	public static void run(String host, int port_service) {
		Socket s = null;
		try {
			s = new Socket(host, port_service);

			BufferedReader sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter sout = new PrintWriter(s.getOutputStream(), true);
			BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Connecté au serveur " + s.getInetAddress() + ":" + s.getPort());

			String line;
			while (true) {
				line = sin.readLine();
				while (!line.equals(stop)) {
					if (line.equals(end)) {
						s.close();
						break;
					}
					System.out.println(line);
					line = sin.readLine();
				}
				if (line.equals(end)) {
					s.close();
					System.err.println("Fin de la connexion");
					return;
				}
				System.out.print("> ");
				sout.println(clavier.readLine());
			}
		} catch (IOException e) {
			System.err.println("Fin de la connexion");
		}
	}

}