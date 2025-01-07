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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;

public class ListSimpleType extends JavaSimpleType<List> {
    private static final long serialVersionUID = 366613288878494755L;
    private final SdoType _itemType;
    private URINamePair _xsdType;
    private final HelperContext _helperContext;
    private DataObject _dataObject;
    private static Property _itemTypeProperty;

    public ListSimpleType(SdoType pItemType, HelperContext pHelperContext) {
        this(pItemType, new URINamePair(pItemType.getURI(), pItemType.getName() + 's'), pHelperContext);
    }
    
    public ListSimpleType(SdoType pItemType, URINamePair pUnp, HelperContext pHelperContext) {
        super(pUnp, List.class);
        if (pItemType.getName() != null && !pItemType.isDataType()) {
            //if the type is initialized and no data type
            throw new IllegalArgumentException("cannot create simple type list from complex type " + pItemType.getQName().toStandardSdoFormat());
        }
        _itemType = pItemType;
        _helperContext = pHelperContext;
    }
    
    public ListSimpleType(HelperContext pHelperContext, DataObject pTypeObject) {
        this((SdoType)pTypeObject.get(getItemTypeProperty()), new URINamePair(pTypeObject.getString(TypeType.URI), pTypeObject.getString(TypeType.NAME)), pHelperContext);
        //TODO is the type defined???
        String xsdType = pTypeObject.getString(PropertyType.getXsdTypeProperty());
        if (xsdType != null) {
            _xsdType = URINamePair.fromStandardSdoFormat(xsdType);
        }
        _dataObject = pTypeObject;
    }
    
    public SdoType getItemType() {
        return _itemType;
    }
    public List convertFromJavaClass(Object data) {
        if (data==null) {
            return null;
        }        
        if (data instanceof List) {
            // make a copy of the List to avoid side effects
            return new ArrayList((List)data);
        }
        if (data instanceof String) {
            List l = new ArrayList();
            StringTokenizer tokenizer = new StringTokenizer((String)data);
            while (tokenizer.hasMoreTokens()) {
                l.add(_itemType.convertFromJavaClass(tokenizer.nextToken()));
            }
            return l;
        }
        return convertFromWrapperOrEx(data);
    }

    public <T> T convertToJavaClass(List data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType.equals(List.class)) {
            return (T)data;
        }
        if (targetType.equals(String.class)) {
            StringBuilder buf = new StringBuilder();
            for (Object o: data) {
                if (buf.length() > 0) {
                    buf.append(" ");
                }
                buf.append(_itemType.convertToJavaClass(o, String.class));
            }
            return (T)buf.toString();
        }
        return convertToWrapperOrEx(data, targetType);
    }
    
    @Override
    public HelperContext getHelperContext() {
        return _helperContext;
    }

    @Override
    public boolean isAbstract() {
        return true;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public List copy(List o, boolean shallow) {
        return new ArrayList(o);
    }
    
    @Override
    public Object readResolve() {
        Object o = super.readResolve();
        if (o == null) {
            o = this;
        }
        return o;
    }

    public URINamePair getXsdType() {
        return _xsdType;
    }
    
    public void setXsdType(URINamePair pXsdType) {
        _xsdType = pXsdType;
    }

    public DataObject getTypeDataObject() {
        if (_dataObject == null) {
            _dataObject = _helperContext.getDataFactory().create(TypeType.getInstance());
            _dataObject.setString(TypeType.URI, getURI());
            _dataObject.setString(TypeType.NAME, getName());
            _dataObject.setBoolean(TypeType.DATA_TYPE, true);
            _dataObject.set(TypeType.getJavaClassProperty(), getInstanceClass().getName());
            _dataObject.set(getItemTypeProperty(), _itemType);
            if (_xsdType != null) {
                _dataObject.set(PropertyType.getXsdTypeProperty(), _xsdType);
            }
        }
        return _dataObject;
    }

    public static Property getItemTypeProperty() {
        if (_itemTypeProperty == null) {
            _itemTypeProperty = SapHelperProviderImpl.getCoreContext().getTypeHelper()
                .getOpenContentProperty(URINamePair.CTX_URI, "itemType");
        }
        return _itemTypeProperty;
    }
}
