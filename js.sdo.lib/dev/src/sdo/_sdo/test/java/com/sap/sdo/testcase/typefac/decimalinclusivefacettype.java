package com.sap.sdo.testcase.typefac;

@com.sap.sdo.api.SdoFacets(
    totalDigits = 3,
    maxInclusive = 20,
    minInclusive = 2
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "com.sap.sdo.testcase3"
)
public interface DecimalInclusiveFacetType extends commonj.sdo.types.Decimal {}

