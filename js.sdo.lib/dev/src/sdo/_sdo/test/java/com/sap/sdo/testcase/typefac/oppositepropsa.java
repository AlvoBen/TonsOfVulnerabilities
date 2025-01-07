package com.sap.sdo.testcase.typefac;

import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface OppositePropsA {
	@SdoPropertyMetaData(opposite="a", containment=false)
	List<OppositePropsB> getBs();
    void setBs(List<OppositePropsB> pBs);
}
