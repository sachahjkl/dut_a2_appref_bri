package bri;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import users.Programmer;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesDemarres = new Vector<>();
		servicesArretes = new Vector<>();
		users = new Vector<>();
		try {
			registerProgrammer("test", "test", "localhost:2121");
			registerProgrammer("test1", "test", "localhost:2121");
			//addService(test.ServiceInversion.class);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private static List<Class<? extends Service>> servicesDemarres;
	private static List<Class<? extends Service>> servicesArretes;
	private static List<User> users;

	// ajoute une classe de service après contrôle de la norme BLTi
	@SuppressWarnings("unchecked")
	public static boolean addService(Class<?> service) throws InvalidClassException {
		if (checkServiceBRI(service)) {
			if (contains(service))
				throw new InvalidClassException("Service déjà présent ou non conforme BRI.");
			return servicesDemarres.add((Class<? extends Service>) service);
		}
		return false;
	}

	public static boolean arreteService(int numService, User u) {
		int i = 0;
		for (Class<? extends Service> c : servicesDemarres) {
			if (c.getPackageName().equals(u.getLogin())) {
				i++;
				if (i == numService) {
					servicesDemarres.remove(c);
					return servicesArretes.add(c);
				}
			}
		}
		return false;
	}

	public static boolean demarrerService(int numService, User u) {
		int i = 0;
		for (Class<? extends Service> c : servicesArretes) {
			if (c.getPackageName().equals(u.getLogin())) {
				i++;
				if (i == numService) {
					servicesArretes.remove(c);
					return servicesDemarres.add(c);
				}
			}
		}
		return false;
	}

	public static boolean updateService(int numService, Class<?> updated, User u)
			throws InvalidClassException, ClassNotFoundException {
		int i = 0;
		for (Class<? extends Service> c : servicesDemarres) {
			if (c.getPackageName().equals(u.getLogin())) {
				++i;
				if (i == numService) {
					removeService(c);
					return addService(updated);
				}
			}
		}
		for (Class<? extends Service> c : servicesArretes) {
			if (c.getPackageName().equals(u.getLogin())) {
				i++;
				if (i == numService) {
					removeService(c);
					return addService(updated);
				}
			}
		}
		return false;
	}

	public static boolean removeService(Class<? extends Service> s) {
		boolean b = servicesDemarres.remove(s);
		return servicesArretes.remove(s) || b;
	}

	private static boolean contains(Class<?> service) {
		return servicesArretes.contains(service) || servicesDemarres.contains(service);
	}

	// renvoie la classe de service (numService -1)
	public static Class<? extends Service> getServiceClass(int numService) {
		return servicesDemarres.get(numService - 1);
	}

	public static String[] getServicesDemarres() {
		if (servicesDemarres.isEmpty())
			return new String[] {};
		String nom;
		List<String> al = new ArrayList<String>();
		synchronized (servicesDemarres) {
			for (Class<? extends Service> c : servicesDemarres) {
				nom = c.getSimpleName();
				try {
					nom = (String) c.getMethod("toStringue").invoke(null, new Object[] {});
				} catch (Exception e) {
					e.printStackTrace();
				}
				al.add("'" + nom + "' de " + c.getPackageName().toUpperCase());
			}
		}
		return al.toArray(new String[al.size()]);
	}

	public static boolean checkServiceBRI(Class<?> service) throws InvalidClassException {
		if (!Arrays.asList(service.getInterfaces()).contains(Service.class))
			throw new InvalidClassException("n'implémente pas L'interface BRi.Service");
		if (Modifier.isAbstract(service.getModifiers()))
			throw new InvalidClassException("est abstract");
		if (!Modifier.isPublic(service.getModifiers()))
			throw new InvalidClassException("n'est pas publique");
		Constructor<?> c = null;
		try {
			c = service.getConstructor(Socket.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new InvalidClassException("PAS DE CONSTRUCTEUR RESPECTANT LA NORME.");
		}
		if (!(Modifier.isPublic(c.getModifiers()) && c.getExceptionTypes().length == 0))
			throw new InvalidClassException("PAS DE CONSTRUCTEUR RESPECTANT LA NORME.");
		if (!(containsPrivateSocket(service.getDeclaredFields())))
			throw new InvalidClassException("N'as pas d'attribut socket private final");
		Method m = null;
		try {
			m = service.getMethod("toStringue");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new InvalidClassException("PAS DE TOSTRINGUE RESPECTANT LA NORME.");
		}
		if (!(Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())
				&& m.getExceptionTypes().length == 0 && m.getReturnType().equals(String.class)))
			throw new InvalidClassException("PAS DE TOSTRINGUE RESPECTANT LA NORME.");
		return true;
	}

	private static boolean containsPrivateSocket(Field[] fields) {
		for (Field aField : fields) {
			if (Modifier.isPrivate(aField.getModifiers()) && aField.getType().equals(Socket.class))
				return true;
		}
		return false;
	}

	// ----------------------<Partie 'Programmer'>

	public static void registerProgrammer(String login, String pwd, String FTPAddress) throws Exception {
		login = login.toLowerCase();
		if (exists(login))
			throw new Exception("Nom d'utilisateur déjà existant");
		Programmer p = new Programmer(login, pwd, FTPAddress);
		users.add(p);
		System.out.println("log : programmeur créé " + p.getLogin());
	}

	public static User login(String login, String pwd) {
		login = login.toLowerCase();
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

	public static Class<? extends Service> getServicesClass(int numService, User u) {
		int i = 0;
		for (Class<? extends Service> c : servicesDemarres) {
			if (c.getPackageName().equals(u.getLogin())) {
				++i;
				if (numService == i)
					return c;
			}

		}
		for (Class<? extends Service> c : servicesArretes) {
			if (c.getPackageName().equals(u.getLogin())) {
				++i;
				if (numService == i)
					return c;
			}
		}
		return null;
	}

	public static String[] getServices(User u) {
		List<String> al = new ArrayList<String>();
		for (Class<? extends Service> c : servicesDemarres) {
			if (c.getPackageName().equals(u.getLogin()))
				al.add(c.getSimpleName());

		}
		for (Class<? extends Service> c : servicesArretes) {
			if (c.getPackageName().equals(u.getLogin()))
				al.add(c.getSimpleName());
		}
		return al.toArray(new String[al.size()]);
	}

	public static String[] getServicesDemarres(User u) {
		String nom;
		List<String> al = new ArrayList<String>();
		for (Class<? extends Service> c : servicesDemarres) {
			if (c.getPackageName().equals(u.getLogin())) {
				nom = c.getSimpleName();
				try {
					nom = (String) c.getMethod("toStringue").invoke(null, new Object[] {});
				} catch (Exception e) {
				}
				al.add(nom);
			}
		}
		return al.toArray(new String[al.size()]);
	}

	public static String[] getServicesArretes(User u) {
		List<String> al = new ArrayList<String>();
		for (Class<? extends Service> c : servicesArretes) {
			if (c.getPackageName().equals(u.getLogin()))
				al.add(c.getSimpleName());
		}
		return al.toArray(new String[al.size()]);
	}

}
