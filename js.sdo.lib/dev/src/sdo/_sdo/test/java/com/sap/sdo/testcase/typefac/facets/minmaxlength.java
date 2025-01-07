package com.sap.sdo.testcase.typefac.facets;

import com.sap.sdo.api.SdoFacets;

@SdoFacets(minLength=3, maxLength=5)
public interface MinMaxLength extends commonj.sdo.types.String {
}
