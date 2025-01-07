package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoFacets(
    pattern = {"[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "GUIDType",
    uri = "http://sap.com/xi/XI/Message/30"
)
public interface GuidType extends com.sap.sdo.api.types.sdo.String {}

