package com.sap.engine.services.dc.util;

import com.sap.bc.proj.jstartup.JStartupFramework;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class SystemProfileManager {

	public static final String SYS_PARAM_SAP_SYSTEM_NAME = "SAPSYSTEMNAME";
	public static final String JAVA_HOME = "jstartup/vm/home";
	public static final String SYS_DATASOURCE_NAME = "SYS_DATASOURCE_NAME";
	public static final String CLUSTER_INSTANCE_ID = "j2ee/instance_id";
	public static final String MS_HOST = "j2ee/ms/host";
	public static final String MS_PORT = "j2ee/ms/port";
	public static final String DIR_EXECUTABLE = "DIR_EXECUTABLE";
	public static final String DIR_CLUSTER = "jstartup/DIR_CLUSTER";
	public static final String DIR_HOME = "DIR_HOME";
	public static final String DIR_CT_RUN = "DIR_CT_RUN";
	public static final String SAPSYSTEM = "SAPSYSTEM";
	public static final String SAPLOCALHOST = "SAPLOCALHOST";
	public static final String DIR_GLOBAL = "DIR_GLOBAL";

	private SystemProfileManager() {
	}

	public static String getSysParamValue(String key) {
		return JStartupFramework.getParam(key);
	}

}
