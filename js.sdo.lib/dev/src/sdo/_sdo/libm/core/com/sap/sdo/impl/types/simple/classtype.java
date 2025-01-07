/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.types.simple;

import java.util.HashMap;
import java.util.Map;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;

/**
 * The ClassType is necessary, if we are going to represent ModelTypeType.getInstanceClass
 * using our framework.
 * @author D042678
 *
 */
public class ClassType extends JavaSimpleType<Class>
{
    private static final long serialVersionUID = 922667817814819939L;
    private static Map<String, Class> _primitiveNameToClass = new HashMap<String, Class>();
    static {
        _primitiveNameToClass.put("boolean", boolean.class);
        _primitiveNameToClass.put("byte", byte.class);
        _primitiveNameToClass.put("byte[]", byte[].class);
        _primitiveNameToClass.put("char", char.class);
        _primitiveNameToClass.put("double", double.class);
        _primitiveNameToClass.put("float", float.class);
        _primitiveNameToClass.put("int", int.class);
        _primitiveNameToClass.put("long", long.class);
        _primitiveNameToClass.put("short", short.class);        
    }


    ClassType() {
		super(URINamePair.CLASS,Class.class);
	}
	public Class convertFromJavaClass(Object data) {
        if (data == null) {
            return null;
        }
		if (data instanceof Class) {
			return (Class)data;
		} else if (data instanceof String) {
			try {
				return internLoadClass((String)data);
			} catch (ClassNotFoundException e) { //$JL-EXC$
				throw new IllegalArgumentException(data.toString()+" is not a class name");
			}
		}
        return convertFromWrapperOrEx(data);
	}

	public <T> T convertToJavaClass(Class data, Class<T> targetType) {
		if (data == null) {
			return null;
		}
		if (targetType == Class.class) {
			return (T)data;
		}
		if (targetType == String.class) {
			return (T)data.getName();
		}
        return convertToWrapperOrEx(data, targetType);
	}
    
    public static Class internLoadClass(String name) throws ClassNotFoundException {
        if (name.length() < 8) { //this is much faster than the lookup, because condition is false most times
            Class primitive = _primitiveNameToClass.get(name);
            if (primitive != null) {
                return primitive;
            }
        }
        return SapHelperProviderImpl.getClassLoader().loadClass(name);
    }

}
