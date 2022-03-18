package fr.rowlaxx.jsavon;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.rowlaxx.convertutils.Converter;
import fr.rowlaxx.convertutils.ConverterFactory;
import fr.rowlaxx.jsavon.annotations.ExcludeFrom;
import fr.rowlaxx.utils.generic.ReflectionUtils;

public abstract class JsavonBase implements Serializable {
	private static final long serialVersionUID = -6972113327092008717L;

	private static final Map<Class<? extends JsavonBase>, Exception> valid = new HashMap<>();
	static final Converter converter = ConverterFactory
			.newDefaultInstance()
			.build();
	
	public static final boolean isVerified(Class<? extends JsavonBase> clazz) {
		return valid.containsKey(clazz);
	}

	public static final boolean isValid(Class<? extends JsavonBase> clazz) {
		if (!isVerified(clazz))
			try{
				verify(clazz);
				valid.put(clazz, null);
			}catch(Exception e) {
				valid.put(clazz, e);
			}

		return valid.get(clazz) == null;
	}

	private static final void verify(Class<? extends JsavonBase> clazz) {
		//TODO Vérifier les annotations ici.


	}

	
	
	//Constructeurs
	JsavonBase() {
		if (!isValid(getClass()))
			throw new JsavonException("The class " + getClass().getName() + " is not a valid Jsavon implementation.", valid.get(getClass()));
	}

	//toString
	@Override
	public String toString() {
		//S'il n'y a pas de field, on renvoit le nom de classe
		final List<Field> fields = ReflectionUtils.getAllFields(getClass());
		if (fields.isEmpty())
			return getClass().getSimpleName();

		final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append(" [");

		ExcludeFrom exclude;
		for (Field field : fields) {
			//Si le field est statique, on l'ignore
			if (Modifier.isStatic(field.getModifiers()))
				continue;

			//Si le field contient l'annotation exclude, on vérifie
			exclude = field.getAnnotation(ExcludeFrom.class);
			if (exclude != null && exclude.fromToString())
				continue;			

			sb.append( field.getName() );
			sb.append('=');
			sb.append( (Object)ReflectionUtils.tryGet(field, this) );
			sb.append(", ");
		}

		//On remplace le ", " à la fin par "]"
		sb.deleteCharAt(sb.length()-1);
		sb.setCharAt(sb.length()-1, ']');
		return sb.toString();
	}

	//equals
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;

		Object o1, o2;
		ExcludeFrom exclude;
		for (Field field : ReflectionUtils.getAllFields(getClass())) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;

			//Si le field contient l'annotation exclude, on vérifie
			exclude = field.getAnnotation(ExcludeFrom.class);
			if (exclude != null && exclude.fromEquals())
				continue;

			//On récupère les valeurs
			o1 = ReflectionUtils.tryGet(field, this);
			o2 = ReflectionUtils.tryGet(field, obj);

			//On vérifie l'égalité
			if (!Objects.equals(o1, o2))
				return false;
		}

		return true;
	}

	//hashCode
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		int temp;

		ExcludeFrom exclude;
		for (Field field : ReflectionUtils.getAllFields(getClass()) ) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;

			//Si le field contient l'annotation exclude, on vérifie
			exclude = field.getAnnotation(ExcludeFrom.class);
			if (exclude != null && exclude.fromHashCode())
				continue;
			
			temp = Objects.hashCode( ReflectionUtils.tryGet(field, this) );
			result = prime * result + (temp ^ (temp >>> 32));
		}
		return result;
	}

	//ToJson
	public JSONObject toJson() {
		final JSONObject json = new JSONObject();
		json.put("class", getClass().getName());

		Object rawValue, newValue;
		for (Field field : ReflectionUtils.getAllFields(getClass())) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if (Modifier.isTransient(field.getModifiers()))
				continue;

			rawValue = ReflectionUtils.tryGet(field, this);
			if (rawValue == null)
				newValue = null;
			else if (	rawValue instanceof Collection)
				newValue = converter.convert(rawValue, JSONArray.class);
			else if (	rawValue instanceof Map || 
						rawValue instanceof JsavonBase)
				newValue = converter.convert(rawValue, JSONObject.class);
			else if (	rawValue instanceof Number || 
						rawValue instanceof Boolean ||
						rawValue instanceof JSONObject ||
						rawValue instanceof JSONArray)
				newValue = rawValue;
			else
				throw new JsavonException("Unknow type : " + rawValue.getClass());
			
			json.put(field.getName(), newValue);
		}

		return json;
	}
}
