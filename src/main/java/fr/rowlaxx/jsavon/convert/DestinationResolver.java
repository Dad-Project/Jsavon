package fr.rowlaxx.jsavon.convert;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import fr.rowlaxx.jsavon.annotations.MapKey;

public class DestinationResolver {
	
	public static <T> Destination<T> resolve(Field field, Object instance) {
		return convert( resolveType(field.getGenericType(), instance), field );
	}
	
	public static <T> Destination<T> resolve(Method method) {
		return convert( resolveType(method.getGenericReturnType(), null), method);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Destination<T> convert(Destination<T> destination, AnnotatedElement element){
		final MapKey mapKey = element.getAnnotation(MapKey.class);
		if (mapKey == null)
			return destination;
		if (!destination.is(Map.class))
			throw new IllegalArgumentException("Cannot convert " + destination + " to a map.");
		
		final Destination<?> key = destination.getGenericParameter(0);
		final Destination<?> value = destination.getGenericParameter(1);
		return (Destination<T>) new MapDestination<>(mapKey, key, value);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Destination<T> resolveType(Type type, Object instance) {
		if (type instanceof Class)
			return new Destination<T>( (Class<T>) type );
		else if (type instanceof ParameterizedType) {
			final Class<T> raw = resolveRawClass(((ParameterizedType) type).getRawType(), instance);
			final Type[] types = ((ParameterizedType) type).getActualTypeArguments();
			final Destination<?>[] destinations = new Destination<?>[types.length];
			for (int i = 0 ; i < types.length ; i++)
				destinations[i] = resolveType(types[i], instance);
			return new Destination<T>(raw, destinations);
		}
		else if (type instanceof TypeVariable<?>)
			return resolveTypeVariable((TypeVariable<?>) type, instance);
		throw new IllegalArgumentException();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Class<T> resolveRawClass(Type type, Object instance){
		if (type instanceof Class)
			return (Class<T>) type;
		else if (type instanceof TypeVariable<?>)
			return (Class<T>) resolveTypeVariable((TypeVariable<?>) type, instance).getDestinationClass();
		throw new IllegalArgumentException();
	}
	
	private static <T> Destination<T> resolveTypeVariable(TypeVariable<?> type, Object instance) {
		final Class<?> parent = getParent(instance, (Class<?>) type.getGenericDeclaration());
		final int index = indexOf(type);
		final Type superType = parent.getGenericSuperclass();
		
		if (superType instanceof ParameterizedType)
			return resolveType(((ParameterizedType) superType).getActualTypeArguments()[index], instance);

		throw new IllegalArgumentException();
	}
	
	private static Class<?> getParent(Object instance, Class<?> child) {
		Class<?> clazz = instance.getClass();
		while (clazz.getSuperclass() != child)
			clazz = clazz.getSuperclass();
		return clazz;
	}
	
	private static int indexOf(TypeVariable<?> type) {
		final TypeVariable<?>[] types = type.getGenericDeclaration().getTypeParameters();
		for (int i = 0 ; i < types.length ; i++)
			if (types[i] == type)
				return i;
		throw new NullPointerException();
	}
}