package com.sap.i18n.service;

import com.sap.conn.jco.JCoException;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.i18n.cache.BackendAvailability;
import com.sap.i18n.cache.BackendDO;
import com.sap.i18n.verify.intf.VerifyIntf;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

public class I18NBridgeTImpl {

	protected static final Category m_oLog = Category.SYS_SERVER;
	protected static final Location m_oLoc = Location.getLocation("com.sap.i18n.language.SAPLanguageTImpl");

	/** interface to verification framework */
	VerifyIntf m_oVerify;

	public I18NBridgeTImpl(VerifyIntf oVerify) {
		m_oVerify = oVerify;
	}

	protected void verify(boolean bCond, String sErrText, String sTestName) {
		m_oVerify.verifyTest(bCond, sErrText, sTestName);
	}

	protected void printLogln(String msg) {
		m_oVerify.printLogln(msg);
	}

	public void synchronizeData(I18NR3BridgeIntf r3Bridge, I18NConfigMngBridge cmBridge) {
		printLogln("Make Connection and sync backend-data");
		try {
			BackendDO data = r3Bridge.executeGetData();
			// initial the cache class with the data
			if (data != null) {
				cmBridge.getBackendDataCache().init(data, BackendAvailability.STATE_OK);
				cmBridge.createConfiguration();
				cmBridge.createSubConfiguration();
				cmBridge.storeData(data);

			}
		} catch (JCoException jcoexception) {
			verify(false,
					"initialization failed due to Jco Exception. Exception Message: "
							+ jcoexception.getMessage(), "synchronizeData()");
			printLogln("test failed due to JcoException. Exception Message: "
					+ jcoexception.getMessage());
		} catch (ConfigurationException cfgexcep) {
			verify(
					false,
					"initialization failed due to Configuration Manager Exception. Exception Message: "
							+ cfgexcep.getMessage(), "synchronizeData()");
			printLogln("Test aborded due to exception. "
					+ cfgexcep.getMessage());
		}
	}
	
}
