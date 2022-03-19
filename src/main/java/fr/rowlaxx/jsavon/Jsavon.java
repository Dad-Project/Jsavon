package fr.rowlaxx.jsavon;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import fr.rowlaxx.convertutils.Converter;
import fr.rowlaxx.convertutils.ConverterFactory;
import fr.rowlaxx.convertutils.MapKey;
import fr.rowlaxx.jsavon.annotations.JAValue;
import fr.rowlaxx.jsavon.annotations.JOValue;
import fr.rowlaxx.jsavon.annotations.ManualValue;
import fr.rowlaxx.jsavon.converters.JSONArrayConverter;
import fr.rowlaxx.jsavon.converters.JSONObjectConverter;
import fr.rowlaxx.jsavon.converters.JsavonBaseConverter;
import fr.rowlaxx.utils.GenericUtils;
import fr.rowlaxx.utils.ReflectionUtils;

public final class Jsavon {

	//Factory
	public static final Converter converter = ConverterFactory
			.newDefaultInstance()
			.addSimpleConverter(new JsavonBaseConverter())
			.addSimpleConverter(new JSONObjectConverter())
			.addSimpleConverter(new JSONArrayConverter())
			.build();
	
	public <T extends JsavonBase> T parse(Object json, Class<T> destination) {
		return converter.convert(json, destination);
	}
	
	public <T extends JsavonBase> T parse(JSONObject json) {
		return converter.convert(json, JsavonBase.class);
	}
	
	public static final JSONObject toJson(JsavonBase base) {
		return converter.convert(base, JSONObject.class);
	}
	
	//Verified
	private static final Map<Class<? extends JsavonBase>, Entry> entries = new HashMap<>();
	
	public static class Entry {
		private static void verify(Class<?> clazz, Field field) {
			final JAValue jaValue = field.getAnnotation(JAValue.class);
			final JOValue joValue = field.getAnnotation(JOValue.class);
			final boolean manualValue = field.isAnnotationPresent(ManualValue.class);
			final boolean mapKey = field.isAnnotationPresent(MapKey.class);
						
			if (manualValue)
				if (jaValue != null || joValue != null || mapKey)
					throw new JsavonException("A ManualValue field must not contains other annotation.");
			
			if (JsavonArray.class.isAssignableFrom(clazz) && joValue != null)
				throw new JsavonException("JOValue annotation may not be present here.");
			if (JsavonObject.class.isAssignableFrom(clazz) && jaValue != null)
				throw new JsavonException("JAValue annotation may not be present here.");
			
			if (mapKey && Map.class.isAssignableFrom(GenericUtils.resolveClass(field.getGenericType(), clazz)))
				throw new JsavonException("A MapKey annotation may only be present on a map object.");
		}
		
		private final Exception exception;
		private final Class<? extends JsavonBase> clazz;
		private final Field[] fields;
		private final Type[] fieldsType;
		
		private Entry(Class<? extends JsavonBase> clazz, Exception exception) {
			this.fields = null;
			this.fieldsType = null;
			this.exception = exception;
			this.clazz = clazz;
		}
		
		private Entry(Class<? extends JsavonBase> clazz) {
			this.exception = null;
			this.clazz = clazz;
			
			final List<Field> fields = ReflectionUtils.getAllFields(clazz);
			Field field;
			for (int i = fields.size() - 1 ; i >= 0 ; i--) {
				field = fields.get(i);
				
				if (Modifier.isStatic(field.getModifiers()))
					fields.remove(i);
				
				field.setAccessible(true);
			}
			
			this.fields = new Field[fields.size()];
			fields.toArray(this.fields);

			this.fieldsType = new Type[fields.size()];
			for (int i = 0 ; i < fields.size() ; i++) {
				verify(clazz, this.fields[i]);
				this.fieldsType[i] = GenericUtils.resolve(this.fields[i].getGenericType(), clazz);
			}
		}
		
		private void check() {
			if (exception != null)
				throw new JsavonException(clazz + " is not correct : " + exception.getMessage());
		}
		
		public Field[] getFields() {
			return fields;
		}
		
		public Type[] getResolvedFieldTypes() {
			return fieldsType;
		}
	}
	
	public static final Entry getEntry(Class<? extends JsavonBase> clazz) {
		Entry entry = entries.get(clazz);
		
		if (entry == null)
			try{
				entries.put(clazz, entry = new Entry(clazz));
			}catch(JsavonException e) {
				entries.put(clazz, entry = new Entry(clazz, e));
			}

		
		return entry;
	}	
	
	public static final void check(Class<? extends JsavonBase> clazz) {
		getEntry(clazz).check();
	}
}