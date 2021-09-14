package fr.rowlaxx.jsavon.interfaces;

import java.lang.reflect.Field;

import org.json.JSONObject;

import fr.rowlaxx.jsavon.JSavONObject;

public interface JOValueRetreiver {
	
	public <T> T getValue(final JSavONObject instance, final JSONObject json, final Field field);

}
