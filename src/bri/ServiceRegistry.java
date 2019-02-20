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

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new Vector<Class<?>>();
	}

	private static List<Class<?>> servicesClasses;

	// ajoute une classe de service après contrôle de la norme BLTi
	public static void addService(Class<? extends Runnable> service) throws InvalidClassException {
		if (checkServiceBRI(service))
			servicesClasses.add(service);
	}

	public static void updateService(Class<? extends Runnable> updated, int numService)
			throws InvalidClassException, ClassNotFoundException {
		Class<?> toUpdate;
		try {
			toUpdate = getServiceClass(numService);
		} catch (IndexOutOfBoundsException e) {
			throw new ClassNotFoundException("Classe à mettre à jour introuvable");
		}
		servicesClasses.remove(toUpdate);
		addService(updated);
	}

	// renvoie la classe de service (numService -1)
	public static Class<?> getServiceClass(int numService) {
		return servicesClasses.get(numService - 1);
	}

	// liste les activités présentes
	public static String toStringue() {
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

	public static boolean checkServiceBRI(Class<? extends Runnable> s) throws InvalidClassException {
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
