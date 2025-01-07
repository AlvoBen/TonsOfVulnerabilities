package com.sap.sdo.testcase.typefac.facets;

import com.sap.sdo.api.SdoFacets;

import commonj.sdo.types.String;

@SdoFacets(enumeration={"a","b","c"})
public interface EnumFacet extends String {
}
