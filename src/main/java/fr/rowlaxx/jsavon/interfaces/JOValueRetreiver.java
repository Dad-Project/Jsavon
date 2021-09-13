package fr.rowlaxx.jsavon.interfaces;

import java.lang.reflect.Field;

import org.json.JSONObject;

public interface JOValueRetreiver {
	
	public <T> T getValue(final Object object, final JSONObject json, final Field field);

}
