package bri;

import java.util.ArrayList;
import java.util.List;

import users.Programmer;

public class UserRegistry {
	private static List<User> users;

	static {
		users = new ArrayList<>();
		try {
			registerProgrammer("test", "test", "localhost");
			registerProgrammer("test1", "test", "localhost");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void registerProgrammer(String login, String pwd, String FTPAddress) throws Exception {
		if (exists(login))
			throw new Exception("Nom d'utilisateur déjà existant");
		Programmer p = new Programmer(login, pwd, FTPAddress);
		users.add(p);
		System.out.println("log : programmeur créé " + p.getLogin());
	}

	public static User login(String login, String pwd) {
		for (User u : users) {
			if (u.login(login, pwd))
				return u;
		}
		return null;
	}

	private static boolean exists(String login) {
		for (User u : users) {
			if (u.getLogin().equals(login))
				return true;
		}
		return false;
	}

	public static User[] getUsers() {
		return (User[]) users.toArray();
	}

}
