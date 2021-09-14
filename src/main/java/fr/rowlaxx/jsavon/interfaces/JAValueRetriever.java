package fr.rowlaxx.jsavon.interfaces;

import java.lang.reflect.Field;

import org.json.JSONArray;

import fr.rowlaxx.jsavon.JSavONArray;

public interface JAValueRetriever {

	public <T> T getValue(final JSavONArray instance, final JSONArray array, final Field field);
	
}
