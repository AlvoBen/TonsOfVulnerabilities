package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"new", "replace", "embed", "other", "none"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "@show",
    uri = "http://www.w3.org/1999/xlink",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            containment = false,
            name = "show"
        )}
)
public interface AShow extends com.sap.sdo.api.types.sdo.String {}

