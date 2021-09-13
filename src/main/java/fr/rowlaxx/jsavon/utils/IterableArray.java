package fr.rowlaxx.jsavon.utils;

import java.util.Iterator;

public class IterableArray<T> implements Iterable<T> {

	//Variables
	private Object[] array;
	
	//Constructeurs
	public IterableArray(T[] array) {
		this.array = new Object[array.length];
		for (int i = 0 ; i < array.length ; i++)
			this.array[i] = array[i];
	}
	
	//Methodes
	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T) array[index];
	}
	
	public int length() {
		return array.length;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private int i = 0;
			
			@Override
			public boolean hasNext() {
				return i < length();
			}

			@Override
			public T next() {
				return get(i++);
			}
		};
	}

}
