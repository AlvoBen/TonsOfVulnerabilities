package com.sap.sdo.impl.objects.projections;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.strategy.enhancer.WrapsDataStrategy;
import com.sap.sdo.impl.types.SdoProperty;

import commonj.sdo.Property;

public class DelegatingPropMultiValue extends AbstractList implements PropValue<List>, RandomAccess, Serializable  {

    private static final long serialVersionUID = -81046262052841977L;
    private final DelegatingDataStrategy _strategy;
    private final PropValue<List> _main;
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
    public DelegatingPropMultiValue(DelegatingDataStrategy strategy, int property) {
        _main = (PropValue<List>)strategy.getMain().getPropValue(property, null, true);
        _strategy = strategy;
    }

    public void checkSaveOldValue() {
        _main.checkSaveOldValue();
        //TODO other projections too?
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

    public <C> C  getConvertedValue(int pIndex, Class<C> pClass) {
        return _main.getConvertedValue(pIndex, pClass);
    }

    public GenericDataObject getDataObject() {
        return _main.getDataObject();
    }

    public int getIndexByPropertyValue(String propName, Object value) {
        return _main.getIndexByPropertyValue(propName, value);
    }

    public List getOldValue() {
        return _main.getOldValue();
    }

    public SdoProperty getProperty() {
        return _main.getProperty();
    }

    public List getValue() {
        return this;
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

    @Override
    public Object get(int pIndex) {
        return _main.getValue().get(pIndex);
    }
    
    @Override
    public int size() {
        return _main.getValue().size();
    }

    @Override
    public void add(int pIndex, Object pValue) {
        _main.getValue().add(pIndex, pValue);
        for (PropValue<List> p: getProjections()) {
            p.getValue().add(pIndex,pValue);
        }
    }
    
    public boolean remove(Object pValue) {
        boolean ret = _main.remove(pValue);
        for (PropValue p: getProjections()) {
            p.remove(pValue);
        }
        return ret;
    }

    public Object remove(int pIndex) {
        Object ret = _main.getValue().remove(pIndex);
        for (PropValue<List> p: getProjections()) {
            p.getValue().remove(pIndex);
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
