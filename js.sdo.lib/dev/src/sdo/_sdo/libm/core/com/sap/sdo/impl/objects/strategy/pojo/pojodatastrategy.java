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
import java.util.Map;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.DataStrategy;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.strategy.enhancer.AbstractPojoDataStrategy;
import com.sap.sdo.impl.types.SdoProperty;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class PojoDataStrategy extends AbstractPojoDataStrategy {
    
    private static final long serialVersionUID = -2758708126069385621L;
    private final Map<Object, DataObjectDecorator> _pojoToDataObject;

    public PojoDataStrategy(Object pPojo, Map<Object, DataObjectDecorator> pPojoToDataObject) {
        super(pPojo);
        _pojoToDataObject = pPojoToDataObject;
    }
    
    public DataObject createDataObject(Property property, Type pType) {
        // TODO
        throw new UnsupportedOperationException("createDataObject");
    }

    @Override
    public void setDataObject(GenericDataObject pDataObject) {
        super.setDataObject(pDataObject);
        if (_pojoToDataObject != null) {
        	_pojoToDataObject.put(_pojo, pDataObject);
        }
    }

    public PropValue<?> getPropValue(int pIndex, Property pProperty, boolean pCreate) {
        return createPropValue(pIndex, pProperty);
    }

    @Override
    public void addOpenProperty(Property pProperty) {
        throw new UnsupportedOperationException("addOpenProperty");
    }

    @Override
    public void reactivateOpenPropValue(PropValue<?> pPropValue) {
        throw new UnsupportedOperationException("reactivateOpenPropValue");
    }

    @Override
    public PropValue<?> createPropValue(int pPropIndex, Property pProperty) {
        SdoProperty property = (SdoProperty)(pProperty!=null?pProperty:getPropertyByIndex(pPropIndex));
        if (property.isMany()) {
            return new PojoPropMultiValue(this, property);
        }
        return new PojoPropSingleValue(this, property);
    }

    @Override
    public void removeOpenPropValue(int pPropertyIndex) {
        throw new UnsupportedOperationException("removeOpenPropValue");
    }

    public DataStrategy refineType(Type pOldType, Type pNewType) {
        return this;
    }

    public DataObjectDecorator getDataObject(Object pPojo, PropValue<?> pPropValue) {
        if (pPojo == null) {
            return null;
        }
        if (_pojoToDataObject != null) {
        	DataObjectDecorator dataObject = _pojoToDataObject.get(pPojo);
        	if (dataObject != null) {
        		return dataObject.getInstance().getFacade();
        	}
        }
        DataObjectDecorator dataObject = (DataObjectDecorator)PojoDataObjectFactory.createDataObject(pPropValue.getProperty().getType(), pPojo, _pojoToDataObject);
        if (pPropValue.getProperty().isContainment()) {
        	dataObject.getInstance().setContainerWithoutCheck(pPropValue);
        }
        dataObject = dataObject.getInstance().getFacade();
        
        if (_pojoToDataObject != null) {
        	_pojoToDataObject.put(pPojo,dataObject);
        }        
        return dataObject;
    }

    public Object getPojo() {
        return _pojo;
    }

	@Override
	protected String getSetterString() {
		return "set";
	}
    
    @Override
    public List<Property> getOpenProperties() {
        return null;
    }
    @Override
    public void setOpenProperties(ArrayList<Property> pOpenProperties) {
        throw new UnsupportedOperationException("setOpenProperties");
    }
}
