package com.sap.sdo.impl.objects.projections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.strategy.enhancer.WrapsDataStrategy;
import com.sap.sdo.impl.types.SdoProperty;

import commonj.sdo.Property;

public class DelegatingPropSingleValue implements PropValue<Object>, Serializable {

    private static final long serialVersionUID = 8436685123438502858L;
    private final DelegatingDataStrategy _strategy;
    private final PropValue<Object> _main;
    private List<PropValue> getProjections() {
        List<PropValue> projections = new ArrayList<PropValue>();
        int index = _strategy.indexOfProperty(getProperty(), false);
        boolean openProperty = getProperty().isOpenContent();
        for (GenericDataObject gdo: _strategy.getProjections()) {
            if (gdo.getFacade() instanceof WrapsDataStrategy) {
                ProjectionDataStrategy projectionDataStrategy = (ProjectionDataStrategy)gdo.getDataStrategy();
                AbstractProjectionMappingStrategy mappingStrategy = projectionDataStrategy.getToProjectionMappingStrategy();
                int mappedIndex;
                if (openProperty) {
                    Property prop = mappingStrategy.getOpenProperty(projectionDataStrategy.getHelperContext(), getProperty());
                    mappedIndex = projectionDataStrategy.indexOfProperty(prop, false);
                } else {
                    int[] mappings = mappingStrategy.getPropertyMap(_strategy.getDataObject().getType(), gdo.getType());
                    mappedIndex = mappings[index];
                }
                projections.add(((WrapsDataStrategy)gdo.getFacade())._getDataStrategy().getPropValue(mappedIndex, null, false));
            }
        }
        return projections;
    }
    public DelegatingPropSingleValue(DelegatingDataStrategy strategy, PropValue<Object> pMainPropValue) {
        _main = pMainPropValue;
        _strategy = strategy;
    }

    public void checkSaveOldValue() {
        _main.checkSaveOldValue();
        //TODO other projections too?
    }

    public <C> C getConvertedValue(Class<C> pClass) {
        return _main.getConvertedValue(pClass);
    }

    public <C> C getConvertedValue(int pIndex, Class<C> pClass) {
        return _main.getConvertedValue(pIndex, pClass);
    }

    public GenericDataObject getDataObject() {
        return _main.getDataObject();
    }

    public int getIndexByPropertyValue(String propName, Object value) {
        return _main.getIndexByPropertyValue(propName, value);
    }

    public Object getOldValue() {
        return _main.getOldValue();
    }

    public SdoProperty getProperty() {
        return _main.getProperty();
    }

    public Object getValue() {
        return _main.getValue();
    }

    public void internAttachOpposite(DataObjectDecorator pDataObject) {
        _main.internAttachOpposite(pDataObject);

    }

    public void internDetachOpposite(DataObjectDecorator pDataObject) {
        _main.internDetachOpposite(pDataObject);

    }

    public boolean isMany() {
        return _main.isMany();
    }

    public boolean isModified() {
        return _main.isModified();
    }

    public boolean isReadOnly() {
        return _main.isReadOnly();
    }

    public boolean isSet() {
        return _main.isSet();
    }

    public boolean remove(Object pValue) {
        boolean ret = _main.remove(pValue);
        for (PropValue p: getProjections()) {
            p.remove(pValue);
        }
        return ret;
    }

    public Object set(int pIndex, Object pValue) {
        Object ret = _main.set(pIndex, pValue);
        for (PropValue p: getProjections()) {
            p.set(pIndex,pValue);
        }
        return ret;
    }

    public void setValue(Object pValue) {
        _main.setValue(pValue);
        for (PropValue p: getProjections()) {
            p.setValue(pValue);
        }
    }

    public void unset() {
        _main.unset();
        for (PropValue p: getProjections()) {
            p.unset();
        }

    }

    public void trimMemory() {
    }

}
