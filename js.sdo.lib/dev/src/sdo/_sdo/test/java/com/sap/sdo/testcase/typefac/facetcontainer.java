package com.sap.sdo.testcase.typefac;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface FacetContainer {
	@SdoPropertyMetaData(sdoType="com.sap.sdo.testcase.typefac.facets#MinMaxLength")
	String getMinMaxLength();
	void setMinMaxLength(String string);
	
	@SdoPropertyMetaData(sdoType="com.sap.sdo.testcase.typefac.facets#ExactLength")
	String getExactLength();
	void setExactLength(String string);
	
	@SdoPropertyMetaData(sdoType="com.sap.sdo.testcase.typefac.facets#EnumFacet")
	String getEnum();
	void setEnum(String string);

	@SdoPropertyMetaData(sdoType="com.sap.sdo.testcase.typefac.facets#MinMaxExclusive")
	int getMinMaxEx();
	void setMinMaxEx(int x);
}
