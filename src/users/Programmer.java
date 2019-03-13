package users;

import java.io.InvalidClassException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;
import java.util.Vector;

import bri.Service;
import bri.ServiceRegistry;
import bri.User;

public class Programmer implements User {

	private static final int MIN_LENGTH_LOGIN = 3, MIN_LENGTH_PWD = 4;
	private static final long serialVersionUID = 1L;
	private String login, sha256PWD;
	private URL FTPAddress;
	private List<Class<? extends Runnable>> serviceClasses;

	public Programmer(String login, String pwd, String FTPAddress) throws Exception {
		setLogin(login);
		setPwd(pwd);
		setFTPAddress(FTPAddress);
		this.serviceClasses = new Vector<Class<? extends Runnable>>();
	}

	@Override
	public boolean login(String login, String pwd) {
		return this.login.equals(login) && checkPwd(pwd);
	}

	@Override
	public boolean checkPwd(String pwd) {
		return sha256(pwd).equals(sha256PWD);
	}

	private void setLogin(String login) throws Exception {
		if (login.length() < MIN_LENGTH_LOGIN)
			throw new Exception("Login trop court (" + MIN_LENGTH_LOGIN + " caractères min).");
		this.login = login;
	}

	public void setPwd(String pwd) throws Exception {
		if (pwd.length() < MIN_LENGTH_PWD)
			throw new Exception("Mot de passe trop court (" + MIN_LENGTH_PWD + " caractères min).");
		this.sha256PWD = sha256(pwd);
	}

	public void setFTPAddress(String address) throws MalformedURLException {
		this.FTPAddress = new URL("ftp://" + address + "/");
	}

	public boolean resetPwd(String old, String neww) {
		if (checkPwd(old)) {
			sha256PWD = sha256(neww);
			return true;
		}
		return false;
	}

	public URL getFTPAddress() {
		return this.FTPAddress;
	}

	public static String sha256(String base) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void addService(Class<? extends Service> service) throws InvalidClassException {
		if (ServiceRegistry.addService(service))
			this.serviceClasses.add(service);
	}

	@Override
	public String getLogin() {
		return login;
	}

	@Override
	public String toStringue() {
		// TODO Auto-generated method stub
		return null;
	}

}
