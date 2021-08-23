package fr.rowlaxx.jsavon;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import fr.rowlaxx.jsavon.annotations.Exclude;
import fr.rowlaxx.jsavon.utils.ReflectionUtils;

public abstract class JSavON {

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
		sb.append(" ]");
		
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
				sb.append( field.get(this).toString() );
				sb.append(", ");
			} catch (IllegalAccessException e) {
				throw new JSavONException("The Field \"" + field.getName() + "\" objectis enforcing Java language access control and the underlyingfield is inaccessible.");
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
		Field otherField;
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
				otherField = obj.getClass().getDeclaredField( field.getName() );
				field.setAccessible(true);
				otherField.setAccessible(true);
				o1 = field.get(this);
				o2 = otherField.get(this);
				
				//On vérifie l'égalité
				if (!o1.equals(o2))
					return false;
			} catch(IllegalAccessException e) {
				throw new JSavONException("The Field \"" + field.getName() + "\" objectis enforcing Java language access control and the underlyingfield is inaccessible.");
			} catch(NoSuchFieldException e) {
				throw new JSavONException("Unable to find the corresponding field.");
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
				temp = field.get(this).hashCode();
				result = prime * result + (temp ^ (temp >>> 32));
			}catch(IllegalAccessException e) {
				throw new JSavONException("The Field \"" + field.getName() + "\" objectis enforcing Java language access control and the underlyingfield is inaccessible.");
			}
		}
		return result;
	}
}
