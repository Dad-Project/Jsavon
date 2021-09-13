package fr.rowlaxx.jsavon.convert;

import java.util.Map;
import java.util.Objects;

import fr.rowlaxx.jsavon.annotations.MapKey;

@SuppressWarnings("rawtypes")
public class MapDestination<K, V> extends Destination<Map> {

	//Variables
	private MapKey mapKey;
	
	//Constructeurs
	public MapDestination(MapKey mapKey, Destination<K> keyDestination, Destination<V> valueDestination) {
		super(Map.class, keyDestination, valueDestination);
		this.mapKey = Objects.requireNonNull(mapKey, "fieldName may not be null.");
	}
	
	//Getters
	public MapKey getMapKey() {
		return mapKey;
	}
	
	public String getStringMapKey() {
		return mapKey.fieldName();
	}

}
