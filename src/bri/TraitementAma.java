package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TraitementAma implements Traitement {

	public static final String stop = "#30#";
	public static final String end = "#31#";

	private Socket client;

	TraitementAma(Socket socket) {
		client = socket;
	}

	public void run() {
		System.out.println("log : Service amateur démarré");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);

			while (true) {
				String[] servicesD = ServiceRegistry.getServicesDemarres();
				out.println("* Que voulez vous faire :");
				out.println("* 0 : Quitter");
				if (servicesD.length == 0) {
					out.println("* Pas de services disponibles.\n" + stop);
				} else {
					out.println("* Lancer un service : ");
					int i = 1;
					for (String s : servicesD)
						out.println("* " + i++ + " : " + s);
					out.println(stop);
				}
				try {
					int choix = Integer.parseInt(in.readLine());
					if (choix == 0) {
						out.println(end);
						finalize();
						return;
					} else if (choix > 0 && choix <= servicesD.length) {
						try {
							ServiceRegistry.getServiceClass(choix).getDeclaredConstructor(Socket.class)
									.newInstance(client).run();
							out.println("");
						} catch (Exception e) {
							out.println("Oups! Erreur au chargement du service ");
							e.printStackTrace();
						}
					} else
						out.println("Ce choix n'existe pas. Réessayez");
				} catch (NumberFormatException e) {
					out.println("Vous avez écrit n'importe quoi !");
				}
			}
		} catch (IOException e) {
			finalize();
		}
	}

	protected void finalize() {
		System.out.println("log : Service amateur éteint");
		try {
			client.close();
		} catch (IOException e) {
			System.err.println("log : Erreur à la fermeture de la socket.");
		}
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();
	}

}
