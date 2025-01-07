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
package com.sap.sdo.impl.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.builtin.PropertyType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;

public class EqualityHelperImpl implements EqualityHelper
{
    private final HelperContext _helperContext;
    
    private EqualityHelperImpl(HelperContext pHelperContext) {
        _helperContext = pHelperContext;
    }

    public static EqualityHelper getInstance(HelperContext pHelperContext) {
        // to avoid illegal instances
        EqualityHelper equalityHelper = pHelperContext.getEqualityHelper();
        if (equalityHelper != null) {
            return equalityHelper;
        }
        return new EqualityHelperImpl(pHelperContext);
    }
    
    public boolean equal(DataObject pDataObject1, DataObject pDataObject2) {
        return equal(pDataObject1,pDataObject2,new HashMap<GenericDataObject, Set<GenericDataObject>>(),false);
    }

    public boolean equalShallow(DataObject pDataObject1, DataObject pDataObject2) {
        return equal(pDataObject1,pDataObject2,new HashMap<GenericDataObject, Set<GenericDataObject>>(),true);
    }

    private boolean equal(DataObject pDataObject1, DataObject pDataObject2, 
        Map<GenericDataObject, Set<GenericDataObject>> pComparedPairs, boolean pShallow) {
        if (pDataObject1 == pDataObject2) {
            return true;
        }
        GenericDataObject dataObject1 = ((DataObjectDecorator)pDataObject1).getInstance();
        GenericDataObject dataObject2 = ((DataObjectDecorator)pDataObject2).getInstance();
        if (dataObject1 == dataObject2) {
            return true;
        }
        if (!pShallow) {
            Set<GenericDataObject> compared = pComparedPairs.get(dataObject1);
            if (compared == null) {
                compared = new HashSet<GenericDataObject>();
                pComparedPairs.put(dataObject1, compared);
            }
            if (compared.contains(dataObject2)) {
                return true;
            } else {
                compared.add(dataObject2);
            }
        }
        if (dataObject1.getType() != dataObject2.getType()) {
            return false;
        }
        if (!equalSequence(dataObject1,dataObject2,pComparedPairs,pShallow)) {
            return false;
        }
        return equalProperties(dataObject1,dataObject2,pComparedPairs,pShallow);
    }
    
    private boolean equalSequence(GenericDataObject pDataObject1, GenericDataObject pDataObject2,
        Map<GenericDataObject, Set<GenericDataObject>> pComparedPairs, boolean pShallow) {

        if (!pDataObject1.getType().isSequenced()) {
            return true;
        }
        Sequence s1 = pDataObject1.getSequence();
        Sequence s2 = pDataObject2.getSequence();
        if (s1.size() != s2.size()) {
            return false;
        }
        for (int i=0; i < s1.size(); i++) {
            Property property1 = s1.getProperty(i);
            if (!equalProperty(property1, s2.getProperty(i), pComparedPairs)) {
                return false;
            }
            if (property1 == null || property1.getType().isDataType()) {
                if (!equalDataType(s1.getValue(i), s2.getValue(i))) {
                    return false;
                }
            } else if (!pShallow  && ((property1.getOpposite()==null) || (!property1.getOpposite().isContainment()))) {
                DataObject c1 = (DataObject) s1.getValue(i);
                DataObject c2 = (DataObject) s2.getValue(i);
                if (!equal(c1,c2,pComparedPairs,false)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean equalProperties(GenericDataObject pDataObject1, GenericDataObject pDataObject2,
        Map<GenericDataObject, Set<GenericDataObject>> pComparedPairs, boolean pShallow) {
        List<Property> ip1 = pDataObject1.getInstanceProperties();
        List<Property> ip2 = pDataObject2.getInstanceProperties();
        if (ip1.size()!=ip2.size()) {
            return false;
        }
        List<Property> typeProperties = pDataObject1.getType().getProperties();
        for (Property property: typeProperties) {
            if (!equalProperty(pDataObject1, property, pDataObject2, property, pComparedPairs, pShallow)) {
                return false;
            }
        }
        // To handle open types, we need to map over the name.
        for (int i = typeProperties.size(); i < ip1.size(); i++) {
            Property property1 = ip1.get(i);
            Property property2 = pDataObject2.getInstanceProperty(property1.getName());
            if (!equalProperty(pDataObject1, property1, pDataObject2, property2, pComparedPairs, pShallow)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean equalProperty(GenericDataObject pDataObject1, Property pProperty1, 
        GenericDataObject pDataObject2, Property pProperty2,
        Map<GenericDataObject, Set<GenericDataObject>> pComparedPairs, boolean pShallow) {
        if (pDataObject1.getType().isSequenced() && ((SdoProperty)pProperty1).isXmlElement()) {
            // Don't check twice sequenced properties
            return true;
        }
        if (!equalProperty(pProperty1, pProperty2, pComparedPairs)) {
            return false;
        }
        if (pShallow && !pProperty1.getType().isDataType()) {
            return true;
        }
        if (pDataObject1.isSet(pProperty1)) {
            if (URINamePair.CHANGESUMMARY_TYPE.equalsUriName(pProperty1.getType())
                    && URINamePair.CHANGESUMMARY_TYPE.equalsUriName(pProperty2.getType())) {
                // do not check against old values
                return true;
            }
            if (!pDataObject2.isSet(pProperty2)) {
                return false;
            }
            if (pProperty1.getType().isDataType()) {
                Object value1 = pDataObject1.get(pProperty1);
                Object value2 = pDataObject2.get(pProperty2);
                return equalDataType(value1, value2);
            }
            if (!pShallow  && ((pProperty1.getOpposite()==null) || (!pProperty1.getOpposite().isContainment()))) {
                if (pProperty1.isMany()) {
                    List<DataObject> list1 = pDataObject1.getList(pProperty1);
                    List<DataObject> list2 = pDataObject2.getList(pProperty2);
                    if (list1.size() != list2.size()) {
                        return false;
                    }
                    for (int i = 0; i < list1.size(); i++) {
                        DataObject c1 = list1.get(i);
                        DataObject c2 = list2.get(i);
                        return equal(c1,c2,pComparedPairs,false);                        
                    }
                } else {
                    DataObject c1 = pDataObject1.getDataObject(pProperty1);
                    DataObject c2 = pDataObject2.getDataObject(pProperty2);
                    return equal(c1,c2,pComparedPairs,false);
                }
            }
            // TODO:  Check that this is OK.
            return true;
        }
        return  !pDataObject2.isSet(pProperty2);
    }

    private boolean equalDataType(Object value1, Object value2) {
        if (value1 == null) {
            return (value2 == null);
        }
        if (value1 instanceof byte[]) {
            return equalBytes((byte[])value1, (byte[])value2);
        }
        return value1.equals(value2);
    }
    
    private boolean equalBytes(byte[] pBytes1, byte[] pBytes2) {
        if (pBytes1.length != pBytes2.length) {
            return false;
        }
        for (int i = 0; i < pBytes1.length; i++) {
            if (pBytes1[i] != pBytes2[i]) {
                return false;
            }
        }
        return true;
    }
    
    private boolean equalProperty(Property pProperty1, Property pProperty2, Map<GenericDataObject, Set<GenericDataObject>> pComparedPairs) {
        if (pProperty1 == pProperty2) {
            return true;
        }
        if ((pProperty1 == null) || (pProperty2 == null)) {
            return false;
        }
        // only equal, if it is an open Property
        if (!pProperty1.isOpenContent() || !pProperty2.isOpenContent()) {
            return false;
        }
        List<Property> typeProperties = PropertyType.getInstance().getProperties();
        DataObject propObj1 = (DataObject)pProperty1;
        DataObject propObj2 = (DataObject)pProperty2;
        for (Property property: typeProperties) {
            if (property.getType().isDataType()) {
                if (!equalDataType(propObj1.get(property), propObj2.get(property))) {
                    return false;
                }
            }
        }
        return true;
    }
    
}
