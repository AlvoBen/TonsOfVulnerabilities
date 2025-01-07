package com.sap.sdo.testcase.typefac;

@com.sap.sdo.api.SdoFacets(
    maxLength = 4,
    minLength = 3,
    enumeration = {"test", "tast"},
    pattern = {"t[eu]st"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "com.sap.sdo.testcase3"
)
public interface StringFacetType extends commonj.sdo.types.String {}

