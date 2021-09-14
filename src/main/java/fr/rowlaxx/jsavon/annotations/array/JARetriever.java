package fr.rowlaxx.jsavon.annotations.array;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fr.rowlaxx.jsavon.interfaces.JAValueRetriever;

@Retention(RUNTIME)
@Target(FIELD)
public @interface JARetriever {
	
	public Class<? extends JAValueRetriever> retriever();

}