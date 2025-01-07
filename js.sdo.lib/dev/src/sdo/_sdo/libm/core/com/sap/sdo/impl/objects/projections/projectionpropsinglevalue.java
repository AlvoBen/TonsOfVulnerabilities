package com.sap.sdo.impl.objects.projections;

import java.io.Serializable;

import com.sap.sdo.api.helper.SapDataFactory;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;

public class ProjectionPropSingleValue implements PropValue, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6763322282043736793L;
    private final PropValue<?> _delegate;
    private final Property _property;
    private final ProjectionDataStrategy _strategy;
    public ProjectionPropSingleValue(ProjectionDataStrategy strategy, Property prop, PropValue<?> delegate) {
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

    public Object getConvertedValue(Class pClass) {
        Object value = _delegate.getConvertedValue(pClass);
        if (value instanceof DataObjectDecorator) {
            return ((SapDataFactory)getContext().getDataFactory()).project((DataObject)value);
        }
        return value;
    }

    public Object getConvertedValue(int pIndex, Class pClass) {
        Object ret = _delegate.getConvertedValue(pIndex, pClass);
        if (ret instanceof DataObjectDecorator) {
            ret = ((SapDataFactory)getContext().getDataFactory()).project((DataObject)ret);
        }
        return ret;
    }

    public GenericDataObject getDataObject() {
        return _strategy.getDataObject();
    }

    public int getIndexByPropertyValue(String propName, Object value) {
        return _delegate.getIndexByPropertyValue(propName, value);
    }

    public Object getOldValue() {
        return _delegate.getOldValue();
    }

    public SdoProperty getProperty() {
        return (SdoProperty)_property;
    }

    public Object getValue() {
        Object ret = _delegate.getValue();
        if (_property.getType().isDataType()) {
            Object ret2 = ((SdoType)_property.getType()).convertFromJavaClass(ret);
            return ret2;
        }
        if (ret instanceof DataObjectDecorator) {
            return ((SapDataFactory)getContext().getDataFactory()).project((DataObject)ret);
        }
        return ret;
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
