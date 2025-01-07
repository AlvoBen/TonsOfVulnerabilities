package com.sap.sdo.api.types.schema.hfp;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"length", "minLength", "maxLength", "pattern", "enumeration", "maxInclusive", "maxExclusive", "minInclusive", "minExclusive", "totalDigits", "fractionDigits", "whiteSpace", "maxScale", "minScale"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "hasFacetName",
    uri = "http://www.w3.org/2001/XMLSchema-hasFacetAndProperty"
)
public interface HasFacetName extends com.sap.sdo.api.types.sdo.String {}

