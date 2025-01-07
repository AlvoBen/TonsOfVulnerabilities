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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.DeserializationFailedException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.lib.io.hash.FolderCompareResult;
import com.sap.engine.lib.io.hash.HashUtils;
import com.sap.engine.lib.io.hash.Index;
import com.sap.engine.lib.io.hash.PathNotFoundException;
import com.sap.engine.lib.util.iterators.ArrayEnumeration;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeployExceptionConstContainer;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ExceptionInfo;
import com.sap.engine.services.deploy.container.ReferenceType;
import com.sap.engine.services.deploy.container.ResourceReferenceType;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.util.FailOver;
import com.sap.engine.services.deploy.container.op.util.FileInfo;
import com.sap.engine.services.deploy.container.op.util.FileType;
import com.sap.engine.services.deploy.container.op.util.ModuleInfo;
import com.sap.engine.services.deploy.container.op.util.ModuleProvider;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.exceptions.BaseIllegalArgumentException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.InitiallyStarted;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.dpl_info.module.Version;
import com.sap.engine.services.deploy.server.editor.DIReader;
import com.sap.engine.services.deploy.server.editor.impl.EditorUtil;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.engine.services.deploy.server.utils.FSUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DIReaderImpl2 implements DIReader {
	private final static Location location = 
		Location.getLocation(DIReaderImpl2.class);
	
	private final Version version = Version.SECOND;

	private void handleException(final String name, final String type,
		final Configuration parent, final Exception ex)
		throws ServerDeploymentException {
		ServerDeploymentException sde = new ServerDeploymentException(
			ExceptionConstants.CFG_CANNOT_READ,
			new String[] { name, type + "",
				(parent != null ? parent.getPath() : null) }, ex);
		sde.setMessageID("ASJ.dpl_ds.005300");
		sde.setDcNameForObjectCaller(parent);
		throw sde;
	}

	private void obligatoryISMissing(String name, String type,
		Configuration parent) throws ServerDeploymentException {
		ServerDeploymentException sde = new ServerDeploymentException(
			ExceptionConstants.DI_MISSING_CFG,
			name, type + "",
			(parent.getPath() != null ? parent.getPath() : null));
		sde.setMessageID("ASJ.dpl_ds.005203");
		throw sde;
	}

	private void readDIModuleIS(String what, Configuration from,
		String wrongValue) throws ServerDeploymentException {
		ServerDeploymentException sde = new ServerDeploymentException(
			ExceptionConstants.DI_MODULE_READ_FROM_IS, 
			what, (from.getPath() != null ? from.getPath() : null),
			wrongValue);
		sde.setMessageID("ASJ.dpl_ds.005204");
		throw sde;
	}

	private DeploymentInfo readSerialized(Configuration appsCfg) 
		throws ServerDeploymentException {
		try {
			if (appsCfg.existsFile(DIConsts2.serialized)) {
				final DeploymentInfo dInfo = (DeploymentInfo) EditorUtil
					.getDeserializedObject(appsCfg, DIConsts2.serialized);
				// DeploymentInfo->readModuleProvider->no need, because it is
				// not read during the readDI(...)
				// DeploymentInfo->status->needed
				dInfo.setStatusWithoutShm(Status.STOPPED,
						StatusDescriptionsEnum.INITIALLY_STOPPED, null);
				// DeploymentInfo->additionalClasspath->no need, because it is
				// simply set
				// DeploymentInfo->languageLibs->no need, because are handled
				// from the app_libraries_container
				// DeploymentInfo->ContainerData->filesForCL->needed
				final Iterator<ContainerData> cDataIters = dInfo
						.getCNameAndCData().values().iterator();
				ContainerData cData = null;
				while (cDataIters.hasNext()) {
					cData = cDataIters.next();
					// relative->absolute
					DIAdapter2.setFilesForCL(cData, cData.getFilesForCL());
					DIAdapter2.setHeavyFilesForCL(cData, cData
							.getHeavyFilesForCL());// relative -> absolute
				}
				return dInfo;
			}
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.serialized, "file", appsCfg, ce);
		} catch (DeploymentException dex) {
			DSLog.traceDebugThrowable(
				location,
				null, dex,
				"ASJ.dpl_ds.000214",
				"The deployment info for [{0}] application was NOT deserialized from DB. It will be read.",
				appsCfg.getPath());
		}
		return null;
	}

	private void readGlobalData(DeploymentInfo dInfo, Configuration appsCfg)
			throws ServerDeploymentException {
		Properties props = readPropertiesFromGlobalData(appsCfg);
		if (props == null) {
			obligatoryISMissing(DIConsts2.globalData, "subCfg", appsCfg);
		}

		{// appName
			final String appName = props.getProperty(DIConsts2.appName);
			if (!dInfo.getApplicationName().equals(appName)) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.DI_WRONG_APP_NAME, new String[] {
								dInfo.getApplicationName(), appName });
				sde.setMessageID("ASJ.dpl_ds.005202");
				throw sde;
			}
		}
		{// isStandAlone
			final String isStandAlone = props
					.getProperty(DIConsts2.isStandAlone);
			if (isStandAlone != null) {
				dInfo.setStandAloneArchive(new Boolean(isStandAlone)
						.booleanValue());
			}
		}
		{// failover
			final FailOver failOver = readFailOver(props, appsCfg);
			if (failOver != null) {
				dInfo.setFailOver(failOver);
			}
		}
		{// additionalClasspath
			final String additionalClasspath = props
					.getProperty(DIConsts2.additionalClasspath);
			if (additionalClasspath != null) {
				dInfo.setAdditionalClasspath(additionalClasspath);
			}
		}
	}

	private void readReferences(DeploymentInfo dInfo, Configuration appsCfg)
			throws ServerDeploymentException {
		final ReferenceObject[] refObj = readReferences(appsCfg);
		if (refObj != null && refObj.length > 0) {
			dInfo.setReferences(refObj);
		}
	}

	private void readResourceReferences(ContainerData cData,
			Configuration contData) throws ServerDeploymentException {
		try {
			final Set<ResourceReference> resRefs = readResourceReferences(contData);
			if (resRefs != null) {
				cData.setResourceReferences(resRefs);
			}
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.resourceReferences, "subCfg", contData,
					ce);
		}
	}

	private void readCNameAndCData(DeploymentInfo dInfo, Configuration appsCfg)
			throws ServerDeploymentException {
		try {
			final Hashtable<String, ContainerData> cName_cData = readContainerData(appsCfg);
			if (cName_cData != null) {
				dInfo.setCNameAndCData(cName_cData);
			}
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.containerData, "subCfg++", appsCfg, ce);
		}
	}

	private void readRemoteSupport(DeploymentInfo dInfo, Configuration appsCfg)
			throws ServerDeploymentException {
		try {
			final Set rsSet = readPropSheetWithOrder(appsCfg,
					DIConsts2.remoteSupport);
			if (rsSet != null) {
				dInfo.setRemoteSupport(CAConvertor.cObject(rsSet));
			}
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.remoteSupport, "subCfg->PropSheet",
					appsCfg, ce);
		}
	}

	private void readAppProperties(DeploymentInfo dInfo, Configuration appsCfg)
			throws ServerDeploymentException {
		try {
			final Properties appProps = ConfigUtils
					.getPropsFromSubConfiguration(appsCfg, DIConsts2.properties);
			if (appProps != null) {
				dInfo.setProperties(appProps);
			}
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.properties, "subCfg->PropSheet", appsCfg,
					ce);
		}
	}

	private void readApplicationXML(final DeploymentInfo dInfo,
			final Configuration appsCfg) throws ServerDeploymentException {
		try {
			if (appsCfg.existsFile(DIConsts2.application_xml)
					|| appsCfg.existsConfigEntry(DIConsts2.application_xml)) {
				final String applicationXML = (String) EditorUtil
						.getDeserializedObject(appsCfg,
								DIConsts2.application_xml);
				if (applicationXML != null) {
					dInfo.setApplicationXML(applicationXML);
				}
			}
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.application_xml, "file", appsCfg, ce);
		}
	}

	private void readContainersInfoXML(final DeploymentInfo dInfo,
			final Configuration appsCfg) throws ServerDeploymentException {
		try {
			if (appsCfg.existsFile(DIConsts2.containers_info_xml)) {
				final String contInfoXML = (String) EditorUtil
						.getDeserializedObject(appsCfg,
								DIConsts2.containers_info_xml);
				if (contInfoXML != null) {
					dInfo.setContainerInfoXML(contInfoXML);
				}
			}
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.containers_info_xml, "file", appsCfg, ce);
		}
	}

	private void readAppCfgAndRest(DeploymentInfo dInfo, Configuration appsCfg,
			Configuration customAppConfig) throws ServerDeploymentException {
		try {
			Configuration appCfg = null;
			dInfo.getConfigProvider().setSapManifest(false);
			dInfo.getConfigProvider().setAppGlobalPropsCfg(false);
			if (customAppConfig != null
					&& customAppConfig.existsSubConfiguration(DIConsts2.appcfg)) {
				appCfg = customAppConfig.getSubConfiguration(DIConsts2.appcfg);
			} else {
				appCfg = appsCfg.getSubConfiguration(DIConsts2.appcfg);
			}
			if (appCfg != null) {
				dInfo.getConfigProvider().setAppGlobalPropsCfg(true);
				if (appCfg.existsSubConfiguration(DIConsts2.SAP_MANIFEST)) {
					appCfg.getSubConfiguration(DIConsts2.SAP_MANIFEST);
					dInfo.getConfigProvider().setSapManifest(true);
				}
			}
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.appcfg, "subCfg", appsCfg, ce);
		}
	}

	private void readStartUp(DeploymentInfo dInfo, Configuration deployCfg)
			throws ServerDeploymentException {
		final StartUp startUp = readStartUp(deployCfg);
		if (startUp != null) {
			dInfo.setStartUp(startUp.getId().intValue());
		}
	}

	private void readExceptionInfo(DeploymentInfo dInfo, Configuration deployCfg)
			throws ServerDeploymentException {
		Configuration serverDeployAppConfig = null;
		try {
			final String clElemID = ""
					+ PropManager.getInstance().getClElemID();
			final String clElemName = PropManager.getInstance().getClElemName();
			if (deployCfg.existsSubConfiguration(clElemID)) {
				serverDeployAppConfig = deployCfg.getSubConfiguration(clElemID);
			} else if (deployCfg.existsSubConfiguration(clElemName)) {
				serverDeployAppConfig = deployCfg
						.getSubConfiguration(clElemName);
			}
			if (serverDeployAppConfig != null
					&& serverDeployAppConfig
							.existsFile(DIConsts2.encodedExceptionInfo)) {
				final ExceptionInfo exceptionInfo = ExceptionInfo
						.encode((String) EditorUtil.getDeserializedObject(
								serverDeployAppConfig,
								DIConsts2.encodedExceptionInfo));
				if (exceptionInfo != null) {
					dInfo.setExceptionInfo(exceptionInfo);
				}
			}
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.encodedExceptionInfo, "file",
					serverDeployAppConfig, ce);
		} catch (ServerDeploymentException sde) {
			DSLog.logErrorThrowable(location, 
				"ASJ.dpl_ds.006380",
				"Error in object deserialization on reading Exception info",
				sde);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIReader#readDI(java.lang
	 * .String, com.sap.engine.frame.core.configuration.Configuration,
	 * com.sap.engine.frame.core.configuration.Configuration, ,
	 * com.sap.engine.frame.core.configuration.Configuration)
	 */
	public DeploymentInfo readDI(String appName, Configuration appsCfg,
			Configuration deployCfg, Configuration customAppConfig)
			throws ServerDeploymentException {
		ValidateUtils.nullValidator(appName, "application name");

		DeploymentInfo dInfo = new DeploymentInfo(appName, getVersion());

		// ********************APPS********************//
		// serialized
		{
			final DeploymentInfo serializedDInfo = readSerialized(appsCfg);
			if (serializedDInfo != null) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"The deployment info {0} for application [{1}] " +
					"was deserialized from DB and will be returned.",
					serializedDInfo, appName);
				return serializedDInfo;
			}
		}
		// golbalData
		readGlobalData(dInfo, appsCfg);
		// encodedReferences
		readReferences(dInfo, appsCfg);
		// containerCLFiles, containerNames, optionalContainers,
		// deployedComponents
		// deployedResources_Types, privateDeployedResources_Types,
		// deployedFileNames
		readCNameAndCData(dInfo, appsCfg);
		// remoteSupport
		readRemoteSupport(dInfo, appsCfg);
		// properties
		readAppProperties(dInfo, appsCfg);
		// application_xml
		readApplicationXML(dInfo, appsCfg);
		// containers-info.xml
		readContainersInfoXML(dInfo, appsCfg);
		// appcfg, SAP_MANIFEST
		readAppCfgAndRest(dInfo, appsCfg, customAppConfig);

		// ********************DEPLOY********************//
		// status
		dInfo.setStatusWithoutShm(Status.STOPPED,
				StatusDescriptionsEnum.INITIALLY_STOPPED, null);
		// startUp
		readStartUp(dInfo, deployCfg);
		// exInfoFromStart
		readExceptionInfo(dInfo, deployCfg);
		// initiallyStarted
		dInfo.setInitiallyStarted(readInitiallyStarted(deployCfg));

		// PRINT IT
		if (location.beDebug()) {
			DSLog.traceDebugObject(location, "{0}", dInfo);
		}

		return dInfo;
	}

	private Hashtable<String, ContainerData> readContainerData(
			Configuration appsCfg) throws ConfigurationException,
			ServerDeploymentException {
		Configuration contData = null;
		if (appsCfg.existsSubConfiguration(DIConsts2.containerData)) {
			contData = appsCfg.getSubConfiguration(DIConsts2.containerData);
		} else {
			obligatoryISMissing(DIConsts2.containerData, "subCfg", appsCfg);
		}
		final String[] cNames = contData.getAllSubConfigurationNames();
		Hashtable<String, ContainerData> cName_cData = null;
		if (cNames != null && cNames.length > 0) {
			cName_cData = new Hashtable<String, ContainerData>();
			ContainerData cData = null;
			String cName = null;
			for (int i = 0; i < cNames.length; i++) {
				cName = cNames[i];
				cData = readOneContainerData(contData, cName);
				cName_cData.put(cName, cData);
			}
		} else {
			obligatoryISMissing("<cont_name>", "subCfg", contData);
		}

		if (cName_cData == null || cName_cData.size() == 0) {
			readDIModuleIS("cName_cData", contData, "NULL or 0");
		}

		return cName_cData;
	}

	private ContainerData readOneContainerData(Configuration contData,
			String cName) throws ConfigurationException,
			ServerDeploymentException {
		final Configuration currCDataCfg = contData.getSubConfiguration(cName);
		final ContainerData cData = new ContainerData(cName);

		final Boolean b = (Boolean) currCDataCfg
				.getConfigEntry(DIConsts2.isOptional);
		cData.setOptional(b.booleanValue());

		final Set filesForCL = readPropSheetWithOrder(currCDataCfg,
				DIConsts2.filesForCL);
		DIAdapter2.setFilesForCL(cData, filesForCL);

		final Set heavyFilesForCL = readPropSheetWithOrder(currCDataCfg,
				DIConsts2.heavyFilesForCL);
		DIAdapter2.setHeavyFilesForCL(cData, heavyFilesForCL);

		final Set<Resource> deployedComponents = readProvidedResources(currCDataCfg);
		cData.setProvidedResources(deployedComponents);

		final Set deployedFileNames = readPropSheetWithOrder(currCDataCfg,
				DIConsts2.deployedFileNames);
		if (deployedFileNames != null) {
			cData.setDeployedFileNames(deployedFileNames);
		}

		readResourceReferences(cData, currCDataCfg);

		if (cData == null) {
			readDIModuleIS("ContainerData", currCDataCfg, "NULL");
		}

		return cData;
	}

	private Set<Resource> readProvidedResources(Configuration currCDataCfg)
			throws ServerDeploymentException, ConfigurationException {
		final Set<Resource> deployedComponents = new LinkedHashSet<Resource>();
		final Configuration dcParent = currCDataCfg
				.getSubConfiguration(DIConsts2.deployedComponents);
		final String childs[] = dcParent.getAllSubConfigurationNames();
		if (childs != null) {
			Resource dComp = null;
			Configuration currDCompCfg = null;
			for (int i = 0; i < childs.length; i++) {
				currDCompCfg = dcParent.getSubConfiguration(childs[i]);
				dComp = null;
				final String compName = (String) currDCompCfg
						.getConfigEntry(DIConsts2.compName);
				final String accessModifier = (String) currDCompCfg
						.getConfigEntry(DIConsts2.accessModifier);

				final Set rtSet = readPropSheetWithOrder(currDCompCfg,
						DIConsts2.resTypes);
				if (rtSet != null) {
					for (Object type : rtSet) {
						deployedComponents.add(new Resource(compName,
								(String) type, Resource.AccessType
										.valueOf(accessModifier)));
					}
				} else {
					deployedComponents.add(new Resource(compName, compName,
							Resource.AccessType.valueOf(accessModifier)));

				}
			}
		}

		if(deployedComponents.size() == 0) {
			throw new IllegalStateException(
					"ASJ.dpl_ds.006049 Each deployed application must have at least one deployed component.");
		}
		return deployedComponents;
	}

	// WARNING: has to support two types of reading
	// - without order
	// - with order
	// The order starts from 1.
	private Set readPropSheetWithOrder(Configuration cfg, String psName)
			throws ConfigurationException, ServerDeploymentException {
		final Properties props = ConfigUtils.getPropsFromSubConfiguration(cfg,
				psName);
		if (props == null) {
			return null;
		}

		final boolean hasOrder = hasOrder(props.values());
		Set result = null;
		if (hasOrder) {
			result = new LinkedHashSet();
			int keys[] = null;
			try {
				keys = CAConvertor.cInteger(props.keySet());
			} catch (NumberFormatException nfex) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "reading property sheet with name "
								+ psName + " from configuration "
								+ cfg.getPath() }, nfex);
				sde.setMessageID("ASJ.dpl_ds.005082");
				sde.setDcNameForObjectCaller(cfg);
				throw sde;
			}
			Arrays.sort(keys);
			for (int i = 0; i < keys.length; i++) {
				result.add(props.get(keys[i] + ""));
			}
		} else {
			result = props.keySet();
		}
		return result;
	}

	private boolean hasOrder(Collection coll) {
		if (coll != null) {
			final Iterator iter = coll.iterator();
			String temp = null;
			while (iter.hasNext()) {
				temp = (String) iter.next();
				if (temp != null && !temp.trim().equals("")) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIReader#bootstrapApp(com
	 * .sap.engine.frame.core.configuration.Configuration,
	 * com.sap.engine.services.deploy.server.TransactionCommunicator,
	 * com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo,
	 * java.lang.String)
	 */
	public void bootstrapApp(Configuration config,
			TransactionCommunicator communicator, DeploymentInfo dInfo,
			String action) throws ServerDeploymentException {
		ValidateUtils.nullValidator(config, "'apps' configuration");
		ValidateUtils.nullValidator(dInfo, "deployment info");

		if (!needBootstrap(config, dInfo)) {
			return;
		}

		deleteVersionBin(dInfo);

		// languageLibs
		downloadLibraries(config, DIConsts2.languageLibs, dInfo, action);

		final String[] contNames = dInfo.getContainerNames();
		if (contNames == null || contNames.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NOT_AVAILABLE_INFO_ABOUT_CONTS,
					new String[] { dInfo.getApplicationName(), action });
			sde.setMessageID("ASJ.dpl_ds.005033");
			throw sde;
		} 
			if (location.bePath()) {
			DSLog.tracePath(location, "Will bootstrap application [{0}] deployed on [{1}].",
				dInfo.getApplicationName(), CAConvertor.toString(contNames, ""));
		}
		ContainerInterface currentInt = null;
		try {
			for (int i = 0; i < contNames.length; i++) {
				currentInt = communicator.getContainer(contNames[i]);
				if (currentInt == null) {
					if (!dInfo.isOptionalContainer(contNames[i])) {
						ServerDeploymentException sde = new ServerDeploymentException(
								ExceptionConstants.NOT_AVAILABLE_CONTAINER,
								new String[] { contNames[i], action,
										dInfo.getApplicationName() });
						sde.setMessageID("ASJ.dpl_ds.005006");
						throw sde;
					}
				} else {
					currentInt.downloadApplicationFiles(dInfo
							.getApplicationName(), config);
				}
			}
		} catch (WarningException wex) {
			String temp[] = wex.getWarnings();
			if (temp != null && location.beWarning()) {
				for (int k = 0; k < temp.length; k++) {
					DSLog.traceWarningWithFaultyComponentCSN(location, currentInt,
							"ASJ.dpl_ds.000218", "{0}", temp[k]);
				}
			}
		} catch (ServerDeploymentException sdex) {
			throw sdex;
		} catch (OutOfMemoryError oofmer) {
			throw oofmer;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "downloading files of application "
							+ dInfo.getApplicationName() }, th);
			sde.setMessageID("ASJ.dpl_ds.005082");
			throw sde;
		}

		updateVersionBin(config, dInfo);
	}

	private boolean needBootstrap(Configuration appConfig, DeploymentInfo dInfo)
			throws ServerDeploymentException {
		if (PropManager.getInstance().isApplicationResynch()) {
			traceIsApplicationResynch2True(dInfo);
			return true;
		}

		if (dInfo.getIndexFS() != null) {
			final Index indexDB = ConfigUtils.getIndexDB(appConfig, 
				PropManager.getInstance().getConfigurationHandlerFactory());
			if (indexDB != null) {
				if(equalsFS2DBIndex(indexDB, dInfo)) {
					boolean isOnlyCfgStorageUsed = isOnlyCfgStorageUsed(
							indexDB, dInfo.getCNameAndCData().keySet(), dInfo);
					if (isOnlyCfgStorageUsed) {
						return false;
					}
				}
				return true;
			}
		} 
			traceNoIndexFS(dInfo);
			return true;
		}

	private boolean equalsFS2DBIndex(Index indexDB, DeploymentInfo dInfo) {
		if (dInfo.getIndexFS().getName().equals(indexDB.getName())) {
			final FolderCompareResult fcr = dInfo.getIndexFS().compare(indexDB,
					true);
			if (fcr.isChanged()) {
				traceNotEqualIndexes(dInfo, CAConstants.EOL + fcr);
			} else {
				traceEqualIndexes(dInfo);
			}
			return !fcr.isChanged();
		} 
			traceNotEqualIndexes(
			dInfo, CAConstants.EOL + 
			"=> Index names can only be different for applications deployed on 7.10");
			return false;
		}

	private boolean isOnlyCfgStorageUsed(Index indexDB, Set<String> cNamesSet,
			DeploymentInfo dInfo) {
		final Iterator<String> cNames = cNamesSet.iterator();
		String cName = null;
		while (cNames.hasNext()) {
			cName = cNames.next();
			try {
				indexDB.getFolder(cName);
			} catch (PathNotFoundException e) {
				traceNotOnlyCfgStorageUsed(dInfo, cName);
				return false;
			}
		}
		traceOnlyCfgStorageUsed(dInfo, cNamesSet);
		return true;
	}

	private void traceNotOnlyCfgStorageUsed(DeploymentInfo dInfo, String cName) {
		if (location.beDebug()) {
			DSLog.traceDebug(location, 
							"The [{0}] application binaries bootstrapping from DB will be delegated to the containers, because there is no sub configuration with [{1}] name.",
				dInfo.getApplicationName(),
				cName);
		}
	}

	private void traceOnlyCfgStorageUsed(DeploymentInfo dInfo,
			Set<String> cNames) {
		if (location.beDebug()) {
			DSLog.traceDebug(location, 
							"The [{0}] application binaries bootstrapping from DB WON'T be delegated to the containers, because there are sub configurations with [{1}] names.",
				dInfo.getApplicationName(),
				cNames);
		}
	}

	private void deleteVersionBin(DeploymentInfo dInfo) {
		final File version_bin = dInfo.getVersionBin();
		if (version_bin.exists()) {
			// load current index, if not loaded, otherwise will stay null
			dInfo.getIndexFS();
			version_bin.delete();
		} else {
			dInfo.setIndexFS(null);
		}
	}

	private void updateVersionBin(Configuration config, DeploymentInfo dInfo)
			throws ServerDeploymentException {
		final Index indexDB = ConfigUtils.getIndexDB(config, PropManager
				.getInstance().getConfigurationHandlerFactory());
		if (indexDB != null) {
			final File version_bin = dInfo.getVersionBin();
			EditorUtil.setSerializedObject(version_bin, indexDB, version_bin
					.getAbsolutePath());
			dInfo.updateIndexFS();
		}
	}

	private void downloadLibraries(Configuration config, String subCfgName,
			DeploymentInfo dInfo, String action)
			throws ServerDeploymentException {
		try {
			Configuration libsCfg = null;
			libsCfg = ConfigUtils.getExistsSubConfiguration(config, subCfgName);
			final String libDir = dInfo.getThisAppWorkDir() + subCfgName;
			if (libsCfg != null) {
				final Index dbIndex = libsCfg.getIndex();
				final File libDirAsFile = new File(libDir);
				if (!libDirAsFile.exists()) {
					libDirAsFile.mkdirs();
				}
				final Index fsIndex = HashUtils.getIndex(new File(libDir));

				final FolderCompareResult fcResult = fsIndex.compare(dbIndex,
						true);
				if (fcResult.isChanged()) {
					{
						final ArrayEnumeration deletedEntries = fcResult
								.getDeletedEntries();
						if (deletedEntries != null) {
							FileUtils.deleteFiles(deletedEntries, libDir);
						}
					}
					{
						final ArrayEnumeration newFolders = fcResult
								.getNewFolders();
						if (newFolders != null) {
							String newFolder = null;
							File dir = null;
							while (newFolders.hasNext()) {
								newFolder = (String) newFolders.next();
								dir = new File(libDir + newFolder);
								if (!dir.exists()) {
									dir.mkdirs();
								}
							}
						}
					}
					{
						final ArrayEnumeration filesForDownload = fcResult
								.getFilesForDownload();
						if (filesForDownload != null) {
							File lib = null;
							Configuration rightCfg = null;
							String subLibCfgs = null;
							while (filesForDownload.hasNext()) {
								lib = new File(libDir + filesForDownload.next());
								subLibCfgs = FSUtils.relativePath(libDir, lib
										.getParent());
								rightCfg = ConfigUtils.getSubCnfiguration(
										libsCfg, subLibCfgs);
								FSUtils.downloadFile(rightCfg.getFile(lib
										.getName()), lib);
							}
						}
					}
				}
			} else {
				DUtils.deleteDirectory(new File(libDir));
			}

		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_DOWNLOAD_FILES, new String[] {
							subCfgName, dInfo.getApplicationName(), action },
					cex);
			sde.setMessageID("ASJ.dpl_ds.005037");
			sde.setDcNameForObjectCaller(config);
			throw sde;

		} catch (IOException ioex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_DOWNLOAD_FILES, new String[] {
							subCfgName, dInfo.getApplicationName(), action },
					ioex);
			sde.setMessageID("ASJ.dpl_ds.005037");
			sde.setDcNameForObjectCaller(config);
			throw sde;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIReader#readReferences(
	 * com.sap.engine.frame.core.configuration.Configuration)
	 */
	public ReferenceObject[] readReferences(Configuration appOrDeployCfg)
			throws ServerDeploymentException {
		ValidateUtils.nullValidator(appOrDeployCfg,
				"'apps/deploy' configuration");

		ReferenceObject refObjs[] = null;
		try {
			if (appOrDeployCfg.existsSubConfiguration(DIConsts2.references)) {
				final Configuration refsCfg = appOrDeployCfg
						.getSubConfiguration(DIConsts2.references);
				final String subNames[] = refsCfg.getAllSubConfigurationNames();
				if (subNames != null) {
					PropertySheet ps = null;
					refObjs = new ReferenceObject[subNames.length];
					for (int i = 0; i < subNames.length; i++) {
						ps = refsCfg.getSubConfiguration(subNames[i])
								.getPropertySheetInterface();
						refObjs[i] = readReferenceObject(ps);
					}
				}
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts2.references, "subCfg", appOrDeployCfg, cex);
		}

		return refObjs;
	}

	private ReferenceObject readReferenceObject(PropertySheet ps)
			throws ConfigurationException {
		final ReferenceObject refObj = new ReferenceObject();
		final Properties props = ps.getProperties();
		refObj.setReferenceTarget(props.getProperty(DIConsts2.RO_TARGET_NAME));
		{// NOTE : the provide name may be null. is that correct? (read/write)
			final String provider = props
					.getProperty(DIConsts2.RO_TARGET_VENDOR);
			if (provider != null) {
				refObj.setReferenceProviderName(provider);
			}
		}
		refObj.setReferenceTargetType(props
				.getProperty(DIConsts2.RO_TARGET_TYPE));
		refObj.setReferenceType(props.getProperty(DIConsts2.RO_REFERENCE_TYPE));
		{// ReferenceType
			final String sIsFunc = props
					.getProperty(DIConsts2.RC_IS_FUNCTIONAL);
			final String sIsCL = props
					.getProperty(DIConsts2.RC_IS_CLASSLOADING);
			refObj.setCharacteristic(new ReferenceType(booleanValue(sIsFunc),
					booleanValue(sIsCL), true));
		}
		return refObj;
	}

	private Set<ResourceReference> readResourceReferences(Configuration contData)
			throws ConfigurationException {
		ValidateUtils.nullValidator(contData, "'contData' configuration");

		Set<ResourceReference> resRefs = null;
		if (contData.existsSubConfiguration(DIConsts2.resourceReferences)) {
			final Configuration refsCfg = contData
					.getSubConfiguration(DIConsts2.resourceReferences);
			final String subNames[] = refsCfg.getAllSubConfigurationNames();
			if (subNames != null) {
				PropertySheet ps = null;
				resRefs = new LinkedHashSet<ResourceReference>();
				for (int i = 0; i < subNames.length; i++) {
					ps = refsCfg.getSubConfiguration(subNames[i])
							.getPropertySheetInterface();
					resRefs.add(readResourceReference(ps));
				}
			}
		}

		return resRefs;
	}

	private boolean booleanValue(String source) {
		return Boolean.valueOf(
				source == null ? Boolean.TRUE.toString() : source)
				.booleanValue();
	}

	private ResourceReference readResourceReference(PropertySheet ps)
			throws ConfigurationException {
		final ResourceReference resRef = new ResourceReference();
		final Properties props = ps.getProperties();
		resRef.setResRefName(props.getProperty(DIConsts2.RR_TARGET_ID));
		resRef.setResRefType(props.getProperty(DIConsts2.RR_TARGET_TYPE));
		resRef.setReferenceType(props.getProperty(DIConsts2.RR_REFERENCE_TYPE));
		{// ResourceReferenceType
			final String sIsFunc = props
					.getProperty(DIConsts2.RC_IS_FUNCTIONAL);
			final String sIsCL = props
					.getProperty(DIConsts2.RC_IS_CLASSLOADING);
			resRef.setType(new ResourceReferenceType(booleanValue(sIsFunc),
					booleanValue(sIsCL)));
		}
		return resRef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.VersionInterface#getVersion
	 * ()
	 */
	public Version getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIReader#readInitiallyStarted
	 * (com.sap.engine.frame.core.configuration.Configuration)
	 */
	public InitiallyStarted readInitiallyStarted(Configuration deployCfg)
			throws ServerDeploymentException {
		try {
			final String tempInitiallyStarted = (String) ConfigUtils
					.getExistingConfigEntry(deployCfg,
							DIConsts2.initiallyStarted);
			if (tempInitiallyStarted != null) {
				final InitiallyStarted initiallyStarted = InitiallyStarted
						.getStartInitiallyByName(tempInitiallyStarted);
				if (initiallyStarted == null) {
					throw new IllegalArgumentException(
							"ASJ.dpl_ds.006050 There is no initially started object with name "
									+ tempInitiallyStarted + " .");
				}
				return initiallyStarted;
			}
		} catch (DeserializationFailedException dfex) {
			// $JL-EXC$ - the InitiallyStarted object was persisted instead of
			// its
			// name
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.initiallyStarted, "cfgEntry", deployCfg,
					ce);
		} catch (ClassCastException ccex) {
			// $JL-EXC$ - the InitiallyStarted object was persisted instead of
			// its
			// name
		}
		return InitiallyStarted.getDefaultInitiallyStarted();// default value
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIReader#readModuleProvider
	 * (com.sap.engine.frame.core.configuration.Configuration, java.lang.String)
	 */
	public ModuleProvider readModuleProvider(Configuration deployCfg,
			String thisAppWorkDir) throws ServerDeploymentException {
		try {
			final Configuration mpCfg = ConfigUtils.getExistsSubConfiguration(
					deployCfg, DIConsts2.moduleProvider);
			if (mpCfg == null) {
				return null;
			}
			final String mpSubCfgNames[] = mpCfg.getAllSubConfigurationNames();
			if (mpSubCfgNames == null || mpSubCfgNames.length == 0) {
				return null;
			}
			Configuration miCfg = null, fisCfg = null, fiCfg = null;
			String jmtName = null, mUri = null, fisSubNames[] = null, fileTypeName = null, fileUri;
			List<FileInfo> fisArr = null;
			List<ModuleInfo> miArr = new ArrayList<ModuleInfo>();
			File file = null;
			FileType fileType = null;
			FileInfo fileInfo = null, fileInfos[] = null;
			ModuleInfo mInfo = null, mInfos[] = null;
			J2EEModule.Type jmType = null;
			for (int i = 0; i < mpSubCfgNames.length; i++) {
				miCfg = mpCfg.getSubConfiguration(mpSubCfgNames[i]);
				jmtName = (String) miCfg
						.getConfigEntry(DIConsts2.j2eeModuleType);
				jmType = J2EEModule.Type.valueOf(jmtName);
				if (jmType == null) {
					throw new IllegalStateException(
							"ASJ.dpl_ds.006051 There is no J2EEModuleType with name "
									+ jmtName + " ."
									+ "The error occurred while reading "
									+ miCfg + " configuration.");
				}
				mUri = (String) miCfg.getConfigEntry(DIConsts2.moduleUri);
				fisCfg = miCfg.getSubConfiguration(DIConsts2.fileInfos);
				fisSubNames = fisCfg.getAllSubConfigurationNames();
				fisArr = new ArrayList<FileInfo>();
				for (int k = 0; k < fisSubNames.length; k++) {
					fiCfg = fisCfg.getSubConfiguration(fisSubNames[k]);
					fileUri = (String) fiCfg.getConfigEntry(DIConsts2.fileUri);
					file = new File(thisAppWorkDir + File.separator + fileUri);
					FSUtils.downloadFile(fiCfg.getFile(fiCfg
							.getAllFileEntryNames()[0]), file);
					fileTypeName = (String) fiCfg
							.getConfigEntry(DIConsts2.fileType);
					fileType = FileType.getFileTypeByName(fileTypeName);
					if (fileType == null) {
						throw new IllegalStateException(
								"ASJ.dpl_ds.006052 There is no FileType with name "
										+ fileTypeName + " ."
										+ "The error occurred while reading "
										+ fiCfg + " configuration.");
					}
					fileInfo = new FileInfo(fileType, file.getAbsolutePath(),
							fileUri);
					fisArr.add(fileInfo);
				}
				fileInfos = new FileInfo[fisArr.size()];
				fisArr.toArray(fileInfos);

				mInfo = new ModuleInfo(jmType, mUri, fileInfos);
				miArr.add(mInfo);
			}
			mInfos = new ModuleInfo[miArr.size()];
			miArr.toArray(mInfos);

			return new ModuleProvider(mInfos);
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.moduleProvider, "cfg", deployCfg, ce);
		}
		return null;// default value
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIReader#readStatus(com.
	 * sap.engine.frame.core.configuration.Configuration,
	 * com.sap.engine.frame.core.configuration.Configuration, boolean)
	 */
	public Status readStatus(Configuration deployCfg,
			Configuration customAppConfig, boolean isStrict)
			throws ServerDeploymentException {
		// deployCfg - should not be used
		try {
			if (customAppConfig != null
					&& customAppConfig.existsConfigEntry(DIConsts2.status)) {
				final String tempStatus = (String) customAppConfig
						.getConfigEntry(DIConsts2.status);
				return Status.getStatusByName(tempStatus);
			} 
				if (isStrict) {
					obligatoryISMissing(DIConsts2.status, "cfgEntry",
							customAppConfig);
				}
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.status, "cfgEntry", deployCfg, ce);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIReader#readFailOver(com
	 * .sap.engine.frame.core.configuration.Configuration)
	 */
	public FailOver readFailOver(Configuration appsCfg)
			throws ServerDeploymentException {
		Properties props = readPropertiesFromGlobalData(appsCfg);
		return readFailOver(props, appsCfg);
	}

	private Properties readPropertiesFromGlobalData(Configuration appsCfg)
			throws ServerDeploymentException {
		Properties props = null;
		try {
			props = ConfigUtils.getPropsFromSubConfiguration(appsCfg,
					DIConsts2.globalData);
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.globalData, "subCfg", appsCfg, ce);
		}
		return props;
	}

	private FailOver readFailOver(Properties props, Configuration appsCfg)
			throws ServerDeploymentException {
		if (props == null) {
			obligatoryISMissing(DIConsts2.globalData, "subCfg", appsCfg);
		}
		final String failOverKey = props.getProperty(DIConsts2.failover);
		if (failOverKey != null) {
			final FailOver failOver = FailOver.getFailOverByKey(failOverKey);
			if (failOver == null) {
				throw new BaseIllegalArgumentException(
						DeployExceptionConstContainer.WRONG_FAIL_OVER_VALUE,
						new String[] {
								failOverKey,
								FailOver.getKeyAndFailOver().keySet()
										.toString() });
			}
			return failOver;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIReader#readStartUp(com
	 * .sap.engine.frame.core.configuration.Configuration)
	 */
	public StartUp readStartUp(Configuration deployCfg)
			throws ServerDeploymentException {
		try {
			final String tempStartUp = (String) ConfigUtils
					.getExistingConfigEntry(deployCfg, DIConsts2.startUp);
			return StartUp.getStartUpByName(tempStartUp);
		} catch (ConfigurationException ce) {
			handleException(DIConsts2.startUp, "cfgEntry", deployCfg, ce);
		}
		return null;
	}

	private void traceIsApplicationResynch2True(DeploymentInfo dInfo) {
		if (location.beDebug()) {
			DSLog.traceDebug(location, 
							"The [{0}] application binaries bootstrapping from DB will be delegated to the containers, because of [{1}] property of deploy service.",
							dInfo.getApplicationName(),
				PropManager.APPLICATION_RESYNCH);
		}
	}

	private void traceNotEqualIndexes(DeploymentInfo dInfo, String where) {
		if (location.beDebug()) {
			DSLog.traceDebug(location, 
							"The [{0}] application binaries bootstrapping from DB will be delegated to the containers, because DB and FS indexes are not equal in [{1}].",
							dInfo.getApplicationName(), where);
		}
	}

	private void traceEqualIndexes(DeploymentInfo dInfo) {
		if (location.beDebug()) {
			DSLog.traceDebug(location, 
							"The [{0}] application binaries bootstrapping from DB evaluation continues, because DB and FS indexes are equal.",
							dInfo.getApplicationName());
		}
	}

	private void traceNoIndexFS(DeploymentInfo dInfo) {
		if (location.beDebug()) {
			DSLog.traceDebug(location, 
							"The [{0}] application binaries bootstrapping from DB will be delegated to the containers, because there is no valid FS index on application level.",
							dInfo.getApplicationName());
		}
	}
}
