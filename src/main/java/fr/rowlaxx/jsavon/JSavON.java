package fr.rowlaxx.jsavon;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

import fr.rowlaxx.jsavon.annotations.Exclude;
import fr.rowlaxx.jsavon.utils.ReflectionUtils;

abstract class JSavON implements Serializable {
	private static final long serialVersionUID = -6972113327092008717L;

	//Constructeurs
	protected JSavON() {
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
		
		Exclude exclude;
		for (Field field : fields) {
			//Si le field est statique, on l'ignore
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			
			//Si le field contient l'annotation exclude, on vérifie
			if ((exclude = field.getAnnotation(Exclude.class) ) != null)
				if (exclude.excludeFromToString())
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
		Exclude exclude;
		for (Field field : ReflectionUtils.getAllFields(getClass())) {
			//Si le field est statique, on l'ignore
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			
			//Si le field contient l'annotation exclude, on vérifie
			if ((exclude = field.getAnnotation(Exclude.class) ) != null)
				if (exclude.excludeFromEquals())
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

		Exclude exclude;
		for (Field field : ReflectionUtils.getAllFields(getClass()) ) {
			//Si le field est statique, on l'ignore
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			
			//Si le field contient l'annotation exclude, on vérifie
			if ((exclude = field.getAnnotation(Exclude.class) ) != null)
				if (exclude.excludeFromHashCode())
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
}
