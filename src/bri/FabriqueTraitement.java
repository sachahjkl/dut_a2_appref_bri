package bri;

import java.net.Socket;

public class FabriqueTraitement {

	public static Traitement make(String type, Socket socket) throws ClassNotFoundException {
		Traitement t = null;
		switch (type) {
		case "AMA":
			t = new TraitementAma(socket);
			break;
		case "PROG":
			t = new TraitementProg(socket);
			break;
		default:
			throw new ClassNotFoundException("Type de traitement inexistant");
		}
		return t;
	}

}
