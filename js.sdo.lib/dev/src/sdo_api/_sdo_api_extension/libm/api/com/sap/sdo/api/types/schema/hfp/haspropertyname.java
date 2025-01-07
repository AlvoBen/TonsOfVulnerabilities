package com.sap.sdo.api.types.schema.hfp;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"ordered", "bounded", "cardinality", "numeric"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "hasPropertyName",
    uri = "http://www.w3.org/2001/XMLSchema-hasFacetAndProperty"
)
public interface HasPropertyName extends com.sap.sdo.api.types.sdo.String {}

