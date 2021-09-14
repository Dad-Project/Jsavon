package fr.rowlaxx.jsavon.annotations.object;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fr.rowlaxx.jsavon.interfaces.JOValueRetreiver;

@Retention(RUNTIME)
@Target(FIELD)
public @interface JORetriever {
	
	public Class<? extends JOValueRetreiver> retriever();

}
