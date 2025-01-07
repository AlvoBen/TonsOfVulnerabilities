package com.sap.sdo.testcase.typefac.facets;

import com.sap.sdo.api.SdoFacets;

import commonj.sdo.types.Integer;

@SdoFacets(minExclusive=3, maxExclusive=5)
public interface MinMaxExclusive extends Integer {
}
