package fr.rowlaxx.jsavon.interfaces;

import java.lang.reflect.Field;
import java.util.regex.Matcher;

import org.json.JSONObject;

import fr.rowlaxx.jsavon.JSavONException;
import fr.rowlaxx.jsavon.annotations.object.JOValue;
import fr.rowlaxx.jsavon.utils.ConvertUtils;

public class DefaultJOValueRetreiver implements JOValueRetreiver {

	//Methodes reecrites
	@Override
	@SuppressWarnings("unchecked")
	public final <T> T getValue(JSONObject root, Field field) {
		final JOValue jovalue = field.getAnnotation(JOValue.class);
		final Class<T> returnType = (Class<T>) field.getType();
		//TODO prendre en compte les arguments generiques
		
		JSONObject json;
		Object object;
		for (String path : jovalue.path() ) {
			json = goToPath(root, path);
			for (String key : jovalue.key()) {
				object = json.opt(key);
				if (object == null)
					continue;
				
				return ConvertUtils.convert(object, returnType);
			}
		}
		
		final T value = getDefaultValue(root, field, returnType);
		if (value != null)
			return value;
		if (jovalue.mandatory())
			throw new JSavONException("Unable to find a value for the field \"" + field.getName() + "\".");
		return null;
	}
	
	private final static JSONObject goToPath(JSONObject root, String path) {
		if (path.isBlank() || path.equals("/"))
			return root;
		if (path.startsWith("/"))
			path = path.substring(1);
		final String[] subPaths =  path.split(Matcher.quoteReplacement("/"));
		for (String subPath : subPaths)
			root = root.getJSONObject(subPath);
		return root;
	}
	
	//Methodess default
	protected <T> T getDefaultValue(JSONObject json, Field field, Class<T> type) {
		return null;
	}
}