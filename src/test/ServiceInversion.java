package test;

import java.io.*;
import java.net.*;

import bri.Service;

// rien � ajouter ici
public class ServiceInversion implements Service {

	public static final String stop = "#30#";
	private final Socket client;

	public ServiceInversion(Socket socket) {
		client = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);

			out.println("Tapez un texte à inverser\n" + stop);
			String line = in.readLine();
			String invLine = new String(new StringBuffer(line).reverse());
			out.println(invLine);
		} catch (IOException e) {
			System.err.println("Problème avec la socket");
		}
	}

	public static String toStringue() {
		return "Inversion de texte";
	}
}
