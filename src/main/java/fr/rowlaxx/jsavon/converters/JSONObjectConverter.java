package fr.rowlaxx.jsavon.converters;

import org.json.JSONObject;

import fr.rowlaxx.convertutils.Return;
import fr.rowlaxx.convertutils.SimpleConverter;

@Return(canReturnInnerType = false)
public class JSONObjectConverter extends SimpleConverter<JSONObject> {

	//Constructeurs
	public JSONObjectConverter() {
		super(JSONObject.class);
	}
	
}
