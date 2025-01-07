package com.sap.engine.services.dc.util;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-20
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class ComponentPropsCorrector {

	// % . .. # ? * ! SPACE > < " { } | \ ^ ~
	// [ ] ` ; : @ = & + $ ,
	// / \ : * ? " < > | % [ ] # . .. , =

	// According to mail from Mariela
	// The list of forbidden characters is:

	// / \ : * ? " < > | ; , = & % [ ] #
	// Additionally, application name should not end with "."
	// During application deployment each forbidden character (if present) gets
	// replaced with "~".
	/**
	 * array with all forbidden characters
	 */
	private static final char[] ILLEGAL_CHARS = new char[] { '/', '\\', ':',
			'*', '?', '"', '<', '>', '|', ';', ',', '=', '%', '[', ']', '#',
			'&' };

	private static final char LEGAL_REPLACEMENT_CHAR = '~';

	private ComponentPropsCorrector() {
	}

	/**
	 * Replaces all forbiden characters with the character '~'
	 * 
	 * @param compProp
	 *            the <code>String</code> which has to be chekced and fixed.
	 * @return <code>String</code> which has been checked and fixed.
	 */
	public static String getCorrected(String compProp) {

		// in case of null or blank string ( white space only ) do not go any
		// further
		if (compProp == null || compProp.trim().length() == 0) {
			return compProp;
		}

		// in order to be Unicode 3.1 / 4 compliant and handle correctly the
		// supplemental characters
		// but still keep the good performance for the 16 bit ( BMP ) characters
		// we have to detect this case
		final int len = compProp.length();

		// uncomment this when tc~bl~dc~core becomes java 1.5 compatible
		// final int codePointCount = compProp.codePointCount(0, len);
		// if (len != codePointCount){
		// return getUnicode4Corrected(compProp);
		// }

		final char[] chars = new char[len];
		compProp.getChars(0, len, chars, 0);

		// for each character check if it is an illegal one and replace it
		// accordingly
		for (int i = 0; i < chars.length; i++) {

			for (int j = 0; j < ILLEGAL_CHARS.length; j++) {
				if (chars[i] == ILLEGAL_CHARS[j]) {
					chars[i] = LEGAL_REPLACEMENT_CHAR;
					break; // no need to check for the rest of the illegal chars
				}
			}
		}

		// since the name of the appliation can't end with '.' if this is the
		// case replace the dot with the legal char
		final int lastCharIndex = len - 1;
		if (chars[lastCharIndex] == '.') {
			chars[lastCharIndex] = LEGAL_REPLACEMENT_CHAR;
		}

		return new String(chars);
	}

	// uncomment this when tc~bl~dc~core becomes java 1.5 compatible

	// private static String getUnicode4Corrected(String compProp) {
	//		
	//		
	// final int len = compProp.length();
	// final int codePointCount = compProp.codePointCount(0, len);
	// final int [] correctedCodePoints = new int [codePointCount];
	// boolean corrected = false;
	// int currentCodePoint = 0;
	//		
	// // for each code point check if it is an illegal char and replace it
	// accordingly
	// for (int i = 0; i < codePointCount; i++) {
	//			
	// currentCodePoint = compProp.codePointAt(i);
	// corrected = false;
	// // scan for illegal chars at this code point and correct if needed
	// for (int j = 0; j < ILLEGAL_CHARS.length; j++) {
	//				
	// if( currentCodePoint == ILLEGAL_CHARS[j]){ // illegal char found
	// correctedCodePoints[i] = LEGAL_REPLACEMENT_CHAR;
	// corrected = true;
	// break; // no need to search for more illegal chars at this position
	// }
	// }
	// // if no correction has been done just take the original value
	// if (!corrected){
	// correctedCodePoints[i] = currentCodePoint;
	// }
	// }
	//		
	// // since the name of the appliation can't end with '.' if this is the
	// case replace the dot with the legal char
	// final int lastCodePointIndex = codePointCount - 1;
	// if( correctedCodePoints[ lastCodePointIndex ] == '.' ){
	// correctedCodePoints[ lastCodePointIndex ] = LEGAL_REPLACEMENT_CHAR;
	// }
	//		
	// return new String(correctedCodePoints, 0 , codePointCount);
	// }

}
