package com.sap.sdo.testcase.anonymous;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "/com/sap/sdo/testcase/schemas/Anonymous.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "globalType"
)
public interface GlobalType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0,
        sdoType = "com.sap.sdo.testcase.anonymous#globalList"
    )
    java.util.List<String> getValue();
    void setValue(java.util.List<String> pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        sdoType = "com.sap.sdo.testcase.anonymous#@globalAttribute",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<String> getGlobalAttribute();
    void setGlobalAttribute(java.util.List<String> pGlobalAttribute);

}
