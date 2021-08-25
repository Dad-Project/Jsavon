package fr.rowlaxx.jsavon.annotations.object;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fr.rowlaxx.jsavon.interfaces.DefaultJOValueRetreiver;
import fr.rowlaxx.jsavon.interfaces.JOValueRetreiver;

@Retention(RUNTIME)
@Target(FIELD)
public @interface JOValue {
	
	/**
	 * La clé dans le Json qui représente cette variable
	 * La prémière clé trouvée sera utilisée, négligeant les suivantes
	 * Si une clé existante ne permet pas l'assignation de la valeur, une erreur sera lancée.
	 * 
	 * Si key vaut "", alors le nom déclaré de la variable sera utilisé
	 * 
	 * @return la liste des clés possibles.
	 */
	public String[] key() default "";
	
	/**
	 * Les chemins possible dans le Json
	 * Chaque chemin sera testé
	 * Dés qu'une valeur est trouvée, les chemins suivants seront négligés
	 * Le séparateur est '/'
	 * Le chemin root est "" ou "/"
	 * @return la liste des chemins possibles.
	 */
	public String[] path() default "";
	
	/**
	 * représente l'obligation de présence d'une valeur
	 * Si true, alors la valeur sera obligatoire
	 * Si une valeur est mandatoire, alors toute absence de cette valeur dans le json lancera une erreur
	 * @return true si le paramètre est mandatoire
	 */
	public boolean mandatory() default true;
}
