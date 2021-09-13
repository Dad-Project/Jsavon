package fr.rowlaxx.jsavon.convert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
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
import fr.rowlaxx.jsavon.exceptions.JSavONException;
import fr.rowlaxx.jsavon.utils.IterableArray;
import fr.rowlaxx.jsavon.utils.ReflectionUtils;

class ConvertUtils {

	@SuppressWarnings("unchecked")
	static final <T> T convert(Object object, Destination<T> destination) {
		Objects.requireNonNull(destination, "destination may not be null.");
		if (object == null)
			return null;
		
		if (destination.is(object.getClass()))
			return (T) object;
		
		if (ReflectionUtils.isPrimitive(destination.getDestinationClass()) )
			return (T) convertToPrimitiv(object, destination );
		else if (destination.is(String.class))
			return (T) convertToString(object);
		else if (destination.getDestinationClass().isEnum())
			return (T) convertToEnum(object, destination);
		else if (destination.is(Class.class))
			return (T) convertToClass(object);
		else if (destination.is(List.class))
			return (T) convertToList(object, destination.getGenericParameters()[0]);
		else if (destination.is(Set.class))
			return (T) convertToSet(object, destination.getGenericParameters()[0]);
		else if (destination.is(Map.class)) {
			if (destination instanceof MapDestination)
				return (T) convertToMap(object, ((MapDestination<?,?>) destination).getStringMapKey(), destination.getGenericParameter(0), destination.getGenericParameter(1));
			else
				throw new JSavONException("Map conversion must be done with the MapDestination object.");
		}
		else if (JSavONObject.class.isAssignableFrom(destination.getDestinationClass()))
			return instanciate(destination, object);
		else if (JSavONArray.class.isAssignableFrom(destination.getDestinationClass()))
			return instanciate(destination, object);
		
		throw new IllegalArgumentException("Unknow destination : " + object.getClass() + " -> " + destination);
	}
	
	static final String convertToString(Object object) {
		if (object instanceof Number)
			return String.format("%.8f", object);
		return object.toString();
	}
	
	static final Class<?> convertToClass(Object object) {
		if (object instanceof Class<?>)
			return (Class<?>) object;
		final String str = convertToString(object);
		try {
			return Class.forName(str);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	static final Object convertToPrimitiv(Object object, Destination<?> destination) {
		if (ReflectionUtils.isPrimitive(object.getClass()))
			return destination.getDestinationClass().cast(object);
		if (object instanceof String)
			return ReflectionUtils.valueOf(destination.getDestinationClass(), object.toString());
		throw new IllegalArgumentException("Cannot convert this object to " + destination);
	}
	
	static final Object convertToEnum(Object object, Destination<?> destination) {
		if (object instanceof String) {
			
			//On regarde pour l'annotation ValueMatcher
			EnumMatcher enumMatcher;
			for (Field field : destination.getDestinationClass().getDeclaredFields()) {
				if (!field.isEnumConstant())
					continue;
				if ( (enumMatcher = field.getDeclaredAnnotation(EnumMatcher.class)) != null ) {
					for (String possibleName : enumMatcher.possibleMatchs())
						if (	(enumMatcher.caseSensitiv()  && possibleName.equals(object.toString())) ||
								(!enumMatcher.caseSensitiv() && possibleName.equalsIgnoreCase(object.toString())))
							try {
								return field.get(null);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
				}
			}
			
			//On regarde pour la methode toString
			final Object[] values = destination.getDestinationClass().getEnumConstants();
			for (Object value : values) {
				if (value.toString().equals(object))
					return value;
			}
			
			//On regarde pour la méthode name
			for (Object value : values) {
				if ( ((Enum<?>)value).name().equals(object))
					return value;
			}
		}
		throw new IllegalArgumentException("Cannot convert " + object + " to a " + destination);
	}

	static final <T> void addAll(Collection<T> collection, Destination<T> destination, Object toAdd) {
		if (toAdd instanceof Object[])
			toAdd = new IterableArray<Object>( (Object[])toAdd );
		if ( !(toAdd instanceof Iterable<?>) )
			throw new IllegalArgumentException("toAdd must be an iterable object.");

		for (Object obj : (Iterable<?>)toAdd)
			collection.add( convert(obj, destination) );
	}
	
	static final <T> List<T> convertToList(Object object, Destination<T> destination) {
		final List<T> list = new ArrayList<>();
		addAll(list, destination, object);
		return Collections.unmodifiableList(list);
	}
	
	static final <T> Set<T> convertToSet(Object object, Destination<T> destination) {
		final Set<T> set = new HashSet<>();
		addAll(set, destination, object);
		return Collections.unmodifiableSet(set);
	}
	
	static final <T> T instanciate(Destination<T> destination, Object... args) {
		try {
			final Class<?>[] classes = new Class<?>[args.length];
			for (int i = 0 ; i < classes.length ; i++)
				classes[i] = args[i].getClass();
			
			final Constructor<T> constructor = destination.getDestinationClass().getConstructor(classes);
			return constructor.newInstance(args);
			
		} catch (NoSuchMethodException e) {
			throw new JSavONException("No constructor has been found.");
		} catch (InstantiationException e) {
			throw new JSavONException("The desired return type is abstract.");
		} catch (IllegalAccessException e) {
			throw new JSavONException("The constructor can not be accessed.");
		} catch (InvocationTargetException e) {
			throw new JSavONException(e.getCause());
		} 
	}
	
	static final <K, V> Map<K, V> convertToMap(Object object, String keyFieldName, Destination<K> keyDestination, Destination<V> valueDestination) {
		Objects.requireNonNull(keyFieldName, "keyFieldName may not be null.");
		try {
			final Map<K, V> map = new HashMap<>();
			final Field field = ReflectionUtils.getDeclaredField(keyFieldName, valueDestination.getDestinationClass());
			field.setAccessible(true);
			
			K key;
			for (V value : convertToList(object, valueDestination)) {
				key = convert(field.get(value), keyDestination);
				map.put(key, value);
			}
			
			return Collections.unmodifiableMap(map);
		} catch (IllegalAccessException e) {
			throw new JSavONException("cannot access the field " + keyFieldName);
		} 
	}
}
