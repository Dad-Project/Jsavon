package fr.rowlaxx.jsavon.impl;

import java.lang.reflect.Field;

import org.json.JSONArray;

import fr.rowlaxx.jsavon.JSavONArray;
import fr.rowlaxx.jsavon.interfaces.JAValueRetriever;

public class DefaultJAValueRetriever implements JAValueRetriever {

	//Instance
	public static final DefaultJAValueRetriever INSTANCE = new DefaultJAValueRetriever();
	
	//Méthodes réecrites
	@Override
	public <T> T getValue(JSavONArray instance, JSONArray array, Field field) {
		return null;
	}

}
