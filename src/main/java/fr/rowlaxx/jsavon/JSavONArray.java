package fr.rowlaxx.jsavon;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import fr.rowlaxx.jsavon.annotations.ExcludeFrom;
import fr.rowlaxx.jsavon.annotations.ManualValue;
import fr.rowlaxx.jsavon.annotations.array.JARetriever;
import fr.rowlaxx.jsavon.exceptions.JSavONRetrieverInstanciationException;
import fr.rowlaxx.jsavon.impl.DefaultJAValueRetriever;
import fr.rowlaxx.jsavon.interfaces.JAValueRetriever;
import fr.rowlaxx.jsavon.utils.ReflectionUtils;

public abstract class JSavONArray extends JSavON {
	private static final long serialVersionUID = -2584160215002397493L;

	//Variables
	@ManualValue
	@ExcludeFrom(fromEquals = true, fromHashCode = true, fromToString = true)
	private final transient HashMap<Class<? extends JAValueRetriever>, JAValueRetriever> retrievers = new HashMap<>();
	
	//Constructeurs
	public JSavONArray(JSONArray array) {
		super();
		init(array);
	}
	
	public JSavONArray() {
		super();
	}
	
	//init
	private final void init(JSONArray array) {
		final List<Field> fields = ReflectionUtils.getAllFields(this.getClass());

		JARetriever jaRetriever;
		JAValueRetriever jaValueRetriever;
		
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if (field.isAnnotationPresent(ManualValue.class))
				continue;
			
			jaRetriever = field.getAnnotation(JARetriever.class);
			jaValueRetriever = getRetrieverInstance( jaRetriever == null ? null : jaRetriever.retriever() );
			
			try {
				field.setAccessible(true);
				field.set(this, jaValueRetriever.getValue(this, array, field));
			} catch(IllegalAccessException e) {
				e.printStackTrace();//This error should never be thrown
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends JAValueRetriever> T getRetrieverInstance(Class<T> clazz) {
		synchronized (retrievers) {
			if (retrievers.containsKey(clazz))
				return (T) retrievers.get(clazz);
		}
		
		T instance;
		if (clazz == null || clazz == DefaultJAValueRetriever.class)
			instance = (T) DefaultJAValueRetriever.INSTANCE;
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
