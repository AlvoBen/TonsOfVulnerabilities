package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"simple", "extended", "locator", "arc", "resource", "title", "none"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "@type",
    uri = "http://www.w3.org/1999/xlink",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            containment = false,
            name = "type"
        )}
)
public interface AType extends com.sap.sdo.api.types.sdo.String {}

