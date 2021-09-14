package fr.rowlaxx.jsavon.impl;

import java.lang.reflect.Field;
import org.json.JSONException;
import org.json.JSONObject;

import fr.rowlaxx.jsavon.JSavONObject;
import fr.rowlaxx.jsavon.annotations.object.JOValue;
import fr.rowlaxx.jsavon.convert.ConvertRequest;
import fr.rowlaxx.jsavon.convert.Destination;
import fr.rowlaxx.jsavon.convert.DestinationResolver;
import fr.rowlaxx.jsavon.exceptions.JSavONException;
import fr.rowlaxx.jsavon.interfaces.JOValueRetreiver;

public class DefaultJOValueRetriever implements JOValueRetreiver {

	//Instance
	public static final DefaultJOValueRetriever INSTANCE = new DefaultJOValueRetriever();
	
	//Methodes reecrites
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(final JSavONObject instance, final JSONObject root, final Field field) {
		final JOValue jovalue = field.getAnnotation(JOValue.class);
		
		String[] paths, keys;
		
		if (jovalue == null) {
			paths = new String[] {""};
			keys = new String[] {field.getName()};
		}
		else {
			paths = jovalue.path().length == 0 ? new String[]{""} : jovalue.path();
			keys = jovalue.key().length == 0 ? new String[] {field.getName()} : jovalue.key();
		}
		
		final Destination<T> destination = (Destination<T>) DestinationResolver.resolve(field, instance);
		ConvertRequest<T> request;
		JSONObject json;
		Object object;
						
		for (String path : paths ) {
			json = goToPath(root, path);
			
			for (String key : keys)
				try{
					object = get(json, key);
					request = new ConvertRequest<>(object, destination);
					return request.execute();
				}catch(JSONException e) {//Si la méthode get(json, key) ne donne rien
					continue;
				}
		}
		
		if (jovalue.mandatory())
			throw new JSavONException("Unable to find a value for the field \"" + field.getName() + "\".");
		return null;
	}
	
	private final static Object get(JSONObject root, String fullPath) {
		final String[] paths = fullPath.split("/");
		JSONObject obj = root;
		
		for (int i = 0 ; i < paths.length-1 ; i++)
			if (!paths[i].isBlank())
				obj = obj.getJSONObject(paths[i]);
		
		return obj.get(paths[paths.length-1]);
	}
	
	private final static JSONObject goToPath(JSONObject root, String path) {
		if (path == null)
			return root;
		for (String subPath : path.split("/"))
			if (!subPath.isBlank())
				root = root.getJSONObject(subPath);
		return root;
	}
}