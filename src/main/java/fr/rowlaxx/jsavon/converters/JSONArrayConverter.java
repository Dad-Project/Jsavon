package fr.rowlaxx.jsavon.converters;

import org.json.JSONArray;

import fr.rowlaxx.convertutils.Return;
import fr.rowlaxx.convertutils.SimpleConverter;

@Return(canReturnInnerType = false)
public class JSONArrayConverter extends SimpleConverter<JSONArray> {

	//Constructeurs
	public JSONArrayConverter() {
		super(JSONArray.class);
	}

}
