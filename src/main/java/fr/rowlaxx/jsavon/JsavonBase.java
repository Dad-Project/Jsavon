package fr.rowlaxx.jsavon;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;

import fr.rowlaxx.jsavon.annotations.ExcludeFrom;
import fr.rowlaxx.utils.ReflectionUtils;

public abstract class JsavonBase implements Serializable {
	private static final long serialVersionUID = -6972113327092008717L;
	
	//Constructeurs
	JsavonBase() {
		Jsavon.check(getClass());
	}

	//toString
	@Override
	public String toString() {
		final Jsavon.Entry entry = Jsavon.getEntry(getClass());
		final Field[] fields = entry.getFields();
		final StringBuilder sb = new StringBuilder(0xFF);

		ExcludeFrom exclude;
		for (Field field : fields) {
			exclude = field.getAnnotation(ExcludeFrom.class);
			if (exclude != null && exclude.fromToString())
				continue;			

			sb.append( field.getName() );
			sb.append('=');
			sb.append( (Object)ReflectionUtils.tryGet(field, this) );
			sb.append(", ");
		}

		if (sb.length() != 0) {
			sb.deleteCharAt(sb.length()-1);
			sb.setCharAt(sb.length()-1, ']');
			sb.insert(0, " [");
		}
		
		sb.insert(0, getClass().getSimpleName());
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
		
		final Jsavon.Entry entry = Jsavon.getEntry(getClass());
		Object o1, o2;
		ExcludeFrom exclude;
		for (Field field : entry.getFields()) {
			exclude = field.getAnnotation(ExcludeFrom.class);
			if (exclude != null && exclude.fromEquals())
				continue;

			o1 = ReflectionUtils.tryGet(field, this);
			o2 = ReflectionUtils.tryGet(field, obj);

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

		final Jsavon.Entry entry = Jsavon.getEntry(getClass());
		ExcludeFrom exclude;
		for (Field field : entry.getFields()) {
			exclude = field.getAnnotation(ExcludeFrom.class);
			if (exclude != null && exclude.fromHashCode())
				continue;
			
			temp = Objects.hashCode( ReflectionUtils.tryGet(field, this) );
			result = prime * result + (temp ^ (temp >>> 32));
		}
		return result;
	}
}
