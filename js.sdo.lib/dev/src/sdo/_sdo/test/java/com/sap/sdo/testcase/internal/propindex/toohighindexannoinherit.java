package com.sap.sdo.testcase.internal.propindex;

public interface TooHighIndexAnnoInherit extends FullIndexAnno {
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 6)
    String getE();
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 4)
    String getD();


}
