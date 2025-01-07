package com.sap.sdo.impl.objects.strategy.enhancer;

import java.util.HashMap;
import java.util.Map;

import com.sap.sdo.impl.objects.DataStrategy;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoType;
import commonj.sdo.Property;

public class DefaultPojoHelper {

	public static Object get(Object proxy, DataStrategy ds, String name, String descr) {
		GenericDataObject gdo = ds.getDataObject();
		return gdo.get(getPropertyFromMethodName(proxy, gdo, name));
	}
	private static final String METHOD_NAME_TO_PROPERTY = "methodNameToProperty"; 
	private static Property getPropertyFromMethodName(Object proxy, GenericDataObject gdo, String name) {
		SdoType t = (SdoType)gdo.getType();
		Map<String,Property> map = 	(Map<String,Property>)t.getExtraData("pojoHelper", METHOD_NAME_TO_PROPERTY);
		if (map == null) {
			map = new HashMap<String,Property>();
			t.putExtraData("pojoHelper", METHOD_NAME_TO_PROPERTY, map);
		}
		String key = proxy.getClass().getSuperclass().getName()+'.'+name;
		Property ret = map.get(key);
		if (ret == null) {
			ret = t.getPropertyFromJavaMethodName(name);
			map.put(key, ret);
		}
		return ret;
	}
	public static void set(Object proxy, DataStrategy ds, String name, String descr, Object arg) {	
		GenericDataObject gdo = ds.getDataObject();
		gdo.set(getPropertyFromMethodName(proxy, gdo, name),arg);
	}
    
}
