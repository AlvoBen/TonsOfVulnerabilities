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
package com.sap.engine.services.dc.api.util;

import java.security.MessageDigest;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Repository with some convenienced static methods.</DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date:</B></DT>
 * <DD>2004-11-2</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class DAUtils {
	public static final char[] hexChars = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * generates unique ID
	 * 
	 * @return
	 * @deprecated
	 */
	synchronized public static String createSID() {
		java.rmi.dgc.VMID vmid = new java.rmi.dgc.VMID();
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(vmid.toString().getBytes("UTF-8"));
			return hexStringFromBytes(md5.digest());
		} catch (Exception e) {
			return vmid.toString();// Base64.encodeString(vmid.toString());
		}
	}

	/**
	 * generates human readable representation of the given byte array
	 * 
	 * @param b
	 * @return
	 */
	public static String hexStringFromBytes(byte[] b) {
		String hex = "";
		int msb;
		int lsb = 0;
		int i;
		int mod = hexChars.length;
		for (i = 0; i < b.length; i++) {
			msb = (b[i] & 0x000000FF) / mod;
			lsb = (b[i] & 0x000000FF) % mod;
			hex = hex + hexChars[msb] + hexChars[lsb];
		}
		return (hex);
	}

	/**
	 * extracts the class name of the given parameter
	 * 
	 * @param t
	 * @return
	 */
	public static String getThrowableClassName(Throwable t) {
		if (t == null) {
			return "argument is null";
		}
		String tmp = t.getClass().getName();
		int pos = tmp.lastIndexOf(".");
		if (pos != -1) {
			return tmp.substring(pos + 1);
		}
		return tmp;
	}

	/**
	 * This method accepts a file path (either unix or windows one ) and tries
	 * to determine the file name by searching for the last index of the file
	 * separator.
	 * 
	 * @param filePath
	 *            the file path to be searched for
	 * @return the determined file name or the original string if no file
	 *         separators have been found
	 */
	public static String getFileName(String filePath) {
		int pos1 = filePath.lastIndexOf("/");
		int pos2 = filePath.lastIndexOf("\\");
		if (pos1 != -1 || pos2 != -1) {
			filePath = filePath.substring(Math.max(pos1, pos2) + 1);
		}
		return filePath;
	}

	/**
	 * 
	 * @param name
	 *            argument name
	 * @param obj
	 *            the object that is checked for null
	 * @throws IllegalArgumentException
	 *             with the appropriate message if obj is null
	 */
	public static void validateNull(String name, Object obj)
			throws IllegalArgumentException {
		if (obj == null) {
			throw new IllegalArgumentException(name + " should not be null.");
		}
	}

}
