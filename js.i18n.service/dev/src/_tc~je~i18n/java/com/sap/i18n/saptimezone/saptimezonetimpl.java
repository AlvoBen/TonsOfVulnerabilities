package com.sap.i18n.saptimezone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.i18n.cache.BackendAvailability;
import com.sap.i18n.cache.BackendDO;
import com.sap.i18n.cache.BackendDataCacheFactory;
import com.sap.i18n.cache.DateTimeParserUTC;
import com.sap.i18n.service.I18NBridgeTImpl;
import com.sap.i18n.timezone.SAPTimeZone;
import com.sap.i18n.verify.intf.VerifyIntf;

public class SAPTimeZoneTImpl extends I18NBridgeTImpl {
	
	public SAPTimeZoneTImpl(VerifyIntf oVerify){
		super(oVerify); 
	}
	
	/*
	 * make connection to backend
	 * sync time zone data initial time zone library
	 * do check
	 */
	public void testMainWithFEConn(){
		check();
		
		checkDisplayedName();
		checkSerializationOfSAPTimeZoneInstances();
	}
	/*
	 * make connection to the configuration manager
	 * read data from i18n/saptimezone configuration
	 * do check
	 */
	public void testMainCfgMngData(){
	
		printLogln("Test with Configuration data");
		
		SAPTimeZoneCMBridge bridge2CfgMng = SAPTimeZoneCMBridge.getInstance();			
		try{
			BackendDO timezoneDO = bridge2CfgMng.readData();
			if(timezoneDO != null){
				BackendDataCacheFactory.getCacheTimeZone().init(timezoneDO, BackendAvailability.STATE_OK);
				check();
				checkDisplayedName();
				checkSerializationOfSAPTimeZoneInstances();
			}else{
				verify(false,"TimeZone data could not be read from the configuration manager. Method name: testMainCfgMngData.","testMainCfgMngData()");

			}
		}catch(ConfigurationException cfgexcep){
			m_oLog.errorT(m_oLoc, "Test aborded due to exception throw. "+ cfgexcep.getMessage());
			printLogln("Test aborded due to exception. "+ cfgexcep.getMessage());
		}
//		}catch(JCoException jcoexception){
//			m_oLog.errorT(m_oLoc, "Test  with Configuration data aborded due to exception throw. "+ jcoexception.getMessage());
//			printLogln("Test aborded due to exception. "+ jcoexception.getMessage());
//		}
	}
	
	private void check() {
		printLogln("check start");
		verifyTZDateTimeABAP( "CET new year", "CET", "20000101", "000000", 60);
		verifyTZDateTimeABAP( "CET mid year", "CET", "20000601", "000000", 120);
		verifyTZDateTimeABAP( "CET end year", "CET", "20001231", "235959", 60);

		verifyTZDateTimeABAP( "CET before switch to DST 2006", "CET", "20060326", "000000", 60);
		verifyTZDateTimeABAP( "CET at switch to DST 2006", "CET", "20060326", "005959", 60);
		verifyTZDateTimeABAP( "CET at switch to DST 2006", "CET", "20060326", "010000", 120);
		verifyTZDateTimeABAP( "CET at switch to DST 2006", "CET", "20060326", "010001", 120);
		verifyTZDateTimeABAP( "CET after switch to DST 2006", "CET", "20060326", "020000", 120);

		verifyTZDateTimeJava( "Java CET at switch to DST 2006", "CET", "20060326", "005959", 60);
		verifyTZDateTimeJava( "Java CET at switch to DST 2006", "CET", "20060326", "010000", 120);
		verifyTZDateTimeJava( "Java CET at switch to DST 2006", "CET", "20060326", "010001", 120);
		
		verifyTZDateTimeABAP( "CET before switch from DST 2006", "CET", "20061029", "000000", 120);
		verifyTZDateTimeABAP( "CET at switch from DST 2006", "CET", "20061029", "005959", 120);
		verifyTZDateTimeABAP( "CET at switch from DST 2006", "CET", "20061029", "010000", 60);
		verifyTZDateTimeABAP( "CET at switch from DST 2006", "CET", "20061029", "010001", 60);
		verifyTZDateTimeABAP( "CET after switch from DST 2006", "CET", "20061029", "020000", 60);

		verifyTZDateTimeJava( "Java CET at switch from DST 2006", "CET", "20061029", "005959", 120);
		verifyTZDateTimeJava( "Java CET at switch from DST 2006", "CET", "20061029", "010000", 60);
		verifyTZDateTimeJava( "Java CET at switch from DST 2006", "CET", "20061029", "010001", 60);
		verifyTZDateTimeJava( "Java CET after switch from DST 2006", "CET", "20061029", "020000", 60);
		verifyTZDateTimeJava( "Java CET after switch from DST 2006", "CET", "20061029", "020001", 60);

		verifyTZDateTimeABAP( "CET at switch to DST 1990", "CET", "19900325", "005959", 60);
		verifyTZDateTimeABAP( "CET at switch to DST 1990", "CET", "19900325", "010000", 120);
		verifyTZDateTimeABAP( "CET at switch to DST 1990", "CET", "19900325", "010001", 120);
		verifyTZDateTimeABAP( "CET after switch to DST 1990", "CET", "19900325", "020000", 120);

		verifyTZDateTimeABAP( "CET new year before DST", "CET", "19700101", "000000", 60);
		verifyTZDateTimeABAP( "CET mid year before DST", "CET", "19700601", "000000", 60);
		verifyTZDateTimeABAP( "CET end year before DST", "CET", "19701231", "235959", 60);

		verifyTZDateTimeABAP( "CST new year", "CST", "20000101", "000000", -360);
		verifyTZDateTimeABAP( "CST mid year", "CST", "20000601", "000000", -300);

		verifyTZDateTimeABAP( "CST before switch to DST 2006", "CST", "20060402", "070000", -360);
		verifyTZDateTimeABAP( "CST at switch to DST 2006", "CST", "20060402", "075959", -360);
		verifyTZDateTimeABAP( "CST at switch to DST 2006", "CST", "20060402", "080000", -300);
		verifyTZDateTimeABAP( "CST at switch to DST 2006", "CST", "20060402", "800001", -300);
		verifyTZDateTimeABAP( "CST after switch to DST 2006", "CST", "20060402", "090000", -300);
		
		verifyTZDateTimeJava( "Java CST at switch to DST 2006", "CST", "20060402", "075959", -360);
		verifyTZDateTimeJava( "Java CST at switch to DST 2006", "CST", "20060402", "080000", -300);
		verifyTZDateTimeJava( "Java CST at switch to DST 2006", "CST", "20060402", "800001", -300);

		verifyTZDateTimeABAP( "CST before switch from DST 2006", "CST", "20061029", "060000", -300);
		verifyTZDateTimeABAP( "CST at switch from DST 2006", "CST", "20061029", "065959", -300);
		verifyTZDateTimeABAP( "CST at switch from DST 2006", "CST", "20061029", "070000", -360);
		verifyTZDateTimeABAP( "CST at switch from DST 2006", "CST", "20061029", "070001", -360);
		verifyTZDateTimeABAP( "CST after switch from DST 2006", "CST", "20061029", "080000", -360);

		verifyTZDateTimeJava( "Java CST before switch from DST 2006", "CST", "20061029", "060000", -300);
		verifyTZDateTimeJava( "Java CST at switch from DST 2006", "CST", "20061029", "065959", -300);
		verifyTZDateTimeJava( "Java CST at switch from DST 2006", "CST", "20061029", "070000", -360);
		verifyTZDateTimeJava( "Java CST at switch from DST 2006", "CST", "20061029", "070001", -360);
		verifyTZDateTimeJava( "Java CST after switch from DST 2006", "CST", "20061029", "080000", -360);
		
		verifyTZDateTimeABAP( "BRAZIL new year", "BRAZIL", "20000101", "000000", -120);
		verifyTZDateTimeABAP( "BRAZIL mid year", "BRAZIL", "20000601", "000000", -180);
		verifyTZDateTimeABAP( "BRAZIL end year", "BRAZIL", "20001231", "235959", -120);

		verifyTZDateTimeJava( "Java BRAZIL new year", "Brazil/East", "20000101", "000000", -120);
		verifyTZDateTimeJava( "Java BRAZIL mid year", "Brazil/East", "20000601", "000000", -180);
		verifyTZDateTimeJava( "Java BRAZIL end year", "Brazil/East", "20001231", "235959", -120);

		if (SAPTimeZone.isABAPInfoAvaliable() ) {
			verifyTZDateTimeABAP( "BRAZIL before switch from DST 2006", "BRAZIL", "20060312", "030000", -120);
			verifyTZDateTimeABAP( "BRAZIL at switch from DST 2006", "BRAZIL", "20060312", "035959", -120);
			verifyTZDateTimeABAP( "BRAZIL at switch from DST 2006", "BRAZIL", "20060312", "040000", -180);
			verifyTZDateTimeABAP( "BRAZIL at switch from DST 2006", "BRAZIL", "20060312", "040001", -180);
			verifyTZDateTimeABAP( "BRAZIL after switch from DST 2006", "BRAZIL", "20060312", "050000", -180);
		}

		verifyTZDateTimeJava( "Java BRAZIL at switch from DST 2006", "Brazil/East", "20060211", "035959", -120);
		verifyTZDateTimeJava( "Java BRAZIL at switch from DST 2006", "Brazil/East", "20060312", "040000", -180);
		verifyTZDateTimeJava( "Java BRAZIL at switch from DST 2006", "Brazil/East", "20060312", "040001", -180);

		verifyTZDateTimeABAP( "BRAZIL new year before DST", "BRAZIL", "19700101", "000000", -180);
		verifyTZDateTimeABAP( "BRAZIL mid year before DST", "BRAZIL", "19700601", "000000", -180);
		verifyTZDateTimeABAP( "BRAZIL end year before DST", "BRAZIL", "19701231", "235959", -180);

		verifyTZDateTimeABAP( "CST before switch to DST 2007", "CST", "20070311", "070000", -360);
		verifyTZDateTimeABAP( "CST at switch to DST 2007", "CST", "20070311", "075959", -360);
		verifyTZDateTimeABAP( "CST at switch to DST 2007", "CST", "20070311", "080000", -300);
		verifyTZDateTimeABAP( "CST at switch to DST 2007", "CST", "20070311", "800001", -300);
		verifyTZDateTimeABAP( "CST after switch to DST 2007", "CST", "20070311", "090000", -300);
		
		verifyTZDateTimeJava( "Java CST at switch to DST 2007", "CST", "20070311", "075959", -360);
		verifyTZDateTimeJava( "Java CST at switch to DST 2007", "CST", "20070311", "080000", -300);
		verifyTZDateTimeJava( "Java CST at switch to DST 2007", "CST", "20070311", "800001", -300);

		verifyTZDateTimeABAP( "CST before switch from DST 2007", "CST", "20071104", "060000", -300);
		verifyTZDateTimeABAP( "CST at switch from DST 2007", "CST", "20071104", "065959", -300);
		verifyTZDateTimeABAP( "CST at switch from DST 2007", "CST", "20071104", "070000", -360);
		verifyTZDateTimeABAP( "CST at switch from DST 2007", "CST", "20071104", "070001", -360);
		verifyTZDateTimeABAP( "CST after switch from DST 2007", "CST", "20071104", "080000", -360);

		verifyTZDateTimeJava( "Java CST at switch from DST 2007", "CST", "20071104", "065959", -300);
		verifyTZDateTimeJava( "Java CST at switch from DST 2007", "CST", "20071104", "070000", -360);
		verifyTZDateTimeJava( "Java CST at switch from DST 2007", "CST", "20071104", "070001", -360);

		verifyTZDateTimeABAP( "IRAN new year 1999", "IRAN", "19990101", "000000", 210);
		verifyTZDateTimeABAP( "IRAN mid year 1999", "IRAN", "19990601", "000000", 270);
		verifyTZDateTimeABAP( "IRAN end year 1999", "IRAN", "19991231", "235959", 210);

		if (SAPTimeZone.isABAPInfoAvaliable() ) {
			verifyTZDateTimeABAP( "IRAN before switch to DST 1999", "IRAN", "19990320", "220000", 210);
			verifyTZDateTimeABAP( "IRAN at switch to DST 1999", "IRAN", "19990320", "222959", 210);
			verifyTZDateTimeABAP( "IRAN at switch to DST 1999", "IRAN", "19990320", "223000", 270);
			verifyTZDateTimeABAP( "IRAN at switch to DST 1999", "IRAN", "19990320", "223001", 270);
			verifyTZDateTimeABAP( "IRAN after switch to DST 1999", "IRAN", "19990320", "230000", 270);
	
			verifyTZDateTimeABAP( "IRAN before switch from DST 1999", "IRAN", "19990922", "220000", 270);
			verifyTZDateTimeABAP( "IRAN at switch from DST 1999", "IRAN", "19990922", "222959", 270);
			verifyTZDateTimeABAP( "IRAN at switch from DST 1999", "IRAN", "19990922", "223000", 210);
			verifyTZDateTimeABAP( "IRAN at switch from DST 1999", "IRAN", "19990922", "223001", 210);
			verifyTZDateTimeABAP( "IRAN after switch to DST 1999", "IRAN", "19990922", "230000", 210);

			verifyTZDateTimeABAP( "IRAN new year 2000", "IRAN", "20000101", "000000", 210);
			verifyTZDateTimeABAP( "IRAN mid year 2000", "IRAN", "20000601", "000000", 210);
			verifyTZDateTimeABAP( "IRAN end year 2000", "IRAN", "20001231", "235959", 210);
		}
		
		verifyTZDateTimeABAP( "NODSTRULE new year", "NODSTRULE", "20000101", "000000", 0);
		verifyTZDateTimeABAP( "NODSTRULE mid year", "NODSTRULE", "20000601", "000000", 0);
		verifyTZDateTimeABAP( "NODSTRULE end year", "NODSTRULE", "20001231", "235959", 0);

		verifyTZDateTimeABAP( "NOTZRULE new year", "NOTZRULE", "20000101", "000000", 0);
		verifyTZDateTimeABAP( "NOTZRULE mid year", "NOTZRULE", "20000601", "000000", 0);
		verifyTZDateTimeABAP( "NOTZRULE end year", "NOTZRULE", "20001231", "235959", 0);

		verifyTZDateTimeABAP( "ILLTZRULE new year", "ILLTZRULE", "20000101", "000000", 0);
		verifyTZDateTimeABAP( "ILLTZRULE mid year", "ILLTZRULE", "20000601", "000000", 0);
		verifyTZDateTimeABAP( "ILLTZRULE end year", "ILLTZRULE", "20001231", "235959", 0);
		
		//Check Rules equality
		verifySAPTZvsJDKTZRules();
		
		
		printLogln("check finished");
	}
	/*this check include additional data that doesn't exist in the dummy */
	public void extendedCheck(){
		verifyTZDateTimeABAP( "PST before switch to DST", "PST", "20000101", "000000", -480);
		verifyTZDateTimeABAP( "PST after switch to DST", "PST", "20000601", "000000", -420);
	}
	public void verifyTZDateTimeABAP( String sTest, String sTimeZone, String sUTCDate, String sUTCTime, int nExpectedOffset ) {
		TimeZone oTimeZone = SAPTimeZone.getTimeZoneFromAbapId( sTimeZone );
		checkTZDateTime( sTest, oTimeZone, sUTCDate, sUTCTime, nExpectedOffset );
	}
	private void checkDisplayedName(){
		Locale DE = new Locale("DE");
		Locale EN = new Locale("EN");
		verifyDisplayedName("BRAZIL", "Brasilien" , "BRAZIL", DE);
		verifyDisplayedName("BRAZIL", "Brazil" , "BRAZIL", EN);
		
		verifyDisplayedName("CET", "Mitteleuropa" , "CET", DE);
		verifyDisplayedName("CET", "Central Europe" , "CET", EN);
		
		verifyDisplayedName("UK", "England, Irland, Schottland" , "UK", DE);
//		verifyDisplayedName("UK", "England, Ireland, Scotland" , "UK", EN);
		
		verifyDisplayedName("CST", "Central Time (Dallas)" , "CST", DE);
		verifyDisplayedName("CST", "Central Time (Dallas)" , "CST", EN);
		
	}
	private void verifyDisplayedName(String sapTzId, String expectedLongName, String expectedShortName, Locale forLocale){
		TimeZone sapTZ = SAPTimeZone.getTimeZoneFromAbapId(sapTzId);
		String displayedNameLong = sapTZ.getDisplayName(forLocale);
		String displayedNameShort =  sapTZ.getDisplayName(false, 0, forLocale);
		verify( 
				expectedShortName.equals(displayedNameShort),
				"Timezone: " + sapTZ.getID() + "\n"+ 
				" Diplayed Short name: " + displayedNameShort +"\n"+
				" Expected short name: " + expectedShortName +"\n",
				"Diplayed short name"
		);
		verify( 
				expectedLongName.equals(displayedNameLong),
				"Timezone: " + sapTZ.getID() + "\n"+ 
				" Diplayed Short name: " + displayedNameLong +"\n"+
				" Expected short name: " + expectedLongName +"\n",
				"Diplayed Long name"
		);
		
	}
	public void verifyTZDateTimeJava( String sTest, String sTimeZone, String sUTCDate, String sUTCTime, int nExpectedOffset ) {
		TimeZone oTimeZone = TimeZone.getTimeZone( sTimeZone );
		checkTZDateTime( sTest, oTimeZone, sUTCDate, sUTCTime, nExpectedOffset );
	}

	public void checkTZDateTime( String sTest, TimeZone oTimeZone, String sUTCDate, String sUTCTime, int nExpectedOffset ) {
		Date oDate = DateTimeParserUTC.parseDateTime( sUTCDate+sUTCTime );
		
		long nDate = oDate.getTime();
		int nOffset = oTimeZone.getOffset( nDate ) / (60*1000);
		verify( 
				nOffset==nExpectedOffset,
				"Timezone: " + oTimeZone.getDisplayName() + " at UTC "+ sUTCDate + sUTCTime + 
				" Offset: " + nOffset + " Expected: " + nExpectedOffset,
				sTest
		);
	}
	
	/*
	 * Extra test of the method hasSameRules()
	 */
	public void verifySAPTZvsJDKTZRules(){
		String[] abapTZs = SAPTimeZone.getAvailableAbapIDs();
		for (int i = 0; i < abapTZs.length; i++) {
			String sJavaTZ = SAPTimeZone.getIdJavaFromAbap(abapTZs[i]);
			if (sJavaTZ != null){
				TimeZone jdkTZ = TimeZone.getTimeZone(sJavaTZ);
				TimeZone sapTZ = SAPTimeZone.getTimeZoneFromAbapId(abapTZs[i]);
				
				if (sapTZ instanceof SAPTimeZone) {
					printLogln("SAP TimeZone Rules vs. JDK TimeZone Rules.");
					verify(!sapTZ.hasSameRules(jdkTZ), "Unexpected equality: SAPTimeZone: "+sapTZ.getID()+" and JDK TimeZone"+
							jdkTZ.getID()+" have the same Rules!\n", "SAP TimeZone Rules vs. JDK TimeZone Rules");
				}
								
//				printLogln("\n JDK TimeZone Rules vs. SAP TimeZone Rules.");
//				verify(!jdkTZ.hasSameRules(sapTZ), "Unexpected equality: JDKTimeZone: "+jdkTZ.getID()+" and SAP TimeZone "+
//						sapTZ.getID()+ " have the same Rules \n", "JDK TimeZone Rules vs. SAP TimeZone Rules");
			}
		}
	}
	public void checkSerializationOfSAPTimeZoneInstances(){
		printLogln("check of Serialization");
		String[] sapIds = SAPTimeZone.getAvailableAbapIDs();
		for (int i = 0; i < sapIds.length; i++) {
			TimeZone tz = SAPTimeZone.getTimeZoneFromAbapId(sapIds[i]);
			try{
			checkIsSerializable(tz);
			checkRoundTripSerialization(tz);
			}catch(ClassNotFoundException cnotfexception){
				verify(false, "Test failed due to: "+cnotfexception.getMessage(), "Serialization check");
			}catch(IOException ioexception){
				verify(false, "Test failed due to: "+ioexception.getMessage(), "Serialization check");
			}
		}
		
	}
	/**
	 * check if serialization is possible
	 */
	public void checkIsSerializable(TimeZone tz) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(tz);
		oos.close();
		verify(out.toByteArray().length > 0,"","");
	}
	/**
	 * check if the serialized form.
	 */
	public void checkRoundTripSerialization(TimeZone tz) throws ClassNotFoundException, IOException{
		
		TimeZone originalTimeZone = tz;
		String id = originalTimeZone.getID();
		int rowoffset = originalTimeZone.getRawOffset();
		boolean useDaylightSavingTime = originalTimeZone.useDaylightTime();

		//serialize
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(originalTimeZone);
		oos.close();
		
		//deserialize
		byte[] picked = out.toByteArray();
		InputStream is = new ByteArrayInputStream(picked);
		ObjectInputStream ois = new ObjectInputStream(is);
		Object object = ois.readObject();
		TimeZone copyTimeZone =(TimeZone) object;
		ois.close();
		
		//test Result
		String copyID = copyTimeZone.getID();
		verify(id.equals(copyID), "Original ID: "+id+"\n CopyID: "+copyID+"\n Expected ID: "+id, "Time Zone ID");
		
		int coryRowOffset = copyTimeZone.getRawOffset();
		verify(id.equals(copyID), "Original RowOffset: "+rowoffset+"\n copy RowOffset: "+coryRowOffset+"\n Expected RowOffset: "+rowoffset, "RowOffset");
		
		boolean copyUsedDST = copyTimeZone.useDaylightTime();
		verify(copyUsedDST == useDaylightSavingTime, "Original uses DST: "+useDaylightSavingTime+
				"\n copy Time Zone uses DST: "+copyUsedDST+
				"\n Expected: "+useDaylightSavingTime, "Using of Daylight Saving Time");
		
		verify(originalTimeZone.hasSameRules(copyTimeZone),
				"the original SAP TimeZone: "+id+" and the copy" +copyID+" don't habe the same rule. " , 
				"has same Rules");
	}

}

