package com.sap.sdo.testcase.internal.propindex;

public interface FullIndexAnno2 {
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 1)
    String getD();
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 0)
    String getE();

}
