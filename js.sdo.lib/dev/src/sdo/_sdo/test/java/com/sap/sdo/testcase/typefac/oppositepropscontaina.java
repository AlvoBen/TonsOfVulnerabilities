package com.sap.sdo.testcase.typefac;

import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface OppositePropsContainA {
	@SdoPropertyMetaData(opposite="a", containment=true)
	List<OppositePropsContainB> getBs();
}
