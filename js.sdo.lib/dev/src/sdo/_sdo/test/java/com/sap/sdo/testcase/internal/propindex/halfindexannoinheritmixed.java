package com.sap.sdo.testcase.internal.propindex;

public interface HalfIndexAnnoInheritMixed extends HalfIndexAnno {
    
    @com.sap.sdo.api.SdoPropertyMetaData(propertyIndex = 1)
    String getF();
    
    String getE();

}
