package fr.rowlaxx.jsavon.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface JOValue {
	
	/**
	 * La clé dans le Json qui représente cette variable
	 * La prémière clé trouvée sera utilisée, terminant la recherche
	 * 
	 * Vous pouvez utiliser un chemin à condition que le chemin ne commence pas par le séparateur
	 * Le séparateur est '/'
	 * 
	 * Si une clé existante ne permet pas l'assignation de la valeur, une erreur sera envoyée.
	 * 
	 * Si key vaut {}, alors le nom déclaré de la variable sera utilisé
	 * 
	 * @return la liste des clés possibles.
	 */
	public String[] key() default {};
	
	/**
	 * Les chemins possible 
	 * Chaque chemin sera testé dans l'ordre de déclaration
	 * Pour chaque chemin, toutes les clés seront testées
	 * 
	 * La prémière clé trouvée sera utilisée, terminant la recherche
	 * 
	 * Le séparateur est '/'
	 * Le chemin root est "/"
	 * 
	 * Par défault, seul le chemin root est utilisé
	 * 
	 * @return la liste des chemins possibles.
	 */
	public String[] path() default {};
	
	/**
	 * représente l'obligation de présence d'une valeur
	 * Si true, alors la valeur sera obligatoire
	 * Si une valeur est mandatoire, alors toute absence de cette valeur dans le json lancera une erreur
	 * @return true si le paramètre est mandatoire
	 */
	public boolean mandatory() default true;
}
