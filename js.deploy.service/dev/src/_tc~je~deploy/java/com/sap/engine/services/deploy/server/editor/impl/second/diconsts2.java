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
package com.sap.engine.services.deploy.server.editor.impl.second;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DIConsts2 {

	/**
	 * 'apps' for root configuration
	 */
	public static final String version = "version";
	public static final String serialized = "serialized";
	public static final String version_bin = "version.bin";

	// globalData -> as prop sheet
	static final String globalData = "globalData";
	static final String appName = "appName";
	static final String appLoaderName = "appLoaderName";
	static final String isStandAlone = "isStandAlone";
	static final String failover = "failover";
	static final String additionalClasspath = "additionalClasspath";
	//
	static final String RC_IS_FUNCTIONAL = "is_functional";
	static final String RC_IS_CLASSLOADING = "is_classloading";
	//  
	static final String references = "@references";
	static final String RO_TARGET_NAME = "target_name";
	static final String RO_TARGET_VENDOR = "target_vendor";
	static final String RO_TARGET_TYPE = "target_type";
	static final String RO_REFERENCE_TYPE = "reference_type";
	static final String RO_CHAR_IS_PERSISTENT = "is_persistent";
	//
	static final String resourceReferences = "resRefs";
	static final String RR_TARGET_ID = "target_id";
	static final String RR_TARGET_TYPE = "target_type";
	static final String RR_REFERENCE_TYPE = "reference_type";

	// ContainerData (configuration entry)
	static final String containerData = "@containerData";
	// ContainerData->isOptional (configuration entry)
	static final String isOptional = "isOptional";
	// ContainerData->filesForCL (property sheet)
	static final String filesForCL = "filesForCL";

	static final String heavyFilesForCL = "heavyFilesForCL";
	// ContainerData->deployedComponents (configuration entry)
	static final String deployedComponents = "deployedComponents";
	// ContainerData->deployedComponents->compName (configuration entry)
	static final String compName = "compName";
	// ContainerData->deployedComponents->accessModifier (configuration entry)
	static final String accessModifier = "accessModifier";
	// ContainerData->deployedComponents->resTypes (property sheet)
	static final String resTypes = "resTypes";
	// ContainerData->deployedFileNames (property sheet)
	static final String deployedFileNames = "deployedFileNames";
	//
	static final String remoteSupport = "remoteSupport";
	static final String properties = "properties";

	// TODO - used from out side
	public static final String languageLibs = "languageLibs";

	/**
	 * OTHER - used from out side
	 */
	public static final String appcfg = "appcfg";
	public static final String SAP_MANIFEST = "SAP_MANIFEST";
	public static final String APP_GLOB_PROPS = "application.global.properties";
	public static final String application_xml = "application.xml";
	public static final String containers_info_xml = "containers-info.xml";

	/**
	 * 'deploy' for root configuration
	 */
	static final String status = "status";
	//
	static final String startUp = "startUp";
	static final String encodedExceptionInfo = "encodedExceptionInfo";
	static final String initiallyStarted = "initiallyStarted";
	// ModuleProvider
	static final String moduleProvider = "moduleProvider";
	// ModuleInfo
	static final String j2eeModuleType = "j2eeModuleType";
	static final String moduleUri = "moduleUri";
	static final String fileInfos = "fileInfos";
	static final String fileType = "fileType";
	static final String fileUri = "fileUri";
	//

}
