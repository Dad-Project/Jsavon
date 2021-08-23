package fr.rowlaxx.jsavon.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import fr.rowlaxx.jsavon.JSavONArray;
import fr.rowlaxx.jsavon.JSavONObject;

public class ConvertUtils {

	@SuppressWarnings("unchecked")
	public static final <T> T convert(Object object, Class<T> destination) {
		if (destination == String.class)
			return (T) convertToString(object);
		if (ReflectionUtils.isPrimitive(destination))
			return convertToPrimitiv(object, destination);
		if (destination.isEnum())
			return convertToEnum(object, destination);
		if (destination.isAssignableFrom(List.class))
			return (T) convertToList(object);
		if (destination.isAssignableFrom(Set.class))
			return (T) convertToSet(object);
		if (destination.isAssignableFrom(Map.class))
			return (T) convertToMap(object);
		if (destination.isAssignableFrom(JSavONObject.class))
			return (T) convertToJSavONObject(object);
		if (destination.isAssignableFrom(JSavONArray.class))
			return (T) convertToJSavONArray(object);
		throw new IllegalArgumentException("Unknow destination.");
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
	
	public static final <T> T convertToEnum(Object object, Class<T> clazz ) {
		
	}
	
	public static final List<?> convertToList(Object object) {
		
	}
	
	public static final Set<?> convertToSet(Object object) {
		
	}
	
	public static final Map<?, ?> convertToMap(Object object) {
		
	}
	
	public static final JSavONArray convertToJSavONArray(Object object) {
		
	}
	
	public static final JSavONObject convertToJSavONObject(Object object) {
		
	}
	
}
