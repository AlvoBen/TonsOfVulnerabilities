package com.sap.sdo.testcase.typefac;

import commonj.sdo.DataObject;

public interface ReferenceTestBase {
    
    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0, 
        containment = false)
    DataObject getDataObjectProp();
    void setDataObjectProp(DataObject dataObject);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        containment = false)
    ReferenceTestBase getBaseProp();
    void setBaseProp(ReferenceTestBase dataObject);
    
    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2)
    String getValue1();
    void setValue1(String value);
}
