package com.sap.sdo.testcase.internal.propindex;

public interface FullIndexAnnoMultibleInherit extends FullIndexAnno, FullIndexAnno2 {
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 5)
    String getG();
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 6)
    String getF();

}
