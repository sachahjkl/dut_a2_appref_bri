package bri;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.net.URL;

public interface User extends Serializable {

	boolean login(String login, String pwd);

	String getLogin();

	boolean checkPwd(String pwd);

	void addService(Class<? extends Service> service) throws InvalidClassException;

	String toStringue();

	URL getFTPAddress();

}
