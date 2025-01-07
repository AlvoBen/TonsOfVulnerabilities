package com.sap.sdo.impl.objects.strategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import commonj.sdo.Property;
import commonj.sdo.Type;

public class OpenNonSequencedDataStrategy extends NonSequencedDataStrategy  implements Serializable{

    private static final long serialVersionUID = -9008182964473101597L;

    protected ArrayList<Property> _openProperties;

    public OpenNonSequencedDataStrategy(Type pType) {
        super(pType);
    }

    @Override
    public void setOpenProperties(ArrayList<Property> pOpenProperties) {
        _openProperties = pOpenProperties;
    }

    @Override
    public List<Property> getOpenProperties() {
        return _openProperties;
    }

    @Override
    public void trimMemory() {
        super.trimMemory();
        if (_openProperties != null) {
            _openProperties.trimToSize();
        }        
    }

}
