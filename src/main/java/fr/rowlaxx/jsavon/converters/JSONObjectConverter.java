package fr.rowlaxx.jsavon.converters;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.rowlaxx.convertutils.ConvertMethod;
import fr.rowlaxx.convertutils.Return;
import fr.rowlaxx.convertutils.SimpleConverter;
import fr.rowlaxx.jsavon.Jsavon;
import fr.rowlaxx.jsavon.JsavonBase;
import fr.rowlaxx.jsavon.JsavonException;
import fr.rowlaxx.utils.ReflectionUtils;

@Return(canReturnInnerType = false)
public class JSONObjectConverter extends SimpleConverter<JSONObject> {

	//Constructeurs
	public JSONObjectConverter() {
		super(JSONObject.class);
	}

	//Methodes
	@ConvertMethod
	public JSONObject toJson(JsavonBase base) {
		Objects.requireNonNull(base, "base may not be null.");

		final JSONObject json = new JSONObject();
		final Jsavon.Entry entry = Jsavon.getEntry(base.getClass());

		json.put("class", (String)getConverter().convert(base.getClass(), String.class));

		final Field[] fields = entry.getFields();		
		Object rawValue, newValue;

		for (Field field : fields) {
			rawValue = ReflectionUtils.tryGet(field, base);
			newValue = toJsonItem(rawValue);
			json.put(field.getName(), newValue);
		}

		return json;
	}

	@ConvertMethod
	public JSONObject toJson(Map<String,?> map) {
		Objects.requireNonNull(map, "map may not be null.");
		Object rawValue, newValue;
		
		final JSONObject json = new JSONObject();
		for (Entry<String, ?> entry : map.entrySet()) {
			rawValue = entry.getValue();
			newValue = toJsonItem(rawValue);
			json.put(entry.getKey(), newValue);
		}
		
		return json;
	}

	private Object toJsonItem(Object rawValue) {
		if (rawValue == null)
			return null;
		else if (rawValue instanceof Collection)
			return getConverter().convert(rawValue, JSONArray.class);
		else if (rawValue instanceof Map || rawValue instanceof JsavonBase)
			return getConverter().convert(rawValue, JSONObject.class);
		else if (rawValue instanceof Boolean || rawValue instanceof Number || rawValue instanceof JSONArray || rawValue instanceof JSONObject)
			return rawValue;
		else
			throw new JsavonException("Unknow type : " + rawValue.getClass());
	}
}
