package fr.rowlaxx.jsavon;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.json.JSONObject;

import fr.rowlaxx.jsavon.annotations.JOValue;
import fr.rowlaxx.jsavon.annotations.ManualValue;
import fr.rowlaxx.utils.generic.ReflectionUtils;
import fr.rowlaxx.utils.generic.destination.Destination;

public abstract class JsavonObject extends JsavonBase {
	private static final long serialVersionUID = -7239433548141209455L;
	
	//Methodes statiques
	public static <T extends JsavonObject> T instanciate(Class<T> clazz, JSONObject json) {
		T instance = ReflectionUtils.tryInstanciate(clazz, json);
		if (instance != null)
			return instance;
			
		instance = ReflectionUtils.tryInstanciate(clazz);
		if(instance == null)
			throw new JsavonException("No valid constructor has been found.");
		
		if (json != null)
			init(instance, json);
		return instance;
	}
	
	public static <T extends JsavonObject> T instanciate(Class<T> clazz) {
		return instanciate(clazz, null);
	}
	
	//Constructeurs
	public JsavonObject(JSONObject json) {
		super();
		if (json != null)
			init(this, json);
	}
	
	public JsavonObject() {
		super();
	}
	
	//Init
	private static final void init(final JsavonObject instance, final JSONObject json) {		
		for (Field field : ReflectionUtils.getAllFields(instance.getClass())) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if (field.isAnnotationPresent(ManualValue.class))
				continue;
			
			ReflectionUtils.trySet(field, instance, getValue(json, field));
		}
	}
	
	private static final <T> T getValue(final JSONObject root, final Field field) {
		final JOValue jovalue = field.getAnnotation(JOValue.class);
		
		String[] paths, keys;
		boolean mandatory;
		
		if (jovalue == null) {
			paths = new String[] {""};
			keys = new String[] {field.getName()};
			mandatory = true;
		}
		else {
			paths = jovalue.path().length == 0 ? new String[]{""} : jovalue.path();
			keys = jovalue.key().length == 0 ? new String[] {field.getName()} : jovalue.key();
			mandatory = jovalue.mandatory();
		}
		
		final Destination<T> destination = null; //TODO Proposer un resolver en fonction d'un generic declaration et d'une instance
		JSONObject json;
		Object object;
						
		for (String path : paths ) {
			json = (JSONObject) get(root, path);
			
			for (String key : keys) {
				object = get(json, key);
				if (object != null)
					return converter.convert(object, destination);
			}
		}
		
		if (mandatory)
			throw new JsavonException("Unable to find a value for the field \"" + field.getName() + "\".");
		
		return null;
	}
	
	private final static Object get(JSONObject json, final String fullPath) {	
		if (fullPath.isBlank())
			return json;
		
		final String[] paths = fullPath.split("/");
		for (int i = 0 ; i < paths.length-1 ; i++) {
			if (paths[i].isBlank())
				continue;
			json = json.getJSONObject(paths[i]);
		}
		
		return json.get(paths[paths.length-1]);
	}
}
