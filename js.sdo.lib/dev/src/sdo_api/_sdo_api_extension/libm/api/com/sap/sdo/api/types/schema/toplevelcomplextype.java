package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "complexType"
        )},
    sdoName = "topLevelComplexType",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface TopLevelComplexType extends com.sap.sdo.api.types.schema.ComplexType {

}
