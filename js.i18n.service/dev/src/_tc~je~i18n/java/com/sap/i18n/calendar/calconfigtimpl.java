package com.sap.i18n.calendar;

import com.sap.i18n.service.I18NBridgeTImpl;
import com.sap.i18n.verify.intf.VerifyIntf;

public class CalConfigTImpl extends I18NBridgeTImpl {
	
	public CalConfigTImpl(VerifyIntf oVerify) {
		super(oVerify);
	}

	public void testMain() {
		synchronizeData(CalConfigR3Bridge.getInstance(), CalConfigCMBridge.getInstance());
	}
}
