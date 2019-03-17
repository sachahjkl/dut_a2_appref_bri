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

	public static final String stop = "#30#";
	public static final String end = "#31#";

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
				out.println("* 3 : Quitter\n" + stop);
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
						finalize();
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
			finalize();
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
				out.println("* 5 : Désinstaller un service");
				out.println("* 6 : Déclarer un changement d’adresse de son serveur ftp");
				out.println("* 7 : Voir vos services ajoutées");
				out.println("* 8 : Déconnexion\n" + stop);
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
						removeService(u, in, out);
						break;
					case 6:
						changeFTP(u, in, out);
						break;
					case 7:
						dispServices(u, in, out);
						break;
					case 8:
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
			out.println(e.getMessage());
		}
	}

	private void addService(User u, BufferedReader in, PrintWriter out) {
		try {
			out.println("****************");
			out.println("* 0 : Annuler");
			out.println("Saisissez le nom du service (du .class) à ajouter : \n" + stop);
			String serviceStr = in.readLine();
			if (serviceStr.equals("0"))
				return;
			URLClassLoader classLoader = new URLClassLoader(new URL[] { u.getFTPAddress() });
			Class<?> service = classLoader.loadClass(u.getLogin() + "." + serviceStr);
			classLoader.close();
			if (ServiceRegistry.addService(service))
				out.println("Service " + serviceStr + " ajouté.");
		} catch (IOException e) {
			out.println(e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			out.println("Erreur au chargement de la classe: " + e.getMessage());
			e.printStackTrace();
		}
		out.println("****************");

	}

	private void updateService(User u, BufferedReader in, PrintWriter out) {
		String[] sArr = ServiceRegistry.getServices(u);
		out.println("****************");
		if (sArr.length == 0) {
			out.println("Vous n'avez pas de services.");
		} else {
			try {
				out.println("* 0 : Annuler");
				out.println("Vos services : ");
				int i = 1;
				for (String s : sArr)
					out.println("* " + i++ + " : " + s);
				out.println("Entrez le numéro de service : \n" + stop);
				int numService = Integer.parseInt(in.readLine());
				if (numService == 0)
					return;
				if (numService < 1 || numService > sArr.length) {
					out.println("Numéro de service incorrect.");
					return;
				}
				String serviceStr = sArr[numService - 1];
				URLClassLoader classLoader = new URLClassLoader(new URL[] { u.getFTPAddress() });
				try {
					Class<?> updated = classLoader.loadClass(u.getLogin() + "." + serviceStr);
					if (updated != null)
						out.println(updated.getSimpleName());
					if (ServiceRegistry.updateService(numService, updated, u))
						out.println("Service " + serviceStr + " mis à jour.");
				} catch (ClassNotFoundException e) {
					out.println("Erreur au chargement de la classe: " + e.getMessage());
					e.printStackTrace();
				}
				classLoader.close();
			} catch (IOException e) {
				out.println("Erreur : " + e.getMessage());
				e.printStackTrace();
			}
		}
		out.println("****************");
	}

	private void demarrerService(User u, BufferedReader in, PrintWriter out) {
		String[] saArr = ServiceRegistry.getServicesArretes(u);
		out.println("****************");
		if (saArr.length == 0) {
			out.println("Vous n'avez pas de services arrêtés.");
		} else {
			try {
				out.println("* 0 : Annuler");
				out.println("Vos services arrêtés : ");
				int i = 1;
				for (String s : saArr)
					out.println("* " + i++ + " : " + s);
				out.println("Entrez le numéro de service : \n" + stop);
				int numService = Integer.parseInt(in.readLine());
				if (numService == 0)
					return;
				if (ServiceRegistry.demarrerService(numService, u))
					out.println("Service " + saArr[numService - 1] + " démarré.");
				else
					out.println("Service " + numService + " inexistant.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		out.println("****************");
	}

	private void arreterService(User u, BufferedReader in, PrintWriter out) throws NumberFormatException {

		String[] sdArr = ServiceRegistry.getServicesDemarres(u);
		out.println("****************");
		if (sdArr.length == 0) {
			out.println("Vous n'avez pas de services démarrés.");
		} else {
			try {
				out.println("* 0 : Annuler");
				out.println("Vos services démarrés : ");
				int i = 1;
				for (String s : sdArr)
					out.println("* " + i++ + " : " + s);
				out.println("Entrez le numéro de service : \n" + stop);
				int numService = Integer.parseInt(in.readLine());
				if (numService == 0)
					return;
				if (ServiceRegistry.arreteService(numService, u))
					out.println("Service " + sdArr[numService - 1] + " arrêté.");
				else
					out.println("Service " + numService + " inexistant.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		out.println("****************");
	}

	private void removeService(User u, BufferedReader in, PrintWriter out) {
		String[] sArr = ServiceRegistry.getServices(u);
		out.println("****************");
		if (sArr.length == 0) {
			out.println("Vous n'avez pas de services.");
		} else {
			try {
				out.println("* 0 : Annuler");
				out.println("Vos services : ");
				int i = 1;
				for (String s : sArr)
					out.println("* " + i++ + " : " + s);
				out.println("Entrez le numéro de service : \n" + stop);
				int numService = Integer.parseInt(in.readLine());
				if (numService == 0)
					return;
				if (numService < 1 || numService > sArr.length) {
					out.println("Numéro de service incorrect.");
					return;
				}
				Class<? extends Service> toRemove = ServiceRegistry.getServicesClass(numService, u);
				if (ServiceRegistry.removeService(toRemove))
					out.println("Service " + numService + " désinstallé.");
				else
					out.println("Service " + numService + " inexistant.");
			} catch (IOException e) {
				out.println("Erreur : " + e.getMessage());
				e.printStackTrace();
			}
		}
		out.println("****************");
	}

	private void dispServices(User u, BufferedReader in, PrintWriter out) {
		String[] sdArr = ServiceRegistry.getServicesDemarres(u);
		String[] saArr = ServiceRegistry.getServicesArretes(u);
		int i = 1;
		out.println("****************");
		if (sdArr.length == 0) {
			out.println("Vous n'avez pas de services démarrés.");
		} else {
			out.println("Vos services démarrés : ");
			for (String s : sdArr)
				out.println("* " + i++ + " : " + s);
		}
		if (saArr.length == 0) {
			out.println("Vous n'avez pas de services arrétés.");
		} else {
			out.println("Vos services arrétés : ");
			for (String s : saArr)
				out.println("* " + i++ + " : " + s);
		}
		out.println("****************");
	}

	private void changeFTP(User u, BufferedReader in, PrintWriter out) {
		try {
			out.println("votre adresse actuelle : " + u.getFTPAddress().toString());
			out.println("* 0 : Annuler");
			out.println("nouvelle adresse FTP : \n" + stop);
			String address = in.readLine();
			if (address.equals("0"))
				return;
			((Programmer) u).setFTPAddress(address);
			out.println("Adresse ftp changée avec succès.");
		} catch (IOException e) {
			out.println(e.getMessage());
		}
	}

	protected void finalize() {
		System.out.println("log : Service programmeur éteint");
		try {
			client.close();
		} catch (IOException e) {
			System.err.println("log : Erreur à la fermeture de la socket.");
		}
	}

	public void start() {
		(new Thread(this)).start();
	}

}
