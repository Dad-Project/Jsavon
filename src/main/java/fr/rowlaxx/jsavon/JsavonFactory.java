package fr.rowlaxx.jsavon;

import java.util.Objects;

import fr.rowlaxx.convertutils.Converter;

public class JsavonFactory {

	//Variables
	private final Converter converter;
	
	//Constructeurs
	public JsavonFactory(Converter converter) {
		this.converter = Objects.requireNonNull(converter, "converter may not be null.");
	}
	
}
