package examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
			URL url = new URL(line);
			URLConnection con = url.openConnection();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);

			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				builder.parse(new InputSource(con.getInputStream()));
				out.println("fichier correct, rapport :");
				out.println("La syntaxe du document est correcte.");
			} catch (ParserConfigurationException | SAXException e) {
				out.println("fichier malformé, rapport :");
				out.println(e.getMessage());
			}
		} catch (IOException e) {
			System.err.println("Problème avec la socket");
			e.printStackTrace();
		}
	}

	public static String toStringue() {
		return "Analyse de fichier XML";
	}

}
