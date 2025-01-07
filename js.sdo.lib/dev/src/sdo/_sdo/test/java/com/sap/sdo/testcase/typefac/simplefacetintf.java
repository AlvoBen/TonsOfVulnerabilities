package com.sap.sdo.testcase.typefac;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface SimpleFacetIntf {
	@SdoPropertyMetaData(sdoType="com.sap.sdo.testcase.typefac.facets#MinMaxLength")
	String getRestrictedString();
	void setRestrictedString(String string);
}
