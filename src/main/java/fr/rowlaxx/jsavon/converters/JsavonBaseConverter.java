package fr.rowlaxx.jsavon.converters;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.rowlaxx.convertutils.ConvertMethod;
import fr.rowlaxx.convertutils.Return;
import fr.rowlaxx.convertutils.SimpleConverter;
import fr.rowlaxx.jsavon.Jsavon;
import fr.rowlaxx.jsavon.JsavonArray;
import fr.rowlaxx.jsavon.JsavonBase;
import fr.rowlaxx.jsavon.JsavonException;
import fr.rowlaxx.jsavon.annotations.JAValue;
import fr.rowlaxx.jsavon.annotations.JOValue;
import fr.rowlaxx.jsavon.annotations.ManualValue;
import fr.rowlaxx.utils.ReflectionUtils;

@Return(canReturnInnerType = true)
public class JsavonBaseConverter extends SimpleConverter<JsavonBase> {

	//Constructeurs
	public JsavonBaseConverter() {
		super(JsavonBase.class);
	}
	
	//Methodes
	@ConvertMethod(priority = 0)
	public <T extends JsavonArray> T toJsavon(JSONObject json) {
		@SuppressWarnings("unchecked")
		final Class<T> clazz = (Class<T>)getConverter().convert(json.get("class"), Class.class);
		return toJsavon(json, clazz);
	}
	
	@ConvertMethod(priority = 1)
	public <T extends JsavonBase> T toJsavon(Object json, Class<T> destination) {
		Objects.requireNonNull(json, "json may not be null.");
		Objects.requireNonNull(destination, "destination may not be null.");
		
		if (!(json instanceof JSONObject || json instanceof JSONArray))
			throw new IllegalArgumentException("json must be a JSONArray or a JSONObject.");
		
		final Jsavon.Entry entry = Jsavon.getEntry(destination);
		final Field[] fields = entry.getFields();
		final Type[] types = entry.getResolvedFieldTypes();
		
		T instance = ReflectionUtils.tryInstanciate(destination);
		if (instance == null)
			instance = ReflectionUtils.tryInstanciate(destination, json);
		
		if (instance == null)
			throw new JsavonException("Unable to instanciate type " + destination);
		
		Field field;
		Object value;
		for (int i = 0 ; i < fields.length ; i++) {			
			if ((field = fields[i]).isAnnotationPresent(ManualValue.class))
				continue;
			
			if (json instanceof JSONObject)
				value = getValue((JSONObject)json, field);
			else
				value = getValue((JSONArray)json, field);
			
			value = getConverter().convert(value, types[i]);
			ReflectionUtils.trySet(field, instance, value);
		}
		
		return instance;
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
	
	@SuppressWarnings("unchecked")
	private static final <T> T getValue(final JSONObject root, final Field field) {
		final JOValue jovalue = field.getAnnotation(JOValue.class);
		
		String[] paths = new String[] {""};
		String[] keys = new String[] {field.getName()};
		boolean mandatory = true;
		
		if (jovalue != null) {
			if (jovalue.path().length != 0) paths = jovalue.path();
			if (jovalue.key().length != 0) keys = jovalue.key();
			mandatory = jovalue.mandatory();
		}
		
		JSONObject json;
		Object value;						
		for (String path : paths ) {
			json = (JSONObject) get(root, path);
			for (String key : keys)
				if ((value = get(json, key)) != null)
					return (T) value;
		}
		
		if (mandatory)
			throw new JsavonException("Unable to find a value for the field \"" + field.getName() + "\".");
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValue(final JSONArray array, final Field field) {
		final JAValue jaValue = field.getAnnotation(JAValue.class);
		if (jaValue == null)
			throw new JsavonException("Annotation JAValue must be present for the field " + field);
		
		return (T) array.get(jaValue.index());
	}
}