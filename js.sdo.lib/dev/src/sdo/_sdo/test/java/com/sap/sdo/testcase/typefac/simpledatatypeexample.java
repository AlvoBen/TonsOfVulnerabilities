package com.sap.sdo.testcase.typefac;

public interface SimpleDataTypeExample  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0,
        sdoType = "com.sap.sdo.testcase.typefac#SimpleDataTypeExampleDataType"
    )
    com.sap.sdo.testcase.internal.XsdVisitorTest.HexaStringSimpleType getDataType();
    void setDataType(com.sap.sdo.testcase.internal.XsdVisitorTest.HexaStringSimpleType pDataType);

}
