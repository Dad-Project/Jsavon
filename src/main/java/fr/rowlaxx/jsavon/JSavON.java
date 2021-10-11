package fr.rowlaxx.jsavon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.rowlaxx.jsavon.annotations.ExcludeFrom;
import fr.rowlaxx.jsavon.convert.ConvertRequest;
import fr.rowlaxx.jsavon.convert.Destination;
import fr.rowlaxx.jsavon.utils.IOUtils;
import fr.rowlaxx.jsavon.utils.IterableArray;
import fr.rowlaxx.jsavon.utils.ReflectionUtils;

public abstract class JSavON implements Serializable {
	private static final long serialVersionUID = -6972113327092008717L;

	//Constructeurs
	JSavON() {
		super();
	}

	//toString
	@Override
	public final String toString() {
		//S'il n'y a pas de field, on renvoit le nom de classe
		final List<Field> fields = ReflectionUtils.getAllFields(this.getClass());
		if (fields.isEmpty())
			return getClass().getSimpleName();

		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName() );
		sb.append(" [");

		ExcludeFrom exclude;
		for (Field field : fields) {
			//Si le field est statique, on l'ignore
			if (Modifier.isStatic(field.getModifiers()))
				continue;

			//Si le field contient l'annotation exclude, on vérifie
			if ((exclude = field.getAnnotation(ExcludeFrom.class) ) != null)
				if (exclude.fromToString())
					continue;

			try {
				//On récupère la valeur
				field.setAccessible(true);
				sb.append( field.getName() );
				sb.append('=');
				sb.append( (Object)field.get(this) );
				sb.append(", ");
			} catch (IllegalAccessException e) {
				e.printStackTrace();//This exception should not be thrown.
			}
		}

		//On remplace le ", " à la fin par "]"
		sb.deleteCharAt(sb.length()-1);
		sb.setCharAt(sb.length()-1, ']');
		return sb.toString();
	}

	//equals
	@Override
	public final boolean equals(Object obj) {
		//Quelques vérifications préliminaires
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;

		Object o1, o2;
		ExcludeFrom exclude;
		for (Field field : ReflectionUtils.getAllFields(getClass())) {
			//Si le field est statique, on l'ignore
			if (Modifier.isStatic(field.getModifiers()))
				continue;

			//Si le field contient l'annotation exclude, on vérifie
			if ((exclude = field.getAnnotation(ExcludeFrom.class) ) != null)
				if (exclude.fromEquals())
					continue;

			try {
				//On récupère les valeurs
				field.setAccessible(true);
				o1 = field.get(this);
				o2 = field.get(obj);

				//On vérifie l'égalité
				if (!Objects.equals(o1, o2))
					return false;
			} catch(IllegalAccessException e) {
				e.printStackTrace();//This exception should not be thrown.
			}
		}
		return true;
	}

	//hashCode
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		int temp;

		ExcludeFrom exclude;
		for (Field field : ReflectionUtils.getAllFields(getClass()) ) {
			//Si le field est statique, on l'ignore
			if (Modifier.isStatic(field.getModifiers()))
				continue;

			//Si le field contient l'annotation exclude, on vérifie
			if ((exclude = field.getAnnotation(ExcludeFrom.class) ) != null)
				if (exclude.fromHashCode())
					continue;

			try {
				//On obtient le hashcode
				field.setAccessible(true);
				temp = Objects.hashCode(field.get(this));
				result = prime * result + (temp ^ (temp >>> 32));
			}catch(IllegalAccessException e) {
				e.printStackTrace();//This exception should not be thrown.
			}
		}
		return result;
	}
	public final void print() {
		System.out.println(this);
	}

	//Methodes Read
	@SuppressWarnings("unchecked")
	public final static <T extends JSavON> T readFrom(InputStream is) throws IOException {
		final JSONObject readed = IOUtils.readJson(is);
		return (T) new ConvertRequest<>(readed, Destination.from(JSavON.class)).execute();
	}

	public final static <T extends JSavON> T readFrom(File file) throws IOException {
		return readFrom(new FileInputStream(file));
	}

	//Methodes Writes
	public static final void writeTo(JSavON object, OutputStream os, boolean close) throws IOException {
		Objects.requireNonNull(object, "object may not be null.");
		Objects.requireNonNull(os, "os may not be null.");

		final byte[] toWrite = object.toJson().toString().getBytes();
		os.write(toWrite);
		if (close)
			os.close();
	}

	public static final void writeTo(JSavON object, OutputStream os) throws IOException {
		writeTo(object, os, true);
	}

	public static final void writeTo(JSavON object, File file) throws IOException {
		writeTo(object, new FileOutputStream(file));
	}

	public final void writeTo(OutputStream os, boolean close) throws IOException {
		writeTo(this, os, close);
	}

	public final void writeTo(OutputStream os) throws IOException {
		writeTo(this, os);
	}

	public final void writeTo(File file) throws IOException {
		writeTo(this, file);
	}

	//ToJson
	public JSONObject toJson() {
		checkIO(getClass());

		final JSONObject json = new JSONObject();
		json.put("class", getClass().toString() );

		Object value;
		for (Field field : ReflectionUtils.getAllFields(getClass())) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if (Modifier.isTransient(field.getModifiers()))
				continue;

			field.setAccessible(true);

			try {
				value = convert(field.get(this));
				json.put(field.getName(), value);
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return json;
	}

	private final static Object convert(Object value) {
		if (ReflectionUtils.isPrimitive(value.getClass()))
			return value;
		if (value instanceof String)
			return value;
		if (value instanceof Class)
			return value.toString();
		if (value.getClass().isEnum())
			return value.toString();
		if (value instanceof JSavON)
			return ((JSavON) value).toJson();
		if (value.getClass().isArray())
			return convert(new IterableArray<>((Object[])value) );
		if (value instanceof Iterable) {
			final JSONArray array = new JSONArray();
			for (Object v : (Iterable<?>)value)
				array.put( convert(v) );
			return array;
		}
		if (value instanceof Map) {
			final JSONObject json = new JSONObject();
			for (Entry<?,?> entry : ((Map<?,?>)value).entrySet() )
				json.put(convert(entry.getKey()).toString(), convert(entry.getValue()));
			return json;
		}
		throw new IllegalArgumentException("Cannot put the object " + value + " in the json.");
	}

	public final static void checkIO(Class<?> clazz) {
		Objects.requireNonNull(clazz, "clazz may not be null.");
		if (!JSavON.class.isAssignableFrom(clazz))
			throw new IllegalArgumentException("clazz must be inherited by JSavON.");
		try {
			clazz.getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			throw new IllegalAccessError("The class " + clazz + " must have an empty constructor for being able to use IO fonctions.");
		}
	}


}
