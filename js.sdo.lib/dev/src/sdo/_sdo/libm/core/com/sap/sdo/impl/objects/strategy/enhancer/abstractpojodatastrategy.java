package com.sap.sdo.impl.objects.strategy.enhancer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.strategy.AbstractDataStrategy;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.PropertyType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;

public abstract class AbstractPojoDataStrategy extends AbstractDataStrategy {

    private static final long serialVersionUID = 3884120166893946979L;
    protected Object _pojo;
	private static final String GETTER_MAP = "propertyKeyToGetterMap";
	private static final String SETTER_MAP = "propertyKeyToSetterMap";
	private static final String FIELD_MAP = "propertyKeyToFieldMap";

	public AbstractPojoDataStrategy() {
		super();
	}

	public AbstractPojoDataStrategy(Object pojo) {
		_pojo = pojo;
	}

	private <F> Map<String,F> getPropertyKeyToGetterMap(String map) {
		SdoType t = (SdoType)getDataObject().getType();
		Map<String,F> ret = (Map<String,F>)t.getExtraData(getClass().getName(), map);
		if (ret == null) {
			ret = new HashMap<String,F>();
			t.putExtraData(getClass().getName(), map, ret);
		}
		return ret;
	}
	private boolean isField(Property pProperty) {
		return ((DataObject)pProperty).getBoolean(PropertyType.getJavaFieldProperty());
	}
	public Object getPojoValue(Property pProperty) {
		Object ret;
		if (isField(pProperty)) {
			ret = getPojoValueFromField(pProperty);
		} else {
			ret = getPojoValueFromBeanProperty(pProperty);
		}
		return ret;
	}

	private Object getPojoValueFromBeanProperty(Property pProperty) {
	    Method getter = getGetterOfProperty(pProperty);
	    try {
	        return getter.invoke(_pojo);
	    } catch (IllegalAccessException e) {
	        throw new IllegalArgumentException("Getter " + getter.getName(), e);
	    } catch (InvocationTargetException e) {
	        throw new IllegalArgumentException("Getter " + getter.getName(), e);
	    }
	}

	private Method getGetterOfProperty(Property pProperty) {
		final String key = getPropertyKey(pProperty);
	    Map<String,Method> map = getPropertyKeyToGetterMap(GETTER_MAP);
	    Method getter = map.get(key);
	    if (getter == null) {
	        String javaName = ((SdoProperty)pProperty).getJavaName();
	        if (javaName == null) {
	            javaName = pProperty.getName();
	        }
	        javaName = Character.toUpperCase(javaName.charAt(0)) + javaName.substring(1);
	        String methodName = "get" + javaName;
	        try {
	            getter = _pojo.getClass().getMethod(methodName);
	        } catch (NoSuchMethodException e) {
	            methodName = "is" + javaName;
	            try {
	                getter = _pojo.getClass().getMethod(methodName);
	            } catch (NoSuchMethodException nsme) {
	                throw new IllegalArgumentException("No getter for " + javaName, nsme);
	            }
	        }
	        map.put(key, getter);
	    }
		return getter;
	}

	private Object getPojoValueFromField(Property pProperty) {
	    Field field = getFieldOfProperty(pProperty);
	    try {
            //TODO allowing access to a private field if it was annotated
            //check JLin rules!!!
	    	//field.setAccessible(true);
	        return field.get(_pojo);
	    } catch (IllegalAccessException e) {
	        throw new IllegalArgumentException("Getter " + field.getName(), e);
	    }
	}

	public void setPojoValue(Property pProperty, Object pValue) {
		if (((DataObject)pProperty).getBoolean(PropertyType.IS_FIELD)) {
			setPojoValueToField(pProperty, pValue);
		} else {
			setPojoValueToBeanProperty(pProperty, pValue);
		}
	}
	protected abstract String getSetterString();
	public void setPojoValueToBeanProperty(Property pProperty, Object pValue) {
	    final String key = getPropertyKey(pProperty);
	    Map<String,Method> map = getPropertyKeyToGetterMap(SETTER_MAP);
	    Method setter = map.get(key);
	    if (setter == null) {
	        String javaName = ((SdoProperty)pProperty).getJavaName();
	        if (javaName == null) {
	            javaName = pProperty.getName();
	        }
	        javaName = Character.toUpperCase(javaName.charAt(0)) + javaName.substring(1);
	        String methodName = getSetterString() + javaName;
	        try {
	            setter = _pojo.getClass().getMethod(methodName, getGetterOfProperty(pProperty).getReturnType());
	        } catch (NoSuchMethodException e) {
	            throw new IllegalArgumentException("No setter for " + javaName, e);
	        }
	        map.put(key, setter);
	    }
	    try {
	    	
	    	Object value = convertToPojoValue(pValue, pProperty, setter.getParameterTypes()[0]);
	        setter.invoke(_pojo, value);
	    } catch (Exception e) {
	        throw new IllegalArgumentException("Setter " + setter.getName() + "(" + pValue + ")", e);
	    }
	}
	private Object convertToPojoValue(Object value, Property pProperty, Class<?> target) { 
		if (pProperty.isMany()) {
            return value;
		}
        return convertToPojoSingleValue(value, pProperty, target);		
	}
	protected Object convertToPojoSingleValue(Object value, Property pProperty, Class<?> target) { 
        SdoType propType = ((SdoType)pProperty.getType());
		if (value==null && propType.isDataType() && pProperty.getType().getInstanceClass()!=null
				&& pProperty.getType().getInstanceClass().isPrimitive()) {
			// TODO:  other simple types.
			return 0;
		}
		HelperContext ctx = getHelperContext();
    	if (value instanceof DataObjectDecorator) {
    		return ((DataObjectDecorator)value).getInstance().project(ctx);
    	} else if (propType.isDataType()){
            //    		value = ctx.getDataHelper().convert(pProperty, value);
            Class nonPrimitive = nonPrimitive(target);
            if (nonPrimitive.isInstance(value)) {
                return value;
            }
            Object normValue = propType.convertFromJavaClass(value);
            return propType.convertToJavaClass(normValue, nonPrimitive);
    	}
    	return value;
	}

	private Class nonPrimitive(Class<?> methodClass) {
        if (methodClass.isPrimitive()) {
            if (methodClass==Boolean.TYPE) {
                methodClass = Boolean.class;
            } else if (methodClass==Integer.TYPE) {
                methodClass = Integer.class;
            } else if (methodClass==Character.TYPE) {
                methodClass = Character.class;
            } else if (methodClass==Byte.TYPE) {
                methodClass = Byte.class;
            } else if (methodClass==Short.TYPE) {
                methodClass = Short.class;
            } else if (methodClass==Long.TYPE) {
                methodClass = Long.class;
            } else if (methodClass==Float.TYPE) {
                methodClass = Float.class;
            } else if (methodClass==Double.TYPE) {
                methodClass = Double.class;
            } else {
                throw new IllegalArgumentException("cannot access getter with return type " + methodClass.getName());
            }
            return methodClass;
        }
        return methodClass;

	}

	public void setPojoValueToField(Property pProperty, Object pValue) {
	    Field field = getFieldOfProperty(pProperty);
	    try {
	    	Object value = convertToPojoValue(pValue, pProperty, field.getType());
	        field.set(_pojo, value);
	    } catch (Exception e) {
	        throw new IllegalArgumentException("Setter " + field.getName() + "(" + pValue + ")", e);
	    }
	}

	private Field getFieldOfProperty(Property pProperty) {
		final String key = getPropertyKey(pProperty);
	    Map<String,Field> map = getPropertyKeyToGetterMap(FIELD_MAP);
	    Field field = map.get(key);
	    if (field == null) {
	        String javaName = ((SdoProperty)pProperty).getJavaName();
	        if (javaName == null) {
	            javaName = pProperty.getName();
	        }
	        try {
	            field = _pojo.getClass().getField(javaName);
	        } catch (NoSuchFieldException e) {
	            throw new IllegalArgumentException("No setter for " + javaName, e);
	        }
	        map.put(key, field);
	        
	    }
		return field;
	}

	private String getPropertyKey(Property pProperty) {
	    return _pojo.getClass().getSuperclass().getName() + '.' + pProperty.getName();
	}
	public Class getTargetClass(Property pProperty) {
		if (isField(pProperty)) {
				return getTargetClassFromField(pProperty);
		}
		return getTargetClassFromBeanProperty(pProperty);
	}
	private Class getTargetClassFromBeanProperty(Property property) {
		return calculateElementType(getGetterOfProperty(property).getGenericReturnType());
	}

	private Class calculateElementType(Type t) {
    	if (t instanceof ParameterizedType) {
    		if (((ParameterizedType)t).getRawType().equals(List.class)) {
    			java.lang.reflect.Type arg = ((ParameterizedType)t).getActualTypeArguments()[0];
    			if (arg instanceof Class) {
    				return (Class)arg;
    			}
    		}
    	}
		return (Class)t;
	}

	private Class getTargetClassFromField(Property property) {
		return calculateElementType(getFieldOfProperty(property).getGenericType());
	}

	public Object getPojo() {
		return _pojo;
	}

	public void setPojo(Object pojo) {
		_pojo = pojo;
	}

}