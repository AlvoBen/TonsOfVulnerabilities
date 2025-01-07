package com.sap.i18n.language;

import java.util.Locale;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.i18n.cache.BackendAvailability;
import com.sap.i18n.cache.BackendDO;
import com.sap.i18n.cache.BackendDataCacheFactory;
import com.sap.i18n.cpbase.LangUtil;
import com.sap.i18n.service.I18NBridgeTImpl;
import com.sap.i18n.verify.intf.VerifyIntf;

public class SAPLanguageTImpl extends I18NBridgeTImpl {

	boolean forcedconvert;

	public SAPLanguageTImpl(VerifyIntf oVerify) {
		super(oVerify);
	}

	public void testWithFEConnect() {
		doCheck();
		testConvertSAPLangToLocale();
		testConvertLocaleToSAPLang();
	}

	public void testConfigManagerData() {
		printLogln("Make Connection and sync Language-data");
		SAPLanguageCMBridge cmBridge = SAPLanguageCMBridge.getInstance();
		try {
			BackendDO languageDO = cmBridge.readData();
			// initial the cache class with the data
			if (languageDO != null) {
				BackendDataCacheFactory.getCacheLanguage().init(languageDO,
						BackendAvailability.STATE_OK);
				doCheck();
				testConvertSAPLangToLocale();
				testConvertLocaleToSAPLang();
			}
		} catch (ConfigurationException configexception) {
			verify(false,
					"test failed due to Configuration Exception. Exception Message: "
							+ configexception.getMessage()
							+ "Method: testConfigManagerData.",
					"testConfigManagerData()");
			printLogln("test failed due to Configuration Exception. Exception Message: "
					+ configexception.getMessage()
					+ "Method: testConfigManagerData.");
		}
	}

	private void doCheck() {
		printLogln("Start of the Check");
		forcedconvert = true;
		// B20 Client 000 MySAP modus
		// verifyConvertLocaleToallowedSAPLang(new Locale( "id", "" ), "ID",
		// "Indonesia 1" );
		// verifyConvertLocaleToallowedSAPLang( new Locale( "in" , "" ), "ID",
		// "Indonesia 2");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "sr", "" ), "EN",
		// "Serbian");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "bg", "" ), "BG",
		// "Bulgarian");
		//		
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ar", "SA" ), "AR",
		// "Arabic Saudi Arabia");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ar", "" ), "AR",
		// "Arabic Saudi Arabia");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ar", "OM" ), "AR",
		// "Arabic Oman");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ar", "TN" ), "AR",
		// "Arabic Tunesien");
		//		
		// verifyConvertLocaleToallowedSAPLang( new Locale( "az", "" ), "EN",
		// "Azerbaijani Latin");
		//		
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ko", "KR" ), "KO",
		// "Korean South");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ko", "KP" ), "KO",
		// "Korean North");
		// //
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ms", "MY" ), "MS",
		// "Malay Malaysia");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ms", "BN" ), "MS",
		// "Malay Brunei Darussalam");
		// //
		// verifyConvertLocaleToallowedSAPLang( new Locale( "pt", "BR" ), "PT",
		// "Portugese Brazil");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "pt", "PT" ), "PT",
		// "Portugese Portugal");
		// //
		// verifyConvertLocaleToallowedSAPLang( new Locale( "sv", "" ), "SV",
		// "Swedish");

		verifyConvertLocaleToallowedSAPLang(new Locale("en"), "EN",
				"English without country");
		verifyConvertLocaleToallowedSAPLang(new Locale("en", ""), "EN",
				"English");
		verifyConvertLocaleToallowedSAPLang(new Locale("en", "US"), "EN",
				"English with Country");
		verifyConvertLocaleToallowedSAPLang(new Locale("en", "US", "Variant"),
				"EN", "English with Variant");

		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "" ), "ZH",
		// "Chinese");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "CN" ), "ZH",
		// "Chinese (China)");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "TW" ), "ZF",
		// "Chinese (Taiwan)");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "HK" ), "EN",
		// "Hong Kong");
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "he", "" ), "HE",
		// "Hebrew 1" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "fr", "FR" ), "FR",
		// "french France" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "fr", "" ), "FR",
		// "french France" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "da", "" ), "DA",
		// "Danish" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "de", "DE" ), "DE",
		// "german Germany" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "de", "CH" ), "DE",
		// "german Germany" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "tr", "" ), "TR",
		// "Turkish" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "af", "" ), "AF",
		// "Afrikaans" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "cs", "" ), "CS",
		// "Czech" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "el", "" ), "EL",
		// "Greek" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "es", "ES" ), "ES",
		// "Spanish Spain" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "es", "" ), "ES",
		// "Spanish Spain" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "it", "IT" ), "IT",
		// "Italian Italy" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "it", "" ), "IT",
		// "Italian Italy" );

		// End B20

		// BIZ Client 000 A1S modus
		// verifyConvertLocaleToallowedSAPLang(new Locale( "de", "DE" ), "DE",
		// "german Germany" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "fr", "FR" ), "FR",
		// "french France" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "fr", "" ), "FR",
		// "french France" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "it", "IT" ), "IT",
		// "Italian Italy" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "da", "" ), "DA",
		// "Danish" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "nl", "NL" ), "NL",
		// "Dutch Netherlands" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "HK" ), "EN",
		// "Hong Kong");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "id", "" ), "EN",
		// "Indonesia 1" );
		// verifyConvertLocaleToallowedSAPLang( new Locale( "in" , "" ), "EN",
		// "Indonesia 2");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "sr", "" ), "EN",
		// "Serbian");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "mk", "" ), "EN",
		// "Macedonian");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ho", "" ), "EN",
		// "Invalid HO");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ar", "SA" ), "EN",
		// "Arabic Saudi Arabia");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "en" ), "EN",
		// "English without country");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "en", "" ), "EN",
		// "English" );
		// verifyConvertLocaleToallowedSAPLang( new Locale( "en", "US" ), "EN",
		// "English with Country");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "en", "US", "Variant"
		// ), "EN", "English with Variant" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "" ), "ZH",
		// "Chinese");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "CN" ), "ZH",
		// "Chinese (China)");
		// End BIZ

		/*
		 * forcedconvert = false: if no corresponding sap language key for the
		 * locale is found the method returns null.
		 */
		forcedconvert = false;
		// verifyConvertLocaleToallowedSAPLang( new Locale( "in" , "" ), "ID",
		// "Indonesia 2");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "sr", "" ), null,
		// "Serbian");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "mk", "" ), null,
		// "Macedonian");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ho", "" ), null,
		// "Invalid HO");
		//		
		// verifyConvertLocaleToallowedSAPLang(new Locale( "id", "" ), "ID",
		// "Indonesia 1" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "bg", "" ), "BG",
		// "Bulgarian");
		//		
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ar", "SA" ), "AR",
		// "Arabic Saudi Arabia");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ar", "" ), "AR",
		// "Arabic Saudi Arabia");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ar", "OM" ), "AR",
		// "Arabic Oman");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ar", "TN" ), "AR",
		// "Arabic Tunesien");
		//		
		// verifyConvertLocaleToallowedSAPLang( new Locale( "az", "" ), null,
		// "Azerbaijani Latin");
		//		
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ko", "KR" ), "KO",
		// "Korean South");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ko", "KP" ), "KO",
		// "Korean North");
		// //
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ms", "MY" ), "MS",
		// "Malay Malaysia");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ms", "BN" ), "MS",
		// "Malay Brunei Darussalam");
		// //
		// verifyConvertLocaleToallowedSAPLang( new Locale( "pt", "BR" ), "PT",
		// "Portugese Brazil");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "pt", "PT" ), "PT",
		// "Portugese Portugal");
		// //
		// verifyConvertLocaleToallowedSAPLang( new Locale( "sv", "" ), "SV",
		// "Swedish");

		verifyConvertLocaleToallowedSAPLang(new Locale("en"), "EN",
				"English without country");
		verifyConvertLocaleToallowedSAPLang(new Locale("en", ""), "EN",
				"English");
		verifyConvertLocaleToallowedSAPLang(new Locale("en", "US"), "EN",
				"English with Country");
		verifyConvertLocaleToallowedSAPLang(new Locale("en", "US", "Variant"),
				"EN", "English with Variant");

		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "" ), "ZH",
		// "Chinese");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "CN" ), "ZH",
		// "Chinese (China)");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "TW" ), "ZF",
		// "Chinese (Taiwan)");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "HK" ), null,
		// "Hong Kong");
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "he", "" ), "HE",
		// "Hebrew 1" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "fr", "FR" ), "FR",
		// "french France" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "fr", "" ), "FR",
		// "french France" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "da", "" ), "DA",
		// "Danish" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "de", "DE" ), "DE",
		// "german Germany" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "de", "CH" ), "DE",
		// "german Germany" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "tr", "" ), "TR",
		// "Turkish" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "af", "" ), "AF",
		// "Afrikaans" );
		//        
		// verifyConvertLocaleToallowedSAPLang(new Locale( "cs", "" ), "CS",
		// "Czech" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "el", "" ), "EL",
		// "Greek" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "es", "ES" ), "ES",
		// "Spanish Spain" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "es", "" ), "ES",
		// "Spanish Spain" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "it", "IT" ), "IT",
		// "Italian Italy" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "it", "" ), "IT",
		// "Italian Italy" );

		// BIZ Client 000 A1S modus
		// verifyConvertLocaleToallowedSAPLang(new Locale( "de", "DE" ), "DE",
		// "german Germany" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "fr", "FR" ), "FR",
		// "french France" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "fr", "" ), "FR",
		// "french France" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "it", "IT" ), "IT",
		// "Italian Italy" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "da", "" ), "DA",
		// "Danish" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "nl", "NL" ), "NL",
		// "Dutch Netherlands" );
		// verifyConvertLocaleToallowedSAPLang( new Locale( "en" ), "EN",
		// "English without country");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "en", "" ), "EN",
		// "English" );
		// verifyConvertLocaleToallowedSAPLang( new Locale( "en", "US" ), "EN",
		// "English with Country");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "en", "US", "Variant"
		// ), "EN", "English with Variant" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "" ), "ZH",
		// "Chinese");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "CN" ), "ZH",
		// "Chinese (China)");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "TW" ), null,
		// "Chinese (Taiwan)");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "he", "" ), null,
		// "Hebrew 1" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "iw", "" ), null,
		// "Hebrew 2" );
		// verifyConvertLocaleToallowedSAPLang( new Locale( "ar", "SA" ), null,
		// "Arabic Saudi Arabia");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "en" ), "EN",
		// "English without country");
		// verifyConvertLocaleToallowedSAPLang( new Locale( "en", "" ), "EN",
		// "English" );
		// verifyConvertLocaleToallowedSAPLang( new Locale( "en", "US" ), "EN",
		// "English with Country");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "en", "US", "Variant"
		// ), "EN", "English with Variant" );
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "" ), "ZH",
		// "Chinese");
		// verifyConvertLocaleToallowedSAPLang(new Locale( "zh", "CN" ), "ZH",
		// "Chinese (China)");
		// End BIZ

	}

	private void testConvertSAPLangToLocale() {
		verifyConvertSAPLangToLocale("EN", null, new Locale("en"), "English");
		verifyConvertSAPLangToLocale("EN", null, new Locale("en", ""),
				"English with empty country");
		verifyConvertSAPLangToLocale("ZH", null, new Locale("zh", "CN"),
				"Chinese (China)");
		verifyConvertSAPLangToLocale("ZF", null, new Locale("zh", "TW"),
				"Chinese (Taiwan) or (HongKong mapped)");
		verifyConvertSAPLangToLocale("HE", null, new Locale("iw", ""), "Hebrew");
		verifyConvertSAPLangToLocale("ID", null, new Locale("in", ""),
				"Indonesia");
		verifyConvertSAPLangToLocale("SR", null, new Locale("sr", ""),
				"Serbian");
		verifyConvertSAPLangToLocale("MK", null, new Locale("mk", ""),
				"Macedonian");
		verifyConvertSAPLangToLocale("HO", null, new Locale("ho", ""),
				"Invalid HO");
		verifyConvertSAPLangToLocale("he", null, new Locale("iw", ""),
				"Hebrew lowercase");
		verifyConvertSAPLangToLocale("id", null, new Locale("in", ""),
				"Indonesia lowercase");
		verifyConvertSAPLangToLocaleFalse("zh", new Locale("zh", "CN"),
				"Chinese (China) lowercase, fails");
		verifyConvertSAPLangToLocaleFalse("zf", new Locale("zh", "TW"),
				"Chinese (Taiwan) or (HongKong mapped) lowercase, fails");

		// get Locale from language and country
		verifyConvertSAPLangToLocale("EN", "en", new Locale("en", "en"),
				"england");
		verifyConvertSAPLangToLocale("de", "de", new Locale("de", "de"),
				"Germany");
		verifyConvertSAPLangToLocale("ZH", "CN", new Locale("zh", "CN"),
				"china");
		verifyConvertSAPLangToLocale("ZF", "CN", new Locale("zh", "TW"),
				"china Taiwan");
		verifyConvertSAPLangToLocale("ZF", "TW", new Locale("zh", "TW"),
				"china Taiwan");
		verifyConvertSAPLangToLocale("DE", "CH", new Locale("de", "CH"),
				"german Switzerland");
		// 4G is not active on the current backend system :(
		// if(LangUtil.isSAPLanguagesAvailable()){
		// verifyConvertSAPLangToLocale(
		// "4G","", new Locale( "de","CH" ), "german Switzerland"
		// );
		// }
	}

	private void verifyConvertSAPLangToLocale(String sSAPLang, String country,
			Locale oLocaleExpected, String sTestName) {

		if (country == null) {
			Locale oLocale = LangUtil.convertSAPLangToLocale(sSAPLang);
			verify(oLocale.equals(oLocaleExpected), "SAPLang " + sSAPLang
					+ " Locale " + oLocale.toString() + " ("
					+ oLocaleExpected.toString() + ")", sTestName);
		} else {
			Locale oLocale = LangUtil.convertSAPLangToLocale(sSAPLang, country);
			verify(oLocale.equals(oLocaleExpected), "SAPLang " + sSAPLang
					+ " Locale " + oLocale.toString() + " ("
					+ oLocaleExpected.toString() + ")", sTestName);
		}

	}

	private void verifyConvertSAPLangToLocaleFalse(String sSAPLang,
			Locale oLocaleExpected, String sTestName) {
		Locale oLocale = LangUtil.convertSAPLangToLocale(sSAPLang);
		verify(!oLocale.equals(oLocaleExpected), "SAPLang " + sSAPLang
				+ " Locale " + oLocale.toString() + " (is not"
				+ oLocaleExpected.toString() + ")", sTestName);
	}

	private void testConvertLocaleToSAPLang() {

		verifyConvertLocaleToSAPLang(new Locale("zh", "HK"), "ZF", "Hong Kong");
		verifyConvertLocaleToSAPLang(new Locale("id", ""), "ID", "Indonesia 1");
		verifyConvertLocaleToSAPLang(new Locale("in", ""), "ID", "Indonesia 2");
		verifyConvertLocaleToSAPLang(new Locale("sr", ""), "SR", "Serbian");
		verifyConvertLocaleToSAPLang(new Locale("mk", ""), "MK", "Macedonian");
		verifyConvertLocaleToSAPLang(new Locale("ho", ""), "HO", "Invalid HO");
		verifyConvertLocaleToSAPLang(new Locale("en"), "EN",
				"English without country");
		verifyConvertLocaleToSAPLang(new Locale("en", ""), "EN", "English");
		verifyConvertLocaleToSAPLang(new Locale("en", "US"), "EN",
				"English with Country");
		verifyConvertLocaleToSAPLang(new Locale("en", "US", "Variant"), "EN",
				"English with Variant");
		verifyConvertLocaleToSAPLang(new Locale("zh", ""), "ZH", "Chinese");
		verifyConvertLocaleToSAPLang(new Locale("zh", "CN"), "ZH",
				"Chinese (China)");
		verifyConvertLocaleToSAPLang(new Locale("zh", "TW"), "ZF",
				"Chinese (Taiwan)");
		verifyConvertLocaleToSAPLang(new Locale("he", ""), "HE", "Hebrew 1");
		verifyConvertLocaleToSAPLang(new Locale("iw", ""), "HE", "Hebrew 2");

		verifyConvertLocaleToSAPLang(new Locale("de", "CH"), "DE",
				"german Switzerland");

		// if (!LangUtil.isSAPLanguagesAvailable()) {
		//				
		// verifyConvertLocaleToSAPLang(new Locale( "de", "CH" ), "DE", "german
		// Germany" );
		// }
		//	        
		// if(LangUtil.isSAPLanguagesAvailable()){
		// }

	}

	private void verifyConvertLocaleToSAPLang(Locale oLocale,
			String sSAPLangExpected, String sTestName) {
		String sSAPLang = null;
		sSAPLang = LangUtil.convertLocaleToSAPLang(oLocale);
		verify(sSAPLang.equals(sSAPLangExpected), "\n Locale: "
				+ oLocale.toString() + ".\n" + " SAPLang: " + sSAPLang + ".\n"
				+ " Expected SAPLanguage: " + sSAPLangExpected, sTestName);
	}

	private void verifyConvertLocaleToallowedSAPLang(Locale oLocale,
			String sSAPLangExpected, String sTestName) {
		String sSAPLang = null;
		if (null == oLocale) {
			return;
		}
		if (LangUtil.isSAPLanguagesAvailable()) {
			if (forcedconvert) {
				sSAPLang = LangUtil
						.convertLocaleToAllowedSAPLangForced(oLocale);
			} else {
				sSAPLang = LangUtil.convertLocaleToAllowedSAPLangTry(oLocale);
			}
		} else {
			if (forcedconvert) {
				sSAPLang = LangUtil
						.convertLocaleToAllowedSAPLangForced(oLocale);
			} else {
				sSAPLang = LangUtil.convertLocaleToAllowedSAPLangTry(oLocale);
			}
		}
		if (sSAPLangExpected == null) {

			verify(sSAPLang == sSAPLangExpected, "\n Locale: "
					+ oLocale.toString() + ".\n" + " SAPLang: " + sSAPLang
					+ ".\n" + " Expected SAPLanguage: " + sSAPLangExpected,
					sTestName);
		} else {
			verify(sSAPLangExpected.equals(sSAPLang), "\n Locale: "
					+ oLocale.toString() + ".\n" + " SAPLang: " + sSAPLang
					+ ".\n" + " Expected SAPLanguage: " + sSAPLangExpected,
					sTestName);
		}
	}
}
