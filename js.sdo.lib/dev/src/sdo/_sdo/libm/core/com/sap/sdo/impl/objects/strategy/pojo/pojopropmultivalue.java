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
package com.sap.sdo.impl.objects.strategy.pojo;

import java.util.ArrayList;
import java.util.List;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.strategy.AbstractNonSequencedPropMultiValue;

import commonj.sdo.Property;

public class PojoPropMultiValue extends AbstractNonSequencedPropMultiValue {
    private static final long serialVersionUID = 7051720075963934156L;

    protected PojoPropMultiValue(PojoDataStrategy pDataStrategy, Property property) {
        super(pDataStrategy, property);
    }

    @Override
    protected void internAdd(int pIndex, Object pValue) {
        getOrCreatePojoList().add(pIndex, unwrapPojo(pValue));
    }

    @Override
    protected void internAdd(Object pValue) {
        getOrCreatePojoList().add(unwrapPojo(pValue));
    }

    @Override
    protected void internClear() {
        List<Object> pojoList = getPojoList();
        if (pojoList != null) {
            pojoList.clear();
        }
    }

    @Override
    protected void internRemove(int pIndex) {
        getPojoList().remove(pIndex);
    }

    @Override
    public int size() {
        List<Object> pojoList = getPojoList();
        if (pojoList == null) {
            return 0;
        }
        return pojoList.size();
    }

    @Override
    protected void internSet(int pIndex, Object pValue) {
        getOrCreatePojoList().set(pIndex, unwrapPojo(pValue));
    }
    
    @Override
    public Object get(int pIndex) {
        Object pojoValue = getPojoList().get(pIndex);
        if (getProperty().getType().isDataType()) {
            return pojoValue;
        }
        return ((PojoDataStrategy)_dataStrategy).getDataObject(pojoValue, this);
    }
    
    private List<Object> getPojoList() {
        return (List<Object>)((PojoDataStrategy)_dataStrategy).getPojoValue(getProperty());
    }
    
    private List<Object> getOrCreatePojoList() {
        List<Object> pojoList = getPojoList();
        if (pojoList == null) {
            pojoList = new ArrayList<Object>();
            ((PojoDataStrategy)_dataStrategy).setPojoValue(getProperty(), pojoList);
        }
        return pojoList;
    }

    private Object unwrapPojo(Object pValue) {
        if (pValue == null) {
            return null;
        }
        if (getProperty().getType().isDataType()) {
            return pValue;
        }
        final GenericDataObject dataObject = ((DataObjectDecorator)pValue).getInstance();
        PojoDataStrategy dataStrategy = (PojoDataStrategy)dataObject.getDataStrategy();
        return dataStrategy.getPojo();
    }

    public void trimMemory() {
    }

}