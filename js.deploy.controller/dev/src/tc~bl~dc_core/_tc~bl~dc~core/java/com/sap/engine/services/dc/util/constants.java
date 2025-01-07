package com.sap.engine.services.dc.util;

import java.util.Locale;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-13
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public final class Constants {

	private Constants() {
	}

	public static final Locale DC_LOCALE = Locale.ENGLISH;

	public static final String SAP_MANIFEST_FILE = "SAP_MANIFEST.MF";
	// End of line
	public static final String EOL = System.getProperty("line.separator");

	public static final String TAB = "\t";

	public static final String EOL_TAB = EOL + TAB;

	public static final String EOL_TAB_TAB = EOL_TAB + TAB;

	public static final String EOL_TAB_TAB_TAB = EOL_TAB_TAB + TAB;
	// A configuration path separator
	public static final String CFG_PATH_SEPARATOR = "/";

	public static final String DEPLOY_SERVICE_NAME = "deploy";

	public static final String INSTANCE_ID_PREFIX = "ID";
	
	public static final String EMPTY = "";
	
	public static final String UNKNOWN = "UNKNOWN";
	
	public static final String LOCALHOST = "localhost";

}
