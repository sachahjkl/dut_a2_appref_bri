package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TraitementAma implements Traitement{
	
	public static String end = "#31#";
	private Socket client;

	TraitementAma(Socket socket) {
		client = socket;
	}

	public void run() {
		System.out.println("log : Service amateur démarré");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			out.println(ServiceRegistry.toStringue() + "##Connectez-vo:");
			int choix = Integer.parseInt(in.readLine());
			
			// instancier le service numéro "choix" en lui passant la socket "client"
			// invoquer run() pour cette instance ou la lancer dans un thread à part

		} catch (IOException e) {
			// Fin du service
		}

		try {
			client.close();
		} catch (IOException e2) {
		}
	}

	protected void finalize() throws Throwable {
		System.out.println("log : Service amateur éteint");
		client.close();
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();
	}


}
