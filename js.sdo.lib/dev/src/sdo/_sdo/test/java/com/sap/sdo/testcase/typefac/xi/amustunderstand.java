package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoFacets(
    pattern = {"0|1"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "@mustUnderstand",
    uri = "http://schemas.xmlsoap.org/soap/envelope/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            containment = false,
            name = "mustUnderstand"
        )}
)
public interface AMustUnderstand extends com.sap.sdo.api.types.sdo.Boolean {}

