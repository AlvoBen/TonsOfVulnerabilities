package com.sap.sdo.testcase.typefac;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "/com/sap/sdo/testcase/schemas/ComplexSimplePolymorphismAnnotated.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    noNamespace = true,
    elementFormDefault = true
)
public interface IntExtension  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "0",
        propertyIndex = 0
    )
    int getValue();
    void setValue(int pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getAttr();
    void setAttr(String pAttr);

}
