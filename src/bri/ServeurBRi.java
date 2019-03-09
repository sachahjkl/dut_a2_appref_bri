package bri;

import java.io.*;
import java.net.*;

public class ServeurBRi implements Runnable {
	private ServerSocket listen_socket;
	private String type;

	// Cree un serveur TCP - objet de la classe ServerSocket
	public ServeurBRi(int port, String type) {
		try {
			listen_socket = new ServerSocket(port);
			this.type = type;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// Le serveur ecoute et accepte les connections.
	// pour chaque connection, il cree un ServiceInversion,
	// qui va la traiter.
	public void run() {
		System.out.println("log : Serveur démarré " + type);
		try {
			while (true) {
				Socket s = listen_socket.accept();
				FabriqueTraitement.make(type, s).start();
			}
		} catch (IllegalArgumentException | IOException e) {
			System.err.println("Pb sur le port d'écoute :" + e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// restituer les ressources --> finalize
	protected void finalize() throws Throwable {
		System.out.println("log : Serveur éteint " + type);
		try {
			this.listen_socket.close();
		} catch (IOException e1) {
		}
	}

	// lancement du serveur
	public void start() {
		(new Thread(this)).start();
	}
}
