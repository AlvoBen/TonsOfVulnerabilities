package com.sap.sdo.testcase.internal.propindex;

public interface FullIndexAnno {
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 1)
    String getA();
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 0)
    String getC();
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 2)
    String getB();

}
