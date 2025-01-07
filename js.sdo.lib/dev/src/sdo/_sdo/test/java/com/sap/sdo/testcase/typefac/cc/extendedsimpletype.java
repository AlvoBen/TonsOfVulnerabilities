package com.sap.sdo.testcase.typefac.cc;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "extendedSimpleType.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "extended"
        )},
    uri = "ext.xsd"
)
public interface ExtendedSimpleType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0
    )
    java.math.BigInteger getValue();
    void setValue(java.math.BigInteger pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1
    )
    String getMeta1();
    void setMeta1(String pMeta1);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "0",
        propertyIndex = 2
    )
    int getMeta2();
    void setMeta2(int pMeta2);

}
