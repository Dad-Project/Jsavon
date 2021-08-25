package fr.rowlaxx.jsavon.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.json.JSONArray;

import fr.rowlaxx.jsavon.JSavONArray;
import fr.rowlaxx.jsavon.JSavONException;
import fr.rowlaxx.jsavon.JSavONObject;
import fr.rowlaxx.jsavon.annotations.EnumMatcher;

public class ConvertUtils {

	@SuppressWarnings("unchecked")
	public static final <T> T convert(Object object, Class<T> destination) {
		if (object == null)
			return null;
		if (object.getClass() == destination)
			return (T) object;
		
		
		if (destination == String.class)
			return (T) convertToString(object);
		if (ReflectionUtils.isPrimitive(destination))
			return convertToPrimitiv(object, destination);
		if (destination.isEnum())
			return convertToEnum(object, (Class<T>) destination);
		if (destination.isAssignableFrom(List.class))
			return (T) convertToList(object);
		if (destination.isAssignableFrom(Set.class))
			return (T) convertToSet(object);
		if (destination.isAssignableFrom(Map.class))
			return (T) convertToMap(object);
		if (destination.isAssignableFrom(JSavONObject.class) || destination.isAssignableFrom(JSavONArray.class))
			return instanciate(destination, object);
		
		try {
			return instanciate(destination, object);
		}catch(JSavONException e) {
			throw new IllegalArgumentException("Unknow destination.");
		}
	}
	
	public static final String convertToString(Object object) {
		if (object == null)
			return null;
		if (object instanceof Number)
			return String.format("%.8f", object);
		return object.toString();
	}
	
	public static final Class<?> convertToClass(Object object) {
		if (object == null)
			return null;
		if (object instanceof Class<?>)
			return (Class<?>) object;
		final String str = convertToString(object);
		try {
			return Class.forName(str);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T convertToPrimitiv(Object object, Class<T> destination) {
		Objects.requireNonNull(object, "object may noy be null.");
		if (ReflectionUtils.isPrimitive(object.getClass()))
			return destination.cast(object);
		if (object instanceof String)
			return (T) ReflectionUtils.valueOf(destination, object.toString());
		throw new IllegalArgumentException("Cannot convert this object to a " + destination.getSimpleName() + ".");
	}
	
	public static final boolean convertToBoolean(Object object) {
		return convertToPrimitiv(object, Boolean.class);
	}
	
	public static final double convertToDouble(Object object) {
		return convertToPrimitiv(object, Double.class);
	}
	
	public static final double convertToFloat(Object object) {
		return convertToPrimitiv(object, Float.class);
	}
	
	public static final long convertToLong(Object object) {
		return convertToPrimitiv(object, Long.class);
	}
	
	public static final int convertToInteger(Object object) {
		return convertToPrimitiv(object, Integer.class);
	}
	
	public static final int convertToShort(Object object) {
		return convertToPrimitiv(object, Short.class);
	}
	
	public static final int convertToByte(Object object) {
		return convertToPrimitiv(object, Byte.class);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T convertToEnum(Object object, Class<T> clazz ) {
		if (!clazz.isEnum())
			throw new IllegalArgumentException("clazz is not an enum.");
		if (object == null)
			return null;
		if (object instanceof String) {
			
			//On regarde pour l'annotation ValueMatcher
			EnumMatcher enumMatcher;
			for (Field field : clazz.getDeclaredFields()) {
				if (!field.isEnumConstant())
					continue;
				if ( (enumMatcher = field.getDeclaredAnnotation(EnumMatcher.class)) != null ) {
					for (String possibleName : enumMatcher.possibleMatchs())
						if (	(enumMatcher.caseSensitiv()  && possibleName.equals(object.toString())) ||
								(!enumMatcher.caseSensitiv() && possibleName.equalsIgnoreCase(object.toString())))
							try {
								return (T) field.get(null);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
				}
			}
			
			//On regarde pour la methode toString
			final T[] values = clazz.getEnumConstants();
			for (T value : values) {
				if (value.toString().equals(object))
					return value;
			}
			
			//On regarde pour la méthode name
			for (T value : values) {
				if ( ((Enum<?>)value).name().equals(object))
					return value;
			}
		}
		throw new IllegalArgumentException("Cannot convert this object to a " + clazz.getSimpleName() + ".");
	}
	
	public static final <T> T instanciate(Class<T> destination, Object... args) {
		try {
			final Class<?>[] classes = new Class<?>[args.length];
			for (int i = 0 ; i < classes.length ; i++)
				classes[i] = args[i].getClass();
			
			final Constructor<T> constructor = destination.getConstructor(classes);
			return constructor.newInstance(args);
			
		} catch (NoSuchMethodException e) {
			throw new JSavONException("No constructor has been found.");
		} catch (InstantiationException e) {
			throw new JSavONException("The desired return type is abstract.");
		} catch (IllegalAccessException e) {
			throw new JSavONException("The constructor can not be accessed.");
		} catch (InvocationTargetException e) {
			e.getCause().printStackTrace();
			throw new JSavONException("Unable to instanciate the object.");
		} 
	}
	
	public static final <T> List<T> convertToList(Object object, Class<T> destination) {
		if (object == null)
			return null;
		
		if (object instanceof JSONArray)
			return convertToList((JSONArray)object, destination);
		if (object instanceof Collection<?>)
	}
	
	public static final <T> List<T> convertToList(JSONArray array, Class<T> destination) {
		final ArrayList<T> list = new ArrayList<>(array.length());
		
		for (int i = 0 ; i < array.length() ; i++)
			list.add( convert(array.get(i), destination) );
		
		return list;
	}
	
	public static final <T> List<T> ConvertToList(JSONArray array, Class<T> destination){
		
	}
	
	public static final Set<T> convertToSet(Object object) {
		
	}
	
	public static final Map<K, V> convertToMap(Object object) {
		
	}
}
