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
package com.sap.engine.services.dc.cmd.telnet.impl.util;

import java.util.Hashtable;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class TelnetConstants {

	private static Hashtable dcToTelnetConstants = new Hashtable();
	private static Hashtable telnetToDcConstants = new Hashtable();

	public final static String STATUS = "Status";
	public final static String DESCRIPTION = "Description";

	public final static String NAME = "name";
	public final static String VENDOR = "vendor";
	public final static String VALUE = "value";
	public final static String ACTION = "action";
	public final static String LIST = "list";
	public final static String FILE = "file";
	public final static String DIR = "dir";

	public final static String OR = "|";
	public final static String AND = "&";
	public final static String VALUE_ENTRY = "<...>";
	public final static String EQUATION = "=";
	public final static String BRACKET_OPEN = "{";
	public final static String BRACKET_CLOSE = "}";

	public final static String DEFAULT_VENDOR = "sap.com";

	static {

		// -- DC to telnet --//
		// on error
		dcToTelnetConstants.put("OnErrorStop", "stop");
		dcToTelnetConstants.put("OnErrorSkipDepending", "skip_depending");

		// version rules
		dcToTelnetConstants.put("UpdateAllVersions", "all");
		dcToTelnetConstants.put("UpdateSameAndLowerVersionsOnly", "same_lower");
		dcToTelnetConstants.put("UpdateLowerVersionsOnly", "lower");

		// undeploy and deploy workflow

		// lcm
		dcToTelnetConstants.put("disable LCM", "disable");

		// -- telnet to DC --//
		// on error
		telnetToDcConstants.put("stop", "OnErrorStop");
		telnetToDcConstants.put("skip_depending", "OnErrorSkipDepending");

		// version rules
		telnetToDcConstants.put("all", "UpdateAllVersions");
		telnetToDcConstants.put("same_lower", "UpdateSameAndLowerVersionsOnly");
		telnetToDcConstants.put("lower", "UpdateLowerVersionsOnly");

		// undeploy and deploy workflow

		// lcm
		telnetToDcConstants.put("disable", "disable LCM");
	}

	public static String getTelnetConstantByDcConstant(String constant) {
		if (constant == null) {
			return null;
		} else if (dcToTelnetConstants.containsKey(constant)) {
			return (String) dcToTelnetConstants.get(constant);
		} else {
			return constant;
		}
	}

	public static String getDcConstantByTelnetConstant(String constant) {
		if (constant == null) {
			return null;
		} else if (telnetToDcConstants.containsKey(constant)) {
			return (String) telnetToDcConstants.get(constant);
		} else {
			return constant;
		}
	}

}
