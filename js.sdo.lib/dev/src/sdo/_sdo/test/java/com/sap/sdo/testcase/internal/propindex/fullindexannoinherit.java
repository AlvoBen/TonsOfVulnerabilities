package com.sap.sdo.testcase.internal.propindex;

public interface FullIndexAnnoInherit extends FullIndexAnno {
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 3)
    String getE();
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 4)
    String getD();


}
