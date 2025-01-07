/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
 
package com.sap.engine.services.httpserver.lib.util;

/**
 * Helps in preparation of well formated strings with hex data.
 * @author Nikolay Petkov
 * @version 1.0
 */
public class HexPrinter {
	//	table to convert a nibble to a hex char.
	private static char[] hexChars = {
		'0', '1', '2', '3', '4', '5', '6', '7' ,
		'8', '9', 'a', 'b',	'c', 'd', 'e', 'f'};

	/**
	 * Mekes well formated HEX representation of a <code>byte</code> array.
	 * @param bytes <code>byte</code> array
	 * @param offset offset from where to start reading
	 * @param length length of bytes to read
	 * @return well formated HEX representation of input <code>byte</code> array
	 */
	public static String toString(byte[] bytes, int offset, int length) {
		if (length == 0) {
			return "";
		} 
		
		short address = 0;
		StringBuffer sb = new StringBuffer();	
		appendHex(sb, address); sb.append(": ");
		
		int i = offset, j = offset;
		int end = offset + length, lineEnd = offset + 16;
		for (; i < end; i++) {
			if (i == lineEnd) {
				sb.append("| ");
				for (; j < lineEnd; j++) {
					appendAscii(sb, bytes[j]);
				}
				
				sb.append("\r\n");
				appendHex(sb, address); sb.append(": ");
				lineEnd += 16; address += 16;
			}
			
			appendHex(sb, bytes[i]); sb.append(" ");
		}
		
		for (; i < lineEnd; i++) {
			sb.append("   ");
		}
					
		sb.append("| ");
		for (; j < end; j++) {
			appendAscii(sb, bytes[j]);
		}
		
		return sb.toString();
	}
	
	/**
	 * Appends HEX representation of a <code>byte</code> to an <code>StringBuffer</code>
	 * @param sb <code>StringBuffer</code> where to append
	 * @param b <code>byte</code> to append
	 */
	private static void appendHex(StringBuffer sb, byte b) {
	   sb.append(hexChars[(b & 0xf0) >>> 4]);
	   sb.append(hexChars[b & 0x0f]);
	}

	/**
	 * Appends HEX representation of an <code>short</code> to an <code>StringBuffer</code>
	 * @param sb <code>StringBuffer</code> where to append
	 * @param s <code>short</code> to append
	 */
	private static void appendHex(StringBuffer sb, short s) {
	   sb.append(hexChars[(s & 0xf000) >>> 12]);
	   sb.append(hexChars[(s & 0x0f00) >>> 8]);
	   sb.append(hexChars[(s & 0x00f0) >>> 4]);
	   sb.append(hexChars[s & 0x000f]);
	}
	
	/**
	 * Appends ASCII representation of a <code>byte</code> to an <code>StringBuffer</code>
	 * if it holds printable ASCII char, else dot ("."). 
	 * @param sb <code>StringBuffer</code> where to append
	 * @param b <code>byte</code> to append
	 */
	private static void appendAscii(StringBuffer sb, byte b) {
		if (32 <= b && b <= 127) { // only printable ASCII chars
			sb.append((char)b);
		} else {
			sb.append('.'); 
		}
	}
}
