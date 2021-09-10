package fr.rowlaxx.jsavon.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import fr.rowlaxx.jsavon.annotations.object.JOValue;
import fr.rowlaxx.jsavon.exceptions.JSavONException;
import fr.rowlaxx.jsavon.interfaces.JOValueRetreiver;
import fr.rowlaxx.jsavon.utils.ConvertUtils;

public class DefaultJOValueRetreiver implements JOValueRetreiver {

	//Instance
	public static final DefaultJOValueRetreiver INSTANCE = new DefaultJOValueRetreiver();
	
	//Methodes reecrites
	@SuppressWarnings("unchecked")
	@Override
	public final <T> T getValue(JSONObject root, Field field) {
		final JOValue jovalue = field.getAnnotation(JOValue.class);
		
		final String[] paths = (jovalue.path().length == 0) ? new String[]{""} : jovalue.path();
		final String[] keys = (jovalue.key().length == 0) ? new String[] {field.getName()} : jovalue.key();
		
		JSONObject json;
		Object object;
						
		for (String path : paths ) {
			json = goToPath(root, path);
			
			for (String key : keys)
				try{
					object = get(json, key);
					object = convert(object, field.getGenericType(), field);
					if (object == null)
						continue;
					return (T) object;
				}catch(NullPointerException | JSONException e) {
					continue;
				}
		}
		
		final T value = getDefaultValue(root, field);
		if (value != null)
			return value;
		if (jovalue.mandatory())
			throw new JSavONException("Unable to find a value for the field \"" + field.getName() + "\".");
		return null;
	}
	
	private final static Object get(JSONObject root, String path) {
		final String[] pathsTemp = path.split("/");
		final List<String> paths = new ArrayList<>();
		for (String s : pathsTemp)
			if (!s.isBlank())
				paths.add(s);
		
		for (int i = 0 ; i < paths.size() - 1 ; i++)
			root = root.getJSONObject( paths.get(i) );
		
		return root.get( paths.get(paths.size()-1));
	}
	
	private final static JSONObject goToPath(JSONObject root, String path) {
		if (path == null)
			return root;
		for (String subPath : path.split("/"))
			if (!subPath.isBlank())
				root = root.getJSONObject(subPath);
		return root;
	}
	
	//Methodess default
	protected <T> T getDefaultValue(JSONObject json, Field field) {
		return null;
	}
	
	protected <T> T convert(Object object, Type type, AnnotatedElement element) {
		return ConvertUtils.convert(object, type, element);
	}
}