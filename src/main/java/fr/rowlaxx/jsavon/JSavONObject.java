package fr.rowlaxx.jsavon;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;

import fr.rowlaxx.jsavon.annotations.ExcludeFrom;
import fr.rowlaxx.jsavon.annotations.ManualValue;
import fr.rowlaxx.jsavon.annotations.object.JORetriever;
import fr.rowlaxx.jsavon.exceptions.JSavONRetrieverInstanciationException;
import fr.rowlaxx.jsavon.impl.DefaultJOValueRetriever;
import fr.rowlaxx.jsavon.interfaces.JOValueRetreiver;
import fr.rowlaxx.jsavon.utils.ReflectionUtils;

public class JSavONObject extends JSavON {
	private static final long serialVersionUID = -7239433548141209455L;
	
	//Variables
	@ExcludeFrom(fromEquals = true, fromHashCode = true, fromToString = true)
	@ManualValue
	private transient final HashMap<Class<? extends JOValueRetreiver>, JOValueRetreiver> retrievers = new HashMap<>();
	
	//Constructeurs
	public JSavONObject(JSONObject json) {
		super();
		Objects.requireNonNull(json, "json may not be null.");
		init(json);
	}
	
	//Init
	private final void init(JSONObject json) {
		final List<Field> fields = ReflectionUtils.getAllFields(this.getClass());

		JORetriever joRetriever;
		JOValueRetreiver joValueRetriever;
		
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if (field.isAnnotationPresent(ManualValue.class))
				continue;
			
			joRetriever = field.getAnnotation(JORetriever.class);
			joValueRetriever = getRetrieverInstance( joRetriever == null ? null : joRetriever.retriever() );
			
			try {
				field.setAccessible(true);
				field.set(this, joValueRetriever.getValue(this, json, field));
			} catch(IllegalAccessException e) {
				e.printStackTrace();//This error should never be thrown
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends JOValueRetreiver> T getRetrieverInstance(Class<T> clazz) {
		synchronized (retrievers) {
			if (retrievers.containsKey(clazz))
				return (T) retrievers.get(clazz);
		}
		
		T instance;
		if (clazz == null || clazz == DefaultJOValueRetriever.class)
			instance = (T) DefaultJOValueRetriever.INSTANCE;
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
