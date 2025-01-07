﻿/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.deployment;

import java.io.File;

/**
 * @author Mariela Todorova
 */
public interface Constants {
	public static final String sep = File.separator;
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	// constants for directory names
	public static final String META_INF = "META-INF";
	public static final String SERVER = "server";
	public static final String DISPATCHER = "dispatcher";
	public static final String DEPLOYMENT = "deployment";
	public static final String CFG = "cfg";
	public static final String SDA = "SDA";
	public static final String SAP_DM = "SAP_DM";
	public static final String UPDATE_MODULE = "update_module";
	public static final String MODULES = "modules";
	public static final String STREAM = "stream";
	public static final String WS_PACK = "ws_pack";

	// constants for file names
	public static final String SAP_MANIFEST = "SAP_MANIFEST.MF";
	public static final String MANIFEST = "MANIFEST.MF";
	public static final String APPLICATION_XML = "application.xml";
	public static final String ADD_APPLICATION_XML = "application-j2ee-engine.xml";
	public static final String EJB_XML = "ejb-jar.xml";
	public static final String APPCLIENT_XML = "application-client.xml";
	public static final String RA_XML = "ra.xml";
	public static final String PROVIDER_XML = "provider.xml";
	public static final String SDA_XML = "sda.xml";
	public static final String DPLAN = "deployment_plan.properties";
	public static final String DPLAN_ZIP = "deployment_plan.zip";
	public static final String LOGGING = "logging.properties";
	public static final String DEPLOY_PROPS = "deployment14.properties";

	// constants for file extensions
	public static final String EAR = ".ear";
	public static final String JAR = ".jar";
	public static final String WAR = ".war";
	public static final String RAR = ".rar";
	public static final String PAR = ".par";
	public static final String EPA = ".epa";

	// constants for SDA Generation attributes
	public static final String SOFTWARE_TYPE = "softwaretype";
	public static final String NAME = "name";
	public static final String VENDOR = "vendor";
	public static final String LOCATION = "location";
	public static final String COUNTER = "counter";
	public static final String SUBTYPE = "subtype";
	public static final String CONTEXT_ROOT = "context-root";
	public static final String CONTEXT_ROOTS = "context-roots";
	public static final String DEPENDENCIES = "dependencies";

	// constants for SDA key attributes
	public static final String KEY_NAME = "keyname";
	public static final String KEY_VENDOR = "keyvendor";
	public static final String KEY_LOCATION = "keylocation";
	public static final String KEY_COUNTER = "keycounter";
	public static final String KEY_SUBTYPE = "softwaresubtype";

	// constants for deployment plan properties
	public static final String ROOT_MODULE_NAME = "root_module_name";
	public static final String STAND_ALONE = "stand_alone";
	public static final String REFERENCE = "reference";
	public static final String WEBSERVICES_PACK = "webservices_pack";
	public static final String CONTEXT = "context_root";
	public static final String CONTEXTS = "context_roots";

	// constants for Deploy Service properties
	public static final String APP_NAME = "application_name";
	public static final String PROVIDER = "provider_name";
	public static final String WEB = "web:";

	// constants for xml tags
	public static final String COMPONENT_NAME = "component-name";
	public static final String PROVIDER_NAME = "provider-name";
	public static final String DISPLAY_NAME = "display-name";
}
