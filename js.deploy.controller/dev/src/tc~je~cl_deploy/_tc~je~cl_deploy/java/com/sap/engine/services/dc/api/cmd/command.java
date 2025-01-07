/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Sep 7, 2005
 */
package com.sap.engine.services.dc.api.cmd;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Sep 7, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public interface Command {
	public static final int CODE_SUCCESS = 100;
	public static final int CODE_SUCCESS_WITH_WARNINGS = 102;
	public static final int CODE_ERROR_OCCURRED = 104;
	public static final int CODE_ERROR_ALL_ITEMS_ALREADY_DEPLOYED = 105;
	public static final int CODE_ERROR_ENGINE_TIMEOUT = 106;
	public static final int CODE_PREREQUISITE_VIOLATED = 108;
	public static final int CODE_SYNTAX_INCORRECT = 112;
	public static final int CODE_CRITICAL_ERROR = 116;
	public static final String EOL = System.getProperty("line.separator");
	public static final String TAB = "\t";
	public static final String TWO_TABS = TAB + TAB;
	public static final String THREE_TABS = TWO_TABS + TAB;
	public static final String EOL_TAB = EOL + TAB;
	public static final String EOL_2TABS = EOL_TAB + TAB;
	public static final String EOL_3TABS = EOL_2TABS + TAB;

	public String getName();

	public int init(String name, String[] argList);

	public int execute();

	public void usage();

	public String getDescription();

	public void destroy();

}
