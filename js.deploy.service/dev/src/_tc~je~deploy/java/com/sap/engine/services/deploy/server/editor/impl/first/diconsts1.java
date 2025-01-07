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
package com.sap.engine.services.deploy.server.editor.impl.first;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
class DIConsts1 {

	// with 'apps' for root configuration
	static final String appName = "STR:ApplicationName";
	static final String containerNames = "STR[]:ContainerNames";
	static final String isStandAlone = "BOOL:IsStandAloneArchive";
	static final String references = "HASH_STR_STR:Reference_type";
	static final String remoteSupport = "STR[]:RemoteSupport";
	static final String appProperties = "PROPS_STR_STR:ApplicationProperties";
	static final String properties = "Properties";
	static final String descriptor = "BYTE[]:deploymentDescriptor";
	static final String containerCLFiles = "STR[]:AppLoaderFilePaths";
	static final String optionalContainers = "STR[]:optionalContainers";
	static final String deployedComponents = "HASH_STR_STR[]:Cont_CompNames";
	static final String deployedResources_Types = "HASH_STR_STR[]:Res_Types";
	static final String privateDeployedResources_Types = "HASH_STR_STR[]:PrivateRes_Types";
	static final String deployedFileNames = "HASH_STR_STR[]:Cont_FileNames";
	static final String loaderName = "STR:Loader";
	static final String additionalClasspath = "STR:AdditionalClasspath";
	static final String encodeResources = "STR[]:EncodeResources";
	static final String failover = "STR:Failover";

	static final String appcfg = "appcfg";
	static final String SAP_MANIFEST = "SAP_MANIFEST";
	static final String APP_GLOB_PROPS = "application.global.properties";

	// with 'deploy' for root configuration
	static final String appStatus = "BYTE:AppStatus";
	static final String status = "status";
	static final String startUp = "STR:StartUp";
	static final String exInfoFromStart = "STR:ExceptionInfo";
	static final String initiallyStarted = "initiallyStarted";

}
