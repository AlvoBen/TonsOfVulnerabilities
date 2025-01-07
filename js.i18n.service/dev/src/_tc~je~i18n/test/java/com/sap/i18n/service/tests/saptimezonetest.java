package com.sap.i18n.service.tests;

import com.sap.i18n.saptimezone.SAPTimeZoneCMBridge;
import com.sap.i18n.saptimezone.SAPTimeZoneR3Bridge;
import com.sap.i18n.saptimezone.SAPTimeZoneTImpl;
import com.sap.i18n.verify.intf.VerifyIntf;

public class SAPTimeZoneTest {
private VerifyIntf m_oVerify;
private SAPTimeZoneTImpl saptimezonetimp;
	
	public SAPTimeZoneTest(VerifyIntf oVerify) {
		m_oVerify = oVerify;
		saptimezonetimp = new SAPTimeZoneTImpl(m_oVerify);
		saptimezonetimp.synchronizeData(SAPTimeZoneR3Bridge.getInstance(), SAPTimeZoneCMBridge.getInstance());
	}
	
	/**
	 * test with backend connection
	 * @param ignore
	 */
	public void testMainWithFEConn(){
		saptimezonetimp.testMainWithFEConn();
		
	}
//	public void testMainWithCMConn(){
//		saptimezonetimp.testMainCfgMngData();
//		
//	}
//	public void testTimeZonePropertiesList(){
//		SAPTimeZoneTImpl saptimezonetimp = new SAPTimeZoneTImpl(m_oVerify);
//		saptimezonetimp.testTimeZoneProperiesList();
//	}
}
