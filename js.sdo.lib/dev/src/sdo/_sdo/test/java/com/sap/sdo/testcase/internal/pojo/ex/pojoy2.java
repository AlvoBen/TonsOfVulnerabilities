package com.sap.sdo.testcase.internal.pojo.ex;

import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.SdoTypeMetaData;
@SdoTypeMetaData(sdoName="PojoY")
public class PojoY2 {
	public int _y;
	@SdoPropertyMetaData(sdoName="y")
	public int getFoo() {
		return _y;
	}
	public void setFoo(int y) {
		_y = y;
	}
}
