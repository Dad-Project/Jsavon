package fr.rowlaxx.jsavon;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.json.JSONArray;

import fr.rowlaxx.jsavon.annotations.JAValue;
import fr.rowlaxx.jsavon.annotations.ManualValue;
import fr.rowlaxx.utils.generic.ReflectionUtils;
import fr.rowlaxx.utils.generic.destination.Destination;

public abstract class JsavonArray extends JsavonBase {
	private static final long serialVersionUID = -2584160215002397493L;

	//Constructeurs
	public JsavonArray(JSONArray array) {
		super();
		if (array != null)
			init(this, array);
	}
	
	public JsavonArray() {
		super();
	}
	
	//init
	private static final void init(final JsavonArray instance, final JSONArray array) {
		for (Field field : ReflectionUtils.getAllFields(instance.getClass())) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if (field.isAnnotationPresent(ManualValue.class))
				continue;
			
			ReflectionUtils.trySet(field, instance, getValue(array, field));
		}
	}
	
	public static <T> T getValue(final JSONArray array, final Field field) {
		final JAValue jaValue = field.getAnnotation(JAValue.class);
		if (jaValue == null)
			throw new JsavonException("Annotation JAValue must be present for the field " + field);
		
		final Destination<T> destination = null; //TODO Proposer un resolver en fonction d'un generic declaration et d'une instance
		final Object rawValue = array.get(jaValue.index());
		return converter.convert(rawValue, destination);
	}
}
