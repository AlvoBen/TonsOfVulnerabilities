package com.sap.sdo.testcase.typefac;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface OppositePropsContainB {
	//@SdoPropertyMetaData(opposite="bs")
    @SdoPropertyMetaData(containment=false)
	OppositePropsContainA getA();
}
