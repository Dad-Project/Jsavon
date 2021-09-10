package fr.rowlaxx.jsavon;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;

import fr.rowlaxx.jsavon.annotations.Exclude;
import fr.rowlaxx.jsavon.annotations.object.JORetriever;
import fr.rowlaxx.jsavon.annotations.object.JOValue;
import fr.rowlaxx.jsavon.exceptions.JSavONRetrieverInstanciationException;
import fr.rowlaxx.jsavon.impl.DefaultJOValueRetreiver;
import fr.rowlaxx.jsavon.interfaces.JOValueRetreiver;
import fr.rowlaxx.jsavon.utils.ReflectionUtils;

public class JSavONObject extends JSavON {
	private static final long serialVersionUID = -7239433548141209455L;
	
	//Variables
	@Exclude(excludeFromEquals = true, excludeFromHashCode = true, excludeFromToString = true)
	private transient final HashMap<Class<? extends JOValueRetreiver>, JOValueRetreiver> retrievers = new HashMap<>();
	
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
			
			joRetriever = field.getAnnotation(JORetriever.class);
			joValueRetreiver = getRetreiverInstance( joRetriever == null ? null : joRetriever.retreiver() );
			
			try {
				field.setAccessible(true);
				field.set(this, joValueRetreiver.getValue(json, field));
			} catch(IllegalAccessException e) {
				e.printStackTrace();//This error should never be thrown
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends JOValueRetreiver> JOValueRetreiver getRetreiverInstance(Class<T> clazz) {
		synchronized (retrievers) {
			if (retrievers.containsKey(clazz))
				return retrievers.get(clazz);
		}
		
		T instance;
		if (clazz == null || clazz == DefaultJOValueRetreiver.class)
			instance = (T) DefaultJOValueRetreiver.INSTANCE;
		else
			try {
				instance = clazz.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new JSavONRetrieverInstanciationException(e);
			}
		
		synchronized (retrievers) {
			retrievers.put(clazz, instance);
		}
		
		return instance;
	}
}
