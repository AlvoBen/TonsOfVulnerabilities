package com.sap.i18n.service.tests;

import static org.junit.Assert.assertTrue;

import java.util.jar.JarInputStream;

import org.junit.Before;
import org.junit.Test;

import com.sap.i18n.calendar.CalConfigTImpl;
import com.sap.i18n.countryformat.CountryFormatTImpl;
import com.sap.i18n.service.I18NSyncFrame;
import com.sap.i18n.verify.intf.VerifyIntf;

public class CompleteServiceTest implements VerifyIntf {
	private CalConfigTImpl m_CalConfigTImpl;
	private CountryFormatTImpl m_CountryFormatTImpl;
	private SAPTimeZoneTest timezonetest;
	private SAPLanguageTest languagetest;

	/** public method to set up the test-environment */
	@Before
	public void prepare() throws Exception {
		printLogln("Initialization");
		// create destination needed by the test.
		// TestHelp.createDestination4Test();
		
		JarInputStream jarInputStream = new JarInputStream(
				I18NSyncFrame.class
						.getResourceAsStream("resources/i18ndata.i18n"));
		I18NSyncFrame.m_IsUnitTestStatus = true;
		
		I18NSyncFrame.initDataFromFile(jarInputStream);
		
		m_CalConfigTImpl = new CalConfigTImpl(this);
		m_CountryFormatTImpl = new CountryFormatTImpl(this);
		timezonetest = new SAPTimeZoneTest(this);
		languagetest = new SAPLanguageTest(this);
	}

	@Test
	public void calConfigTImpl() {
		m_CalConfigTImpl.testMain();
	}
	
	@Test
	public void countryFormatTImpl() {
		m_CountryFormatTImpl.testMain();
	}

	@Test
	public void languageWithConnection() {

		languagetest.testWithBackEndData();
	}

	@Test
	public void languageTestWithConfigMngData() {

		languagetest.testWithConfigMngData();
	}

	/**
	 * This test method needs a connection to front-end system (BIN, B20,BCE,
	 * BCO).<br>
	 * The connection is used to get data in order to perform the test.<br>
	 * The Test will be ignored if the connection is not available and the
	 * <i>ignore</i> is true.<br>
	 * The test will be failed if the connection is not available and <i>ingore</i>
	 * is false.<br>
	 */
	@Test
	public void timeZoneWithFEConn() {
		// boolean ignore = true;
		timezonetest.testMainWithFEConn();
	}

	/**
	 * test with data gotten from the Configuration manager
	 * 
	 */
//	@Test
//	public void timeZoneWithCMDataConn() {
//		timezonetest.testMainWithCMConn();
//	}

	public void printLogln(String msg) {
		System.out.println(msg);
	}

	public void verifyTest(boolean bCond, String sErrText, String sTestName) {
		// verify(bCond, sErrText, sTestName);
		String message = sErrText + " " + sTestName;
		assertTrue(message, bCond);

	}
}
