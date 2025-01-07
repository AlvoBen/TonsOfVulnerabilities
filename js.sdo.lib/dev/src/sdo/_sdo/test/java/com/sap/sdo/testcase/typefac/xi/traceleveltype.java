package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"Off", "Fatal", "Error", "Warning", "Information", "Debug"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30"
)
public interface TraceLevelType extends com.sap.sdo.api.types.sdo.String {}

