package com.sap.sdo.impl.objects.projections;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

import com.sap.sdo.api.helper.SapDataFactory;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;

public class ProjectionPropMultiValue extends AbstractList<Object> implements PropValue<List<Object>>, RandomAccess, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6763322282043736793L;
    private final PropValue<List<Object>> _delegate;
    private final Property _property;
    private final ProjectionDataStrategy _strategy;
    public ProjectionPropMultiValue(ProjectionDataStrategy strategy, Property prop, PropValue<List<Object>> delegate) {
        _delegate = delegate;
        _strategy = strategy;
        _property = prop;
    }
    private HelperContext getContext() {
        return ((SdoType)_strategy.getDataObject().getType()).getHelperContext();
    }
    public void checkSaveOldValue() {
        _delegate.checkSaveOldValue();
    }

    public <C> C getConvertedValue(Class<C> pClass) {
        if ((pClass == List.class) || (pClass == Object.class)) {
            return (C)getValue();
        }
        int size = size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return getConvertedValue(0, pClass);
        }
        throw new ClassCastException("Can not convert list of size " + size + " to " + pClass.getName());
    }

    public <C> C getConvertedValue(int pIndex, Class<C> pClass) {
        C ret = _delegate.getConvertedValue(pIndex, pClass);
        if (ret instanceof DataObjectDecorator) {
            ret = (C)((SapDataFactory)getContext().getDataFactory()).project((DataObject)ret);
        }
        return ret;
    }

    public GenericDataObject getDataObject() {
        return _strategy.getDataObject();
    }

    public int getIndexByPropertyValue(String propName, Object value) {
        return _delegate.getIndexByPropertyValue(propName, value);
    }

    public List<Object> getOldValue() {
        //TODO project the old values
        return _delegate.getOldValue();
    }

    public SdoProperty getProperty() {
        return (SdoProperty)_property;
    }

    public List getValue() {
        return this;
    }

    public void internAttachOpposite(DataObjectDecorator pDataObject) {
        _delegate.internAttachOpposite(pDataObject);
    }

    public void internDetachOpposite(DataObjectDecorator pDataObject) {
        _delegate.internDetachOpposite(pDataObject);
    }

    public boolean isMany() {
        return _delegate.isMany();
    }

    public boolean isModified() {
        return _delegate.isModified();
    }

    public boolean isReadOnly() {
        return _delegate.isReadOnly();
    }

    public boolean isSet() {
        return _delegate.isSet();
    }
    
    @Override
    public void add(int pIndex, Object pValue) {
        _delegate.getValue().add(pIndex, pValue);
    }
    
    @Override
    public Object get(int pIndex) {
        Object ret = _delegate.getValue().get(pIndex);
        return projectSingleValue(ret);
    }
    private Object projectSingleValue(Object pValue) {
        if (_property.getType().isDataType()) {
            return ((SdoType)_property.getType()).convertFromJavaClass(pValue);
        }
        if (pValue instanceof DataObjectDecorator) {
            return ((SapDataFactory)getContext().getDataFactory()).project((DataObject)pValue);
        }
        return pValue;
    }
    
    @Override
    public Object remove(int pIndex) {
        Object ret = _delegate.getValue().remove(pIndex);
        return projectSingleValue(ret);
    }
    
    @Override
    public int size() {
        return _delegate.getValue().size();
    }
    public boolean remove(Object pValue) {
        return _delegate.remove(pValue);
    }
    public Object set(int pIndex, Object pValue) {
        return _delegate.set(pIndex, pValue);
    }

    public void setValue(Object pValue) {
        _delegate.setValue(pValue);
    }

    public void unset() {
        _delegate.unset();
    }

    public void trimMemory() {
    }

}
