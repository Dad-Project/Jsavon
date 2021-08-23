package fr.rowlaxx.jsavon.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ReflectionUtils {

	public static List<Field> getAllFields(Class<?> type) {
		final List<Field> fields = new ArrayList<Field>();
		for (Class<?> c = type; c != null; c = c.getSuperclass())
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
		
		fields.sort(new Comparator<Field>() {
			@Override
			public int compare(Field o1, Field o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		
		return fields;
	}
	
}
