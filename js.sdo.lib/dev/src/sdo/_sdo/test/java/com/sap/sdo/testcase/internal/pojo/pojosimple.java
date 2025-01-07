package com.sap.sdo.testcase.internal.pojo;

import java.util.List;

public class PojoSimple implements PojoSimpleIntf {

    private List<String> _simpleValues;
    private int _simpleValue;
    
    public List<String> getSimpleValues() {
        return _simpleValues;
    }

    public void setSimpleValues(List<String> pValues) {
        _simpleValues = pValues;
    }

    public int getSimpleInt() {
        return _simpleValue;
    }

    public void setSimpleInt(int pValue) {
        _simpleValue = pValue;
    }

}
