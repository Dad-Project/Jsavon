package fr.rowlaxx.jsavon;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;

import fr.rowlaxx.jsavon.annotations.object.JORetriever;
import fr.rowlaxx.jsavon.annotations.object.JOValue;
import fr.rowlaxx.jsavon.interfaces.DefaultJOValueRetreiver;
import fr.rowlaxx.jsavon.interfaces.JOValueRetreiver;
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
		JORetriever joRetriever;
		JOValueRetreiver joValueRetreiver;
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			
			if (!field.isAnnotationPresent(JOValue.class))
				continue;
			
			if ( (joRetriever = field.getAnnotation(JORetriever.class)) == null )
				joValueRetreiver = DefaultJOValueRetreiver.INSTANCE;
			else if (joRetriever.getClass() == DefaultJOValueRetreiver.class)
				joValueRetreiver = DefaultJOValueRetreiver.INSTANCE;
			else
				try {
					joValueRetreiver = joRetriever.retreiver().getConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					joValueRetreiver = DefaultJOValueRetreiver.INSTANCE;
				}
			
			try {
				field.setAccessible(true);
				field.set(this, joValueRetreiver.getValue(json, field));
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
}
