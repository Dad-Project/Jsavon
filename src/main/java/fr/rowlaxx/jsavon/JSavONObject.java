package fr.rowlaxx.jsavon;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;

import fr.rowlaxx.jsavon.utils.ReflectionUtils;

public class JSavONObject extends JSavON {

	//Constructeurs
	public JSavONObject(JSONObject json) {
		super();
		Objects.requireNonNull(json, "json may not be null.");
		init(json);
	}
	
	//Init
	private void init(JSONObject json) {
		final List<Field> fields = ReflectionUtils.getAllFields(this.getClass());
		for (Field field : fields) {
			
		}
	}
	
	
	
}
