package com.sap.sdo.testcase.internal;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface ISimpleTypeProperty {
	@SdoPropertyMetaData(sdoType="com.sap.sdo.testcase#String40")
	String getX();
	void setX(String x);
}
