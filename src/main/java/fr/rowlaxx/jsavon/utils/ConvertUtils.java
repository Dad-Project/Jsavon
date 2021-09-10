package fr.rowlaxx.jsavon.utils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import fr.rowlaxx.jsavon.JSavONArray;
import fr.rowlaxx.jsavon.JSavONObject;
import fr.rowlaxx.jsavon.annotations.EnumMatcher;
import fr.rowlaxx.jsavon.annotations.object.JOMapKey;
import fr.rowlaxx.jsavon.exceptions.JSavONException;

public class ConvertUtils {

	@SuppressWarnings("unchecked")
	public static final <T> T convert(Object object, Type type, AnnotatedElement element) {
		Objects.requireNonNull(type, "type may not be null.");
		if (object == null)
			return null;
				
		Class<T> destination;
		Type[] types;
		if (type instanceof Class) {
			destination = (Class<T>) type;
			types = null;
		}
		else if (type instanceof ParameterizedType) {
			destination = (Class<T>) ((ParameterizedType) type).getRawType();
			types = ((ParameterizedType) type).getActualTypeArguments();
		}
		else
			throw new UnsupportedOperationException();
		
		System.out.println(object.getClass() + " -> " + destination + "   " + Arrays.toString(types));
		
		destination = ReflectionUtils.toWrapper(destination);
		if (object.getClass() == destination)
			return (T) object;
		
		
		if (destination == String.class)
			return (T) convertToString(object);
		if (ReflectionUtils.isPrimitive(destination))
			return convertToPrimitiv(object, destination);
		if (destination.isEnum())
			return convertToEnum(object, (Class<T>) destination);
		if (destination == Class.class)
			return (T) convertToClass(object);
		
		if (List.class == destination)
			return (T) convertToList(object, types[0]);
		if (Set.class == destination)
			return (T) convertToSet(object, types[0]);
		if (Map.class == destination)
			return (T) convertToMap(object, types[1], element);
		
		if (JSavONObject.class.isAssignableFrom(destination) || JSavONArray.class.isAssignableFrom(destination))
			return instanciate(destination, object);
		
		try {
			return instanciate(destination, object);
		}catch(JSavONException e) {
			throw new IllegalArgumentException("Unknow destination : " + object.getClass() + " -> " + destination);
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
		destination = ReflectionUtils.toWrapper(destination);
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
	
	public static final <T> List<T> convertToList(Object object, Type type) {
		Objects.requireNonNull(type, "type may not be null.");
		Objects.requireNonNull(object, "object may not be null.");
		
		if (object instanceof Object[] ) {
			final List<T> list = new ArrayList<>();
			for (Object obj : (Object[])object)
				list.add( convert(obj, type, null) );
			return Collections.unmodifiableList(list);
		}
		else if (object instanceof Iterable) {
			final List<T> list = new ArrayList<>();
			for (Object obj : (Iterable<?>)object)
				list.add( convert(obj, type, null) );
			return Collections.unmodifiableList(list);
		}
		
		throw new JSavONException("Cannot convert a " + object.getClass() + " to a list.");
	}
	
	public static final <T> Set<T> convertToSet(Object object, Type type) {
		final Set<T> set = new HashSet<>();
		try {
			set.addAll( convertToList(object, type) );
		}catch(JSavONException e) {
			throw new JSavONException("Cannot convert a " + object.getClass() + " to a set.");
		}
		return Collections.unmodifiableSet(set);
	}
	
	@SuppressWarnings("unchecked")
	public static final <K, V> Map<K, V> convertToMap(Object object, String keyFieldName, Type value) {
		Objects.requireNonNull(object, "object may not be null.");
		Objects.requireNonNull(value, "value may not be null.");
		Objects.requireNonNull(keyFieldName, "keyFieldName may not be null.");
				
		try {
			if (object instanceof Object[] ) {
				final Map<K, V> map = new HashMap<>();
				Object key;
				for (Object obj : (Object[])object) {
					obj = convert(obj, value, null);
					key = obj.getClass().getField(keyFieldName).get(obj);
					map.put( (K)key, (V)obj);
				}
				return Collections.unmodifiableMap(map);
			}
			else if (object instanceof Iterable) {
				final Map<K, V> map = new HashMap<>();
				Object key;
				for (Object obj : (Iterable<?>)object) {
					obj = convert(obj, value, null);
					key = obj.getClass().getField(keyFieldName).get(obj);
					map.put( (K)key, (V)obj);
				}
				return Collections.unmodifiableMap(map);
			}
		} catch( NoSuchFieldException | IllegalAccessException e) {
			throw new JSavONException(e);
		}
		
		
		throw new JSavONException("Cannot convert a " + object.getClass() + " to a map.");
	}
	
	public static final <K, V> Map<K, V> convertToMap(Object object, Type value, AnnotatedElement element) {
		if (element.isAnnotationPresent(JOMapKey.class))
			return convertToMap(object, element.getAnnotation(JOMapKey.class).fieldName(), value);
		throw new UnsupportedOperationException();
	}
}
