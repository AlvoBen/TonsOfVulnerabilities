package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "fractionDigits"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "length"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "maxLength"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "minLength"
        )},
    sdoName = "numFacet",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface NumFacet extends com.sap.sdo.api.types.schema.Facet {

}
