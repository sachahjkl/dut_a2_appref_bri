package appli;

import java.net.MalformedURLException;

import bri.ServeurBRi;

public class BRiLaunch {
	private final static int PORT_AMA = 1131;
	private final static int PORT_PROG = 1958;
	private final static String type_AMA = "AMA";
	private final static String type_PROG = "PROG";

	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
		Class.forName("bri.ServiceRegistry");
		new Thread(new ServeurBRi(PORT_AMA, type_AMA)).start();
		new Thread(new ServeurBRi(PORT_PROG, type_PROG)).start();
	}
}
