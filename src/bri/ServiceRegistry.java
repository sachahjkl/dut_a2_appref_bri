package bri;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import users.Programmer;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new Vector<>();
		users = new Vector<>();
	}

	private static List<Class<? extends Service>> servicesClasses;
	private static List<User> users;

	// ajoute une classe de service après contrôle de la norme BLTi
	public static boolean addService(Class<? extends Service> service) throws InvalidClassException {
		if (checkServiceBRI(service))
			return servicesClasses.add(service);
		return false;
	}

	public static boolean updateService(int numService, Class<? extends Service> updated)
			throws InvalidClassException, ClassNotFoundException {
		Class<?> toUpdate;
		try {
			toUpdate = getServiceClass(numService - 1);
		} catch (IndexOutOfBoundsException e) {
			throw new ClassNotFoundException("Classe à mettre à jour introuvable");
		}
		servicesClasses.remove(toUpdate);
		return addService(updated);
	}

	// renvoie la classe de service (numService -1)
	public static Class<?> getServiceClass(int numService) {
		return servicesClasses.get(numService - 1);
	}

	// liste les activités présentes
	public static String toStringue() {
		if (servicesClasses.isEmpty())
			return "Aucune activité";
		StringBuilder result = new StringBuilder("Activités présentes :\n");
		int i = 1;
		synchronized (ServiceRegistry.class) {
			for (Class<?> r : servicesClasses) {
				result.append(i + " " + r.toString() + "\n");
				i++;
			}
		}
		return result.toString();
	}

	public static boolean removeService(Class<? extends Service> s) {
		
		return false;
	}

	public static boolean checkServiceBRI(Class<? extends Service> s) throws InvalidClassException {
		if (!Arrays.asList(s.getInterfaces()).contains(Service.class))
			throw new InvalidClassException("n'implémente pas L'interface BRi.Service");
		if (!Modifier.isAbstract(s.getModifiers()))
			throw new InvalidClassException("n'est pas abstract");
		if (Modifier.isPublic(s.getModifiers()))
			throw new InvalidClassException("est publique");
		Constructor<? extends Runnable> c = null;
		try {
			c = s.getConstructor(Socket.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new InvalidClassException("PAS DE CONSTRUCTEUR RESPECTANT LA NORME.");
		}
		if (!(Modifier.isPublic(c.getModifiers()) && c.getExceptionTypes().length == 0))
			throw new InvalidClassException("PAS DE CONSTRUCTEUR RESPECTANT LA NORME.");
		if (!(containsPrivateSocket(s.getDeclaredFields())))
			throw new InvalidClassException("PAS DE CONSTRUCTEUR RESPECTANT LA NORME.");
		Method m = null;
		try {
			m = s.getMethod("toStringue");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new InvalidClassException("PAS DE TOSTRINGUE RESPECTANT LA NORME.");
		}
		if (!(Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())
				&& m.getExceptionTypes().length == 0 && m.getReturnType().equals(String.class)))
			throw new InvalidClassException("PAS DE TOSTRINGUE RESPECTANT LA NORME.");
		return true;
	}

	private static boolean containsPrivateSocket(Field[] fields) {
		boolean b = false;
		for (Field aField : fields) {
			if (Modifier.isPrivate(aField.getModifiers()) && aField.getClass().equals(Socket.class)) {
				b = true;
				break;
			}
		}
		return b;
	}
}
