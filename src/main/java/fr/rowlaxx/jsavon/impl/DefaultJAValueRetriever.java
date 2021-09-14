package fr.rowlaxx.jsavon.impl;

import java.lang.reflect.Field;

import org.json.JSONArray;

import fr.rowlaxx.jsavon.JSavONArray;
import fr.rowlaxx.jsavon.annotations.array.JAValue;
import fr.rowlaxx.jsavon.convert.ConvertRequest;
import fr.rowlaxx.jsavon.convert.Destination;
import fr.rowlaxx.jsavon.convert.DestinationResolver;
import fr.rowlaxx.jsavon.exceptions.JSavONException;
import fr.rowlaxx.jsavon.interfaces.JAValueRetriever;

public class DefaultJAValueRetriever implements JAValueRetriever {

	//Instance
	public static final DefaultJAValueRetriever INSTANCE = new DefaultJAValueRetriever();
	
	//Méthodes réecrites
	@Override
	public <T> T getValue(final JSavONArray instance, final JSONArray array, final Field field) {
		final JAValue jaValue = field.getAnnotation(JAValue.class);
		if (jaValue == null)
			throw new JSavONException("Annotation JAValue must be present for the field " + field);
		
		final Destination<T> destination = DestinationResolver.resolve(field, instance);
		final Object value = array.get(jaValue.index());
		final ConvertRequest<T> request = new ConvertRequest<>(value, destination);
		return request.execute();
	}
}