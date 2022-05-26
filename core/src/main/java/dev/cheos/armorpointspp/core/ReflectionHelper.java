package dev.cheos.armorpointspp.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionHelper {
	/**
	 * <class name>#<field name>
	 */
	public static <T, V> void setPrivateValue(String identifier, V value) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setPrivateValue(identifier, null, value);
	}
	
	/**
	 * <class name>#<field name>
	 */
	public static <T, V> void setPrivateValue(String identifier, T of, V value) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String[] parts = identifier.split("#", 2);
		setPrivateValue(parts[0], parts[1], of, value);
	}
	
	public static <T, V> void setPrivateValue(String clazz, String name, V value) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setPrivateValue(clazz, name, null, value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, V> void setPrivateValue(String clazz, String name, T of, V value) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setPrivateValue((Class<T>) Class.forName(clazz), name, of, value);
	}
	
	public static <T, V> void setPrivateValue(Class<T> clazz, String name, V value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setPrivateValue(clazz, name, null, value);
	}
	
	public static <T, V> void setPrivateValue(Class<T> clazz, String name, T of, V value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = findField(clazz, name);
		boolean fin = isFinal(field);
		if (fin) unfinalize(field);
		field.set(of, value);
		if (fin) finalize(field);
	}
	
	/**
	 * <class name>#<field name>
	 */
	public static <T, V> V getPrivateValue(String identifier) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getPrivateValue(identifier, null);
	}
	
	/**
	 * <class name>#<field name>
	 */
	public static <T, V> V getPrivateValue(String identifier, T of) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String[] parts = identifier.split("#", 2);
		return getPrivateValue(parts[0], parts[1], of);
	}
	
	public static <T, V> V getPrivateValue(String clazz, String name) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getPrivateValue(clazz, name, null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, V> V getPrivateValue(String clazz, String name, T of) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getPrivateValue((Class<T>) Class.forName(clazz), name, of);
	}
	
	public static <T, V> V getPrivateValue(Class<T> clazz, String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getPrivateValue(clazz, name, null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, V> V getPrivateValue(Class<T> clazz, String name, T of) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = findField(clazz, name);
		return (V) field.get(of);
	}
	
	public static <T, V> V getPrivateValueDirect(Class<T> clazz, String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getPrivateValueDirect(clazz, name, null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, V> V getPrivateValueDirect(Class<T> clazz, String name, T of) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		return (V) field.get(of);
	}
	
	public static Field findField(Class<?> clazz, String name) throws NoSuchFieldException, SecurityException {
		NoSuchFieldException ex = null;
		Field field = null;
		while (field == null)
			try {
				field = clazz.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				if (ex == null)
					ex = e;
				if (clazz == Object.class)
					throw ex;
				clazz = clazz.getSuperclass();
			}
		// no chance this is null now - if the field couldn't be found, an exception will be thrown
		field.setAccessible(true);
		return field;
	}
	
	public static Field unfinalize(Field field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field modifiers = findField(Field.class, "modifiers");
		modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		return field;
	}
	
	public static Field finalize(Field field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field modifiers = findField(Field.class, "modifiers");
		modifiers.setInt(field, field.getModifiers() | Modifier.FINAL);
		return field;
	}
	
	public static boolean isFinal(Field field) {
		return (field.getModifiers() & Modifier.FINAL) != 0;
	}
}
