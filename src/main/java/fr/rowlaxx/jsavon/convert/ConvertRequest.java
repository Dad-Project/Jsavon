package fr.rowlaxx.jsavon.convert;

import java.util.Objects;

public class ConvertRequest<T> {
	
	//Variables
	private Object value;
	private Destination<T> destination;

	//Constructeurs
	public ConvertRequest(Object value, Destination<T> destination) {
		this.value = Objects.requireNonNull(value, "value may not be null.");
		this.destination = Objects.requireNonNull(destination, "destination may not be null.");
	}
	
	//Getter
	public Object getValue() {
		return value;
	}
	
	public Destination<T> getDestination() {
		return destination;
	}
	
	//Execute
	public T execute() {
		return ConvertUtils.convert(value, destination);
	}
	
}
