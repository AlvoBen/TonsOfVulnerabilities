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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.sap.sdo.api.util.URINamePair;

public class BooleanObjectSimpleType extends JavaSimpleType<Boolean>
{
    private static final long serialVersionUID = 8593480832525116031L;

    private Map<Class, Object> trueMap = new HashMap<Class, Object>();
    private Map<Class, Object> falseMap = new HashMap<Class, Object>();
    
    BooleanObjectSimpleType() {
        this(URINamePair.BOOLEANOBJECT, Boolean.class);
    }
    
    BooleanObjectSimpleType(URINamePair unp, Class<Boolean> clz) {
        super(unp, clz);
    }

    public Boolean convertFromJavaClass(Object data) {
        if (data==null) {
            return getDefaultValue();
        }        
        if (data instanceof Boolean) {
            return ((Boolean) data);
        }
        if (data instanceof String) {
            if (data.equals("1")) { // see JIRA SDO-21
                return true;
            }
            return Boolean.valueOf(((String)data).trim());
        }
        if (data instanceof Number) { // see JIRA SDO-21
            // handle all number types in same manner
            return !(((Number)data).doubleValue() == 0d);
        }
        if (data instanceof Character) { // see JIRA SDO-21
            return !(((Character)data).charValue() == '0');
        }
        return convertFromWrapperOrEx(data);
    }

    public <T> T convertToJavaClass(Boolean data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        T result = (T)getResultMap(data).get(targetType);
        if (result != null) {
            return result;
        }
        if (targetType==Boolean.class) {
            result = (T)data;
        } else if (targetType==String.class) {
            result = (T)data.toString();
        } else if (Number.class.isAssignableFrom(targetType)) { // see JIRA SDO-21
            // handle all number types in same manner
            try {
                Constructor<T> constructor = targetType.getConstructor(String.class);
                if (data) {
                    result = constructor.newInstance("1");
                } else {
                    result = constructor.newInstance("0");
                }
            } catch (Exception e) {
                // this can never happen
                throw new IllegalArgumentException(e);
            }
        } else if (targetType==Character.class) { // see JIRA SDO-21
            if (data) {
                result = (T)Character.valueOf('1');
            } else {
                result = (T)Character.valueOf('0');
            }
        }
        if (result != null) {
            getResultMap(data).put(targetType, result);
            return result;            
        }
        return convertToWrapperOrEx(data, targetType);
    }
    
    protected Map<Class, Object> getResultMap(Object pBoolean) {
        if ((Boolean)pBoolean) {
            return trueMap;
        }
        return falseMap;
    }
    
    public JavaSimpleType<Boolean> getNillableType() {
        return BOOLEANOBJECT;
    }
}
