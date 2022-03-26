package fr.rowlaxx.jsavon.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import fr.rowlaxx.convertutils.ConvertMethod;
import fr.rowlaxx.convertutils.Return;
import fr.rowlaxx.convertutils.converters.MapConverter;
import fr.rowlaxx.utils.ParameterizedClass;
import fr.rowlaxx.utils.ReflectionUtils;

@Return(canReturnInnerType = true)
public class MapConverterV2 extends MapConverter {

	@ConvertMethod
	public <K, V> Map<K, V> toMap(JSONObject json, ParameterizedClass destination) {
		@SuppressWarnings("unchecked")
		Map<K, V> map = (Map<K, V>) ReflectionUtils.tryInstanciate(destination.getRawType());
		if (map == null)
			map = new HashMap<>();
		
		for (Entry<String, Object> entry : json.toMap().entrySet())
			map.put(
					getConverter().convert(entry.getKey(), destination.getActualTypeArgument(0)),
					getConverter().convert(entry.getValue(), destination.getActualTypeArgument(1)));
		
		return map;
	}
	
}
