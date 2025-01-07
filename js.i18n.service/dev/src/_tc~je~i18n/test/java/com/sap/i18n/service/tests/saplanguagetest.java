package com.sap.i18n.service.tests;

import com.sap.i18n.language.SAPLanguageCMBridge;
import com.sap.i18n.language.SAPLanguageR3Bridge;
import com.sap.i18n.language.SAPLanguageTImpl;
import com.sap.i18n.verify.intf.VerifyIntf;

public class SAPLanguageTest {

	private VerifyIntf m_oVerify;
	private SAPLanguageTImpl languageTImp;
	
	public SAPLanguageTest(VerifyIntf oVerify){
		this.m_oVerify = oVerify;
		languageTImp = new SAPLanguageTImpl(m_oVerify);
		languageTImp.synchronizeData(SAPLanguageR3Bridge.getInstance(), SAPLanguageCMBridge
				.getInstance());
	}
	
	
	public void testWithBackEndData(){
		languageTImp.testWithFEConnect();
	}
	
	public void testWithConfigMngData(){
		languageTImp.testConfigManagerData();
	}
}
