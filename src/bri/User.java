package bri;

import java.io.InvalidClassException;
import java.io.Serializable;

public interface User extends Serializable {

	boolean login(String login, String pwd);
	
	String getLogin();

	boolean checkPwd(String pwd);

	void addService(Class<? extends Service> service) throws InvalidClassException;

	void updateService(int numService, Class<? extends Service> updated)
			throws InvalidClassException, ClassNotFoundException;

	void stopService(int numService);

	String toStringue();

	Class<? extends Service>[] getService();

}
