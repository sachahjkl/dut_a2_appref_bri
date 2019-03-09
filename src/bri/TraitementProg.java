package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import users.Programmer;

public class TraitementProg implements Traitement {

	public static String stop = "#30#";
	public static String end = "#31#";
	private Socket client;

	TraitementProg(Socket socket) {
		client = socket;
	}

	public void run() {
		System.out.println("log : Service programmeur démarré");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			while (true) {
				out.println("* Que voulez vous faire :");
				out.println("* 1 : Connexion");
				out.println("* 2 : Inscription");
				out.println("* 3 : Fin\n" + stop);
				try {
					int choix = Integer.parseInt(in.readLine());
					switch (choix) {
					case 1:
						login(in, out);
						break;
					case 2:
						register(in, out);
						break;
					case 3:
						out.println(end);
						try {
							finalize();
						} catch (Throwable e) {
						}
						return;
					default:
						out.println("Ce choix n'existe pas. Réessayez");
						break;
					}
				} catch (NumberFormatException e) {
					out.println("Vous avez écrit n'importe quoi !");
				}
			}

		} catch (IOException e) {
			// Fin du service
		}
	}

	private void register(BufferedReader in, PrintWriter out) {
		try {
			String login, pwd, password2, FTPAddress;
			out.println("nom : \n" + stop);
			login = in.readLine();
			if (login.length() < 3) {
				out.println("Nom trop court (inférieur à 3).");
				return;
			}
			out.println("mot de passe : \n" + stop);
			pwd = in.readLine();
			out.println("confirmez mot de passe:\n" + stop);
			password2 = in.readLine();
			if (!pwd.equals(password2)) {
				out.println("Mot de passes différents.");
				return;
			}
			out.println("addresse ftp : \n" + stop);
			FTPAddress = in.readLine();
			UserRegistry.registerProgrammer(login, pwd, FTPAddress);
			out.println("Compte créé avec succès.");
		} catch (Exception e) {
			out.println(e.getMessage());
		}
	}

	private void login(BufferedReader in, PrintWriter out) {
		String login, pwd;
		try {
			out.println("nom : \n" + stop);

			login = in.readLine();
			out.println("mot de passe :\n" + stop);
			pwd = in.readLine();
			User u = UserRegistry.login(login, pwd);
			if (u == null) {
				out.println("Utilisateur introuvable.");
				return;
			} else {
				loggedIn(u, in, out);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loggedIn(User u, BufferedReader in, PrintWriter out) {
		try {
			while (true) {
				out.println("* Programmeur connecté : " + u.getLogin());
				out.println("* 1 : Fournir un nouveau service");
				out.println("* 2 : Mettre-à-jour un service");
				out.println("* 3 : Déclarer un changement d’adresse de son serveur ftp");
				out.println("* 4 : Déconnexion\n" + stop);
				try {
					int choix = Integer.parseInt(in.readLine());
					switch (choix) {
					case 1:
						break;
					case 2:
						break;
					case 3:
						changeFTP(u, in, out);
						break;
					case 4:
						return;
					default:
						break;
					}
				} catch (NumberFormatException e) {
					out.println("Vous avez écrit n'importe quoi !");
				}
			}
		} catch (IOException e) {
			out.println(e.getMessage());
		}
	}

	private void changeFTP(User u, BufferedReader in, PrintWriter out) {
		try {
			out.println("nouvelle adresse FTP : \n" + stop);
			String address = in.readLine();
			((Programmer) u).setFTPAddress(address);
			out.println("Adresse ftp changée avec succès.");
		} catch (IOException e) {
			out.println(e.getMessage());
		}
	}

	protected void finalize() throws Throwable {
		System.out.println("log : Service programmeur éteint");
		client.close();
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();
	}

}
