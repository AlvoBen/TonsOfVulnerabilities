package com.sap.sdo.testcase.internal.pojo;

import java.util.List;

public class PojoComplex implements PojoComplexIntf {

    private List<PojoSimpleIntf> _complexValues;
    private PojoSimpleIntf _complexValue;
    
    public List<PojoSimpleIntf> getComplexValues() {
        return _complexValues;
    }

    public void setComplexValues(List<PojoSimpleIntf> pValues) {
        _complexValues = pValues;
    }

    public PojoSimpleIntf getComplexValue() {
        return _complexValue;
    }

    public void setComplexValue(PojoSimpleIntf pValue) {
        _complexValue = pValue;
    }

}
