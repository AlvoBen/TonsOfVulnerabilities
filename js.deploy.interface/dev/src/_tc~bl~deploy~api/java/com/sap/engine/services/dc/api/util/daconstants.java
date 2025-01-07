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

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Repository for some common constants.</DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date:</B></DT>
 * <DD>2004-11-1</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public interface DAConstants {
	/**
	 * line separator
	 */
	public static final String EOL = System.getProperty("line.separator");
	/**
	 * single indent
	 */
	public static final String SINGLE_INDENT = "\t";
	/**
	 * indent
	 */
	public static final String INDENT = "\t\t";
	/**
	 * line separator plus indent
	 */
	public static final String EOL_INDENT = EOL + INDENT;
	/**
	 * line separator plus 2 indents
	 */
	public static final String EOL_INDENT_INDENT = EOL_INDENT + SINGLE_INDENT;
	/**
	 * line separator plus 3 indents
	 */
	public static final String EOL_INDENT_INDENT_INDENT = EOL_INDENT_INDENT
			+ SINGLE_INDENT;

	public static final String EOL_INDENT_INDENT_INDENT_INDENT = EOL_INDENT_INDENT_INDENT
			+ SINGLE_INDENT;
	/**
	 * line separator plus single indent
	 */
	public static final String EOL_SINGLE_INDENT = EOL + SINGLE_INDENT;

	/**
	 * true value of this system property tells to the DC API to upload the
	 * deployment archives to the server nevertheless the client and the server
	 * are running on the same machine and the server can read the archives.
	 */
	public static final String SYSTEM_DO_NOT_CHECK_FOR_LOCAL_HOST = "dc.api.doNotCheckForLocalhost";
	
	/**
	 * equal
	 */
	public static final String EQUAL = "=";
	
	/**
	 * none
	 */
	public static final String NONE = "none";	
}
