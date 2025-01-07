package com.sap.engine.lib.injection;

import java.util.HashMap;
import java.util.Map;


abstract class ClassElementInjector implements Injector {
	
	private static Map<Class, Class> simple2objectMap = new HashMap<Class, Class>(); 
	
	static {
		simple2objectMap.put(Boolean.TYPE, Boolean.class);
		simple2objectMap.put(Character.TYPE, Character.class);
		simple2objectMap.put(Byte.TYPE, Byte.class);
		simple2objectMap.put(Short.TYPE, Short.class);
		simple2objectMap.put(Integer.TYPE, Integer.class);
		simple2objectMap.put(Long.TYPE, Long.class);
		simple2objectMap.put(Float.TYPE, Float.class);
		simple2objectMap.put(Double.TYPE, Double.class);
	}
	
	protected boolean checkAssignability(Class classElementType, Class injectionValueType) {
		Class convertedClassElementType = convertSimpleTypes(classElementType);
		return convertedClassElementType.isAssignableFrom(injectionValueType);
	}

	private Class convertSimpleTypes(Class classElementType) {
		Class result = simple2objectMap.get(classElementType);
		if (result == null) {
			result = classElementType;
		}
		return result;
	}
}
