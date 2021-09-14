package fr.rowlaxx.jsavon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD )
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ExcludeFrom {

	public boolean fromToString();
	public boolean fromHashCode();
	public boolean fromEquals();
}
