package fr.rowlaxx.jsavon.converters;

import java.util.Collection;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.rowlaxx.convertutils.ConvertMethod;
import fr.rowlaxx.convertutils.Return;
import fr.rowlaxx.convertutils.SimpleConverter;
import fr.rowlaxx.jsavon.JsavonBase;
import fr.rowlaxx.jsavon.JsavonException;

@Return(canReturnInnerType = false)
public class JSONArrayConverter extends SimpleConverter<JSONArray> {

	//Constructeurs
	public JSONArrayConverter() {
		super(JSONArray.class);
	}
	
	//Methodes
	@ConvertMethod
	public JSONArray toJsonArray(Iterable<?> iterable) {
		JSONArray array = new JSONArray();
		for (Object o : iterable)
			array.put(toJsonItem(o));
		return array;
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
