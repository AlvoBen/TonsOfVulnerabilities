package com.sap.sdo.testcase.typefac;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface OppositePropsB {
	@SdoPropertyMetaData(containment=false)
	OppositePropsA getA();
    void setA(OppositePropsA pA);
}
