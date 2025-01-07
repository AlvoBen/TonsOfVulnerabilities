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

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;

import commonj.sdo.DataObject;

public class ObjectSimpleType extends JavaSimpleType<Object>
{
    private static final long serialVersionUID = 4224326168203787682L;

    ObjectSimpleType() {
        super(URINamePair.OBJECT, Object.class);
    }

    public Object convertFromJavaClass(Object data) {
        if (data instanceof DataObject) {
            return convertFromWrapperOrEx(data);
        }
        return data;
    }

    public <T> T convertToJavaClass(Object data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType.isInstance(data)) {
            return (T)data;
        }
        if (targetType!=DataObject.class) {
            TypeHelperImpl typeHelper = (TypeHelperImpl)getHelperContext().getTypeHelper();
            SdoType dataType = typeHelper.getResolvedType(data.getClass());
            if (dataType == null || dataType == this) {
                if (targetType == String.class) {
                    return (T)data.toString();
                }
            } else {
                return (T)dataType.convertToJavaClass(data, targetType);
            }
        }
        return convertToWrapperOrEx(data, targetType);
    }
    
    @Override
    public Object copy(Object o, boolean shallow) {
        // TODO try to identify a type.
        // simply return...
        return o;
    }

}
