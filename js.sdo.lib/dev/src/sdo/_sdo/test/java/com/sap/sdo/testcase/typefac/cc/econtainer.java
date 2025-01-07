package com.sap.sdo.testcase.typefac.cc;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "extendedSimpleType.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "container"
        )},
    sdoName = "+container",
    uri = "ext.xsd"
)
public interface EContainer  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0
    )
    com.sap.sdo.testcase.typefac.cc.ExtendedSimpleType getExtended();
    void setExtended(com.sap.sdo.testcase.typefac.cc.ExtendedSimpleType pExtended);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1
    )
    com.sap.sdo.testcase.typefac.cc.RestrictedSimpleType getRestricted();
    void setRestricted(com.sap.sdo.testcase.typefac.cc.RestrictedSimpleType pRestricted);

}
