package com.sap.sdo.testcase.typefac;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface IPolymorphicProperty {
	@SdoPropertyMetaData(containment=true)
	SimpleAttrIntf getA();
	void setA(SimpleAttrIntf a);
}
