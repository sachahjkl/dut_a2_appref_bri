package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

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
			out.println("addresse ftp :");
			out.println("ftp://\n" + stop);
			FTPAddress = in.readLine();
			ServiceRegistry.registerProgrammer(login, pwd, FTPAddress);
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
			User u = ServiceRegistry.login(login, pwd);
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
				out.println("* 3 : Demarrer un service");
				out.println("* 4 : Arrêter un service");
				out.println("* 5 : Déclarer un changement d’adresse de son serveur ftp");
				out.println("* 6 : Voir vos services ajoutées");
				out.println("* 7 : Déconnexion\n" + stop);
				try {
					int choix = Integer.parseInt(in.readLine());
					switch (choix) {
					case 1:
						addService(u, in, out);
						break;
					case 2:
						updateService(u, in, out);

						break;
					case 3:
						demarrerService(u, in, out);
						break;
					case 4:
						arreterService(u, in, out);
						break;
					case 5:
						changeFTP(u, in, out);
						break;
					case 6:
						dispServices(u, in, out);
						break;
					case 7:
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

	private void updateService(User u, BufferedReader in, PrintWriter out) {
		String s = ServiceRegistry.getService(u);
		out.println("");

	}

	private void arreterService(User u, BufferedReader in, PrintWriter out) throws NumberFormatException {

		String sd = ServiceRegistry.getServicesDemarres(u);
		out.println("****************");
		if (sd.equals("")) {
			out.println("Vous n'avez pas de services démarrés.");
		} else {
			try {
				out.println("Vos services démarrés : ");
				out.println(sd);
				out.println("Entrez le numéro de service : \n" + stop);
				int numService = Integer.parseInt(in.readLine());
				if (ServiceRegistry.arreteService(numService, u))
					out.println("Service " + numService + " arrêté.");
				else
					out.println("Service " + numService + " inexistant.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		out.println("****************");
	}

	private void demarrerService(User u, BufferedReader in, PrintWriter out) {
		String sd = ServiceRegistry.getServicesArretes(u);
		out.println("****************");
		if (sd.equals("")) {
			out.println("Vous n'avez pas de services arrêtés.");
		} else {
			try {
				out.println("Vos services arrêtés : ");
				out.println(sd);
				out.println("Entrez le numéro de service : \n" + stop);
				int numService = Integer.parseInt(in.readLine());
				if (ServiceRegistry.demarrerService(numService, u))
					out.println("Service " + numService + " démarré.");
				else
					out.println("Service " + numService + " inexistant.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		out.println("****************");

	}

	private void addService(User u, BufferedReader in, PrintWriter out) {
		try {
			out.println("****************");
			out.println("Saisissez le nom du service (du .class) à ajouter : \n" + stop);
			String serviceStr = in.readLine();
			URLClassLoader classLoader = new URLClassLoader(new URL[] { u.getFTPAddress() });
			Class<?> service = classLoader.loadClass(u.getLogin() + "." + serviceStr);
			classLoader.close();
			ServiceRegistry.addService(service);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			out.println("Votre service ne se trouve pas correctement sur votre serveur FTP.");
		}
	}

	private void dispServices(User u, BufferedReader in, PrintWriter out) {

		String sd = ServiceRegistry.getServicesDemarres(u);
		String sa = ServiceRegistry.getServicesArretes(u);
		out.println("****************");
		if (sd.equals("")) {
			out.println("Vous n'avez pas de services démarrés.");
		} else {
			out.println("Vos services démarrés : ");
			out.println(sd);
		}
		if (sa.equals("")) {
			out.println("Vous n'avez pas de services arrétés.");
		} else {
			out.println("Vos services arrétés : ");
			out.println(sa);
		}
		out.println("****************");
	}

	private void changeFTP(User u, BufferedReader in, PrintWriter out) {
		try {
			out.println("votre adresse actuelle : " + u.getFTPAddress().toString());
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
