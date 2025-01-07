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

import javax.xml.namespace.QName;

import com.sap.sdo.api.util.URINamePair;

import commonj.sdo.DataObject;

public class URISimpleType extends JavaSimpleType<String>
{
    private static final long serialVersionUID = -322455299854208547L;

    URISimpleType() {
        super(URINamePair.URI, String.class);
    }

    public String convertFromJavaClass(Object data) {
        if (data==null) {
            return null;
        }        
        if (data instanceof DataObject) {
            return convertFromWrapperOrEx(data);
        }
        if (data instanceof URINamePair) {
        	URINamePair unp = (URINamePair)data;
        	return unp.toStandardSdoFormat();
        }
        if (data instanceof QName) {
        	QName unp = (QName)data;
        	return unp.getNamespaceURI()+'#'+unp.getLocalPart();
        }
        return data.toString();
    }

    public <T> T convertToJavaClass(String data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType == String.class) {
            return (T)data;
        }
        if (targetType == QName.class) {
        	URINamePair unp = URINamePair.fromStandardSdoFormat(data);
        	return (T)new QName(unp.getURI(), unp.getName());
        }
        if (targetType == URINamePair.class) {
        	return (T)URINamePair.fromStandardSdoFormat(data);
        }
        return convertToWrapperOrEx(data, targetType);
    }

}
