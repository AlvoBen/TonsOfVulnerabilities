package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoFacets(
    pattern = {"[0-9A-F_\\-/]{0,16}"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30"
)
public interface QueueIdType extends com.sap.sdo.api.types.sdo.String {}

