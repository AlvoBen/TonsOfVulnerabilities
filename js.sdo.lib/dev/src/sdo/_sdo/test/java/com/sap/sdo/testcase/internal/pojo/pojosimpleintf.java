package com.sap.sdo.testcase.internal.pojo;

import java.util.List;

public interface PojoSimpleIntf {
    
    List<String> getSimpleValues();
    void setSimpleValues(List<String> pValues);

    int getSimpleInt();
    void setSimpleInt(int pValue);
}
