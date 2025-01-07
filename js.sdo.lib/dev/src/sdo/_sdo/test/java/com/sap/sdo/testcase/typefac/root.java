package com.sap.sdo.testcase.typefac;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "/com/sap/sdo/testcase/schemas/ComplexSimplePolymorphismAnnotated.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    noNamespace = true,
    sdoName = "root",
    elementFormDefault = true
)
public interface Root  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 0
    )
    com.sap.sdo.testcase.typefac.IntExtension getSimpleInt();
    void setSimpleInt(com.sap.sdo.testcase.typefac.IntExtension pSimpleInt);

}
