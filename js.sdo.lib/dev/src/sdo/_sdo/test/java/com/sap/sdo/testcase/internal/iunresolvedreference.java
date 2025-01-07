package com.sap.sdo.testcase.internal;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface IUnresolvedReference {
	@SdoPropertyMetaData(sdoType="com.sdo#XXX")
	String getXXX();
	void setXXX(String xxx);
}
