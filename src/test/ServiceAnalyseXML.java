package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import bri.Service;

public class ServiceAnalyseXML implements Service {

	public static final String stop = "#30#";
	private final Socket client;

	public ServiceAnalyseXML(Socket socket) {
		client = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);

			out.println("Tapez l'adresse du fichier placé sur un serveur ftp :\n" + stop);
			String line = in.readLine();
			URI fileURL = new URI(line);
			BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(new File(line))));
		} catch (IOException e) {
			System.err.println("Problème avec la socket");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String toStringue() {
		return "Inversion de texte";
	}

}
