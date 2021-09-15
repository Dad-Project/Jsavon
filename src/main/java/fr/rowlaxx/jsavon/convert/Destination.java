package fr.rowlaxx.jsavon.convert;

import java.util.Arrays;
import java.util.Objects;

import fr.rowlaxx.jsavon.utils.ReflectionUtils;

public class Destination<T> {

	//Methodes statiques
	public static <T> Destination<T> from(Class<T> clazz){
		return new Destination<T>(clazz);
	}
	
	//Builder
	public static final class Builder {
		
		private Class<?> clazz;
		private Destination<?>[] generics;
		
		public void setDestinationClass(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		public void setGenericParameters(Destination<?>[] generics) {
			this.generics = generics;
		}
		
		public Destination<?> build() {
			return new Destination<>(clazz, generics);
		}
	}
	
	//Variables
	private Class<T> clazz;
	private Destination<?>[] generics;
	
	//Constructeurs
	public Destination(Class<T> clazz, Destination<?>... generics) {
		Objects.requireNonNull(clazz, "clazz may not be null.");
		this.clazz = ReflectionUtils.toWrapper(clazz);
		final int typesCount = clazz.getTypeParameters().length;
		try {
			check(generics);
			if (typesCount != generics.length)
				throw new IllegalArgumentException("class " + clazz.getName() + " cannot have the generic parameters " + Arrays.toString(generics));
			this.generics = Arrays.copyOf(generics, generics.length);
		} catch(IllegalArgumentException e) {
			if (typesCount != 0)
				throw new IllegalArgumentException("class " + clazz.getName() + " must have " + typesCount + " generic parameters.");
			this.generics = null;
		}
	}
	
	public Destination(Class<T> clazz, Class<?>... generics) {
		this(clazz, toDestinations(generics));
	}
	
	public Destination(Class<T> clazz) {
		this(clazz, (Destination[])null);
	}
	
	private static final Destination<?>[] toDestinations(Class<?>[] classes) {
		final Destination<?>[] destinations = new Destination[classes.length];
		for (int i = 0 ; i < classes.length ; i++)
			destinations[i] = new Destination<>(classes[i]);
		return destinations;
	}
	
	private static final boolean isNull(Destination<?>[] destinations) {
		if (destinations == null)
			return true;
		if (destinations.length == 0)
			return true;
		for (Destination<?> destination : destinations)
			if (destination != null)
				return false;
		return true;
	}
	
	private static final void check(Destination<?>[] destinations) {
		if (isNull(destinations))
			throw new IllegalArgumentException();
		for (Destination<?> destination : destinations)
			if (destination == null)
				throw new NullPointerException();
	}
	
	//Getter
	public boolean is(Class<?> clazz) {
		return this.clazz == ReflectionUtils.toWrapper(clazz);
	}
	
	public Class<T> getDestinationClass(){
		return clazz;
	}
	
	public boolean hasGenericParameters() {
		return generics != null;
	}
	
	public Destination<?>[] getGenericParameters() {
		if (!hasGenericParameters())
			throw new NullPointerException();
		return Arrays.copyOf(this.generics, this.generics.length);
	}
	
	public Destination<?> getGenericParameter(int index){
		return this.generics[index];
	}
	
	@Override
	public String toString() {
		if (hasGenericParameters())
			return clazz.toString() + " " + Arrays.toString(generics);
		return clazz.toString();
	}
}