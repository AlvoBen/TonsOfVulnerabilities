package com.sap.sdo.testcase.typefac;

@com.sap.sdo.api.SdoFacets(
    minExclusive = 3,
    maxExclusive = 21,
    fractionDigits = 2
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "com.sap.sdo.testcase3"
)
public interface DecimalExclusiveFacetType extends commonj.sdo.types.Decimal {}

