package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "whiteSpace"
        )},
    sdoName = "whiteSpace",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface WhiteSpace extends com.sap.sdo.api.types.schema.Facet {

}
