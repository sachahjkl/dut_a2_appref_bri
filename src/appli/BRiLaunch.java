package appli;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

import bri.ServeurBRi;
import bri.ServiceRegistry;

public class BRiLaunch {
	private final static int PORT_SERVICE = 3000;

	public static void main(String[] args) throws MalformedURLException {
		@SuppressWarnings("resource")
		Scanner clavier = new Scanner(System.in);
//		String fileName = "E:\\S4\\Appli_Reflexive\\Projet_Appli_Reflexive\\apache-ftpserver-1.1.0\\res\\home"
//				+ "";
//		String fileNameURL = "file:" +  fileName;

		// URLClassLoader sur ftp
		// URLClassLoader urlcl = URLClassLoader.newInstance(new URL[] { new
		// URL(fileNameURL) });

		System.out.println("Bienvenue dans votre gestionnaire dynamique d'activité BRi");
		System.out.println("Pour ajouter une activité, celle-ci doit être présente sur votre serveur ftp");
		System.out.println("A tout instant, en tapant le nom de la classe, vous pouvez l'intégrer");
		System.out.println("Les clients se connectent au serveur 3000 pour lancer une activité");

		new Thread(new ServeurBRi(PORT_SERVICE)).start();

		while (true) {
			try {
				// String classeName = clavier.next();
				System.out.print("Numéro de la classe :");
				System.out.println(ServiceRegistry.toStringue());
				ServiceRegistry.getServiceClass(Integer.parseInt(clavier.next()) - 1);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
}
