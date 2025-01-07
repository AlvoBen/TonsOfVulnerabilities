package com.sap.i18n.countryformat;

import com.sap.i18n.service.I18NBridgeTImpl;
import com.sap.i18n.verify.intf.VerifyIntf;

public class CountryFormatTImpl extends I18NBridgeTImpl {

	public CountryFormatTImpl(VerifyIntf oVerify) {
		super(oVerify);
	}

	public void testMain() {
		synchronizeData(CountryFormatR3Bridge.getInstance(), CountryFormatCMBridge.getInstance());
	}

}
