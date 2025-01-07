package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"OK", "Error", "AckRequestNotSupported"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30"
)
public interface AckStatusType extends com.sap.sdo.api.types.sdo.String {}

