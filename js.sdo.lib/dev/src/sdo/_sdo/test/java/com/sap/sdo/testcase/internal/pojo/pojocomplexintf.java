package com.sap.sdo.testcase.internal.pojo;

import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface PojoComplexIntf {
    @SdoPropertyMetaData(
        containment = true
    )
    List<PojoSimpleIntf> getComplexValues();
    void setComplexValues(List<PojoSimpleIntf> pValues);

    @SdoPropertyMetaData(
        containment = true
    )
    PojoSimpleIntf getComplexValue();
    void setComplexValue(PojoSimpleIntf pValue);
}
