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

import static com.sap.engine.services.deploy.server.ExceptionConstants.CFG_CANNOT_READ;
import static com.sap.engine.services.deploy.server.ExceptionConstants.CORRUPT_CONFIGURATION;
import static com.sap.engine.services.deploy.server.ExceptionConstants.DI_MISSING_CFG;
import static com.sap.engine.services.deploy.server.ExceptionConstants.DI_WRONG_APP_NAME;
import static com.sap.engine.services.deploy.server.ExceptionConstants.NOT_AVAILABLE_CONTAINER;
import static com.sap.engine.services.deploy.server.ExceptionConstants.NOT_AVAILABLE_INFO_ABOUT_CONTS;
import static com.sap.engine.services.deploy.server.ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeployExceptionConstContainer;
import com.sap.engine.services.deploy.container.ExceptionInfo;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.util.FailOver;
import com.sap.engine.services.deploy.container.op.util.ModuleProvider;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.ear.exceptions.BaseIllegalArgumentException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.InitiallyStarted;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.Version;
import com.sap.engine.services.deploy.server.editor.DIReader;
import com.sap.engine.services.deploy.server.editor.impl.EditorUtil;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.Convertor;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * For Version.FIRST
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DIReaderImpl1 implements DIReader {
	private final static Location location = 
		Location.getLocation(DIReaderImpl1.class);
	private final Version version = Version.FIRST;

	private void handleException(final String name, final String type,
		final Configuration parent, Exception ex)
		throws ServerDeploymentException {
		ServerDeploymentException sde = new ServerDeploymentException(
			CFG_CANNOT_READ, new Object[] { name, type,
			(parent != null ? parent.getPath() : null) }, ex);
		sde.setDcNameForObjectCaller(parent);
		sde.setMessageID("ASJ.dpl_ds.005300");
		throw sde;
	}

	private void obligatoryISMissing(String name, String type,
		Configuration parent) throws ServerDeploymentException {
		ServerDeploymentException sde = new ServerDeploymentException(
			DI_MISSING_CFG, name, type, 
			(parent.getPath() != null ? parent.getPath() : null));
		sde.setMessageID("ASJ.dpl_ds.005203");
		throw sde;
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
	public DeploymentInfo readDI(final String appID, 
		final Configuration appsCfg, final Configuration deployCfg, 
		final Configuration customAppConfig) throws ServerDeploymentException {
		assert appID != null;
		SimpleLogger.trace(
			Severity.PATH, location, null,"DIReaderImpl1.readDI() called");

		final DeploymentInfo dInfo = createDeploymentInfo(appID, appsCfg);

		initContainerNames(appsCfg, dInfo);
		initIsStandAlone(appsCfg, dInfo);
		dInfo.setReferences(readReferences(appsCfg));
		initRemoteSupport(appsCfg, dInfo);
		initProperties(appsCfg, dInfo);
		initApplicationXml(appsCfg, dInfo);
		initContainersCLFiles(appsCfg, dInfo);
		initOptionalContainers(appsCfg, dInfo);
		initDeployedComponents(appsCfg, dInfo);
		initDeployedFiles(appsCfg, dInfo);
		initAdditionalClasspath(appsCfg, dInfo);
		initResourceReferences(appsCfg, dInfo);
		initFailOver(appsCfg, dInfo);
		initStartUp(deployCfg, dInfo);
		initExceptionInfo(deployCfg, dInfo);
		dInfo.setInitiallyStarted(readInitiallyStarted(deployCfg));

		initConfigProvider(appsCfg, deployCfg, customAppConfig, dInfo);
		initApplicationStatus(deployCfg, dInfo);

		// PRINT IT
		if (location.beDebug()) {
			DSLog.traceDebugObject(location, "{0}", dInfo);
		}
		if (clearDI(dInfo) && location.beDebug()) {
			DSLog.traceDebug(location, " >>> CLEARED <<< : {0}\n", 
				dInfo.getApplicationName());
			DSLog.traceDebugObject(location, "{0}", dInfo);
		}
		return dInfo;
	}

	private void initApplicationStatus(final Configuration deployCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    final Status status = readStatus(deployCfg);
		dInfo.setStatusWithoutShm(
			status, Status.STOPPED.equals(status) ?
			StatusDescriptionsEnum.INITIALLY_STOPPED : null, null);
    }

	private void initConfigProvider(final Configuration appsCfg,
        final Configuration deployCfg, final Configuration customAppConfig,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    // appcfg
		try {
			Configuration appCfg = null;
			dInfo.getConfigProvider().setSapManifest(false);
			dInfo.getConfigProvider().setAppGlobalPropsCfg(false);
			if (customAppConfig != null
					&& customAppConfig.existsSubConfiguration(DIConsts1.appcfg)) {
				appCfg = customAppConfig.getSubConfiguration(DIConsts1.appcfg);
			} else {
				appCfg = appsCfg.getSubConfiguration(DIConsts1.appcfg);
			}
			if (appCfg != null) {
				dInfo.getConfigProvider().setAppGlobalPropsCfg(true);
				if (appCfg != null
						&& appCfg
								.existsSubConfiguration(DIConsts1.SAP_MANIFEST)) {
					appCfg.getSubConfiguration(DIConsts1.SAP_MANIFEST);
					dInfo.getConfigProvider().setSapManifest(true);
				} else {
					dInfo.getConfigProvider().setSapManifest(true);
				}
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.appcfg, "cfg", deployCfg, cex);
		}
    }

	private void initExceptionInfo(final Configuration deployCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			Configuration serverDeployAppConfig = null;
			final String clElemID = ""
					+ PropManager.getInstance().getClElemID();
			final String clElemName = PropManager.getInstance().getClElemName();
			if (deployCfg.existsSubConfiguration(clElemID)) {
				serverDeployAppConfig = deployCfg.getSubConfiguration(clElemID);
			} else if (deployCfg.existsSubConfiguration(clElemName)) {
				serverDeployAppConfig = deployCfg
						.getSubConfiguration(clElemName);
			}
			if (serverDeployAppConfig != null) {
				final ExceptionInfo exceptionInfo = ExceptionInfo
						.encode((String) EditorUtil.getDeserializedObject(
								serverDeployAppConfig,
								DIConsts1.exInfoFromStart));
				if (exceptionInfo != null) {
					dInfo.setExceptionInfo(exceptionInfo);
				}
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.exInfoFromStart, "file", deployCfg, cex);
		}
    }

	private void initStartUp(final Configuration deployCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    final StartUp startUp = readStartUp(deployCfg);
		if (startUp != null) {
			dInfo.setStartUp(startUp.getId().intValue());
		}
    }

	private void initFailOver(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    final FailOver failOver = readFailOver(appsCfg);
		if (failOver != null) {
			dInfo.setFailOver(failOver);
		}
    }

	private void initResourceReferences(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			final String[] encodedResources = (String[]) EditorUtil
					.getDeserializedObject(appsCfg, DIConsts1.encodeResources);
			if (encodedResources != null) {
				DIAdapter1.setResourceReferences(dInfo, DIAdapter1
						.encode(encodedResources));
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.encodeResources, "file/cfgEntry",
					appsCfg, cex);
		}
    }

	private void initAdditionalClasspath(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			if (appsCfg.existsConfigEntry(DIConsts1.additionalClasspath)) {
				dInfo.setAdditionalClasspath((String) appsCfg
						.getConfigEntry(DIConsts1.additionalClasspath));
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.additionalClasspath, "cfgEntry", appsCfg,
					cex);
		}
    }

	private void initDeployedFiles(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			final Map<String, String[]> deployedFileNames = 
				(Map<String, String[]>) EditorUtil.getDeserializedObject(
					appsCfg, DIConsts1.deployedFileNames);
			if (deployedFileNames != null) {
				dInfo.setDeployedFileNames(deployedFileNames);
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.deployedFileNames, "file/cfgEntry",
					appsCfg, cex);
		}
    }

	private void initDeployedComponents(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			Map<String, String[]> deployedComponents = (Map) EditorUtil
					.getDeserializedObject(appsCfg,
							DIConsts1.deployedComponents);
			Map<String, String[]> deployedResources_Types = (Map) EditorUtil
					.getDeserializedObject(appsCfg,
							DIConsts1.deployedResources_Types);
			deployedResources_Types = (deployedResources_Types == null ? new Hashtable<String, String[]>(
					0)
					: deployedResources_Types);

			Map<String, String[]> privateDeployedResources_Types = (Map) EditorUtil
					.getDeserializedObject(appsCfg,
							DIConsts1.privateDeployedResources_Types);
			privateDeployedResources_Types = privateDeployedResources_Types == null ? new Hashtable<String, String[]>(
					0)
					: privateDeployedResources_Types;

			ContainerData cdata;
			String[] resTypes;
			for (String containerName : deployedComponents.keySet()) {
				cdata = dInfo.getOrCreateContainerData(containerName);
				for (String resourceName : deployedComponents
						.get(containerName)) {
					resTypes = deployedResources_Types.get(resourceName);
					if (resTypes != null) {
						for (String rType : resTypes) {
							cdata.addProvidedResource(new Resource(
								resourceName, rType,
								Resource.AccessType.PUBLIC));
						}
					} else {
						resTypes = privateDeployedResources_Types
								.get(resourceName);
						if (resTypes != null) {
							for (String rType : resTypes) {
								cdata.addProvidedResource(new Resource(
										resourceName, rType,
										Resource.AccessType.PRIVATE));
							}
						} else {
							cdata.addProvidedResource(new Resource(
									resourceName, resourceName,
									Resource.AccessType.PUBLIC));
						}
					}
				}
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.deployedComponents, "file/cfgEntry",
					appsCfg, cex);
		}
    }

	private void initOptionalContainers(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			if (appsCfg.existsConfigEntry(DIConsts1.optionalContainers)) {
				dInfo.setOptionalContainers((String[]) appsCfg
						.getConfigEntry(DIConsts1.optionalContainers));
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.optionalContainers, "cfgEntry", appsCfg,
					cex);
		}
    }

	private void initContainersCLFiles(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			final String[] containerCLfiles = (String[]) EditorUtil
					.getDeserializedObject(appsCfg, DIConsts1.containerCLFiles);
			if (containerCLfiles != null) {
				DIAdapter1.setFilesForCL(dInfo, containerCLfiles);
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.containerCLFiles, "file/cfgEntry",
					appsCfg, cex);
		}
    }

	private void initProperties(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			Properties props = null;
			if (appsCfg.existsSubConfiguration(DIConsts1.properties)) {
				Configuration propsConfig = appsCfg
						.getSubConfiguration(DIConsts1.properties);
				if (propsConfig != null) {
					PropertySheet prSheet = propsConfig
							.getPropertySheetInterface();
					if (prSheet != null) {
						props = prSheet.getProperties();
						if (props != null) {
							dInfo.setProperties(props);
						}
					}
				}
			}
			if (props == null) {
				if (appsCfg.existsConfigEntry(DIConsts1.appProperties)) {
					dInfo.setProperties((Properties) appsCfg
							.getConfigEntry(DIConsts1.appProperties));
				}
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.appProperties, "cfgEntry", appsCfg, cex);
		}
    }

	private void initRemoteSupport(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			if (appsCfg.existsConfigEntry(DIConsts1.remoteSupport)) {
				dInfo.setRemoteSupport(
					(String[]) appsCfg.getConfigEntry(DIConsts1.remoteSupport));
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.remoteSupport, "cfgEntry", appsCfg, cex);
		}
    }

	private void initIsStandAlone(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			if (appsCfg.existsConfigEntry(DIConsts1.isStandAlone)) {
				dInfo.setStandAloneArchive(
					((Boolean)appsCfg.getConfigEntry(DIConsts1.isStandAlone))
						.booleanValue());
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.isStandAlone, "cfgEntry", appsCfg, cex);
		}
    }

	private void initContainerNames(final Configuration appsCfg,
        final DeploymentInfo dInfo) throws ServerDeploymentException {
	    try {
			if (appsCfg.existsConfigEntry(DIConsts1.containerNames)) {
				dInfo.setContainerNames(
					(String[])appsCfg.getConfigEntry(DIConsts1.containerNames));
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.containerNames, "cfgEntry", appsCfg, cex);
		}
    }

	private DeploymentInfo createDeploymentInfo(final String appID,
        final Configuration appsCfg) throws ServerDeploymentException {
	    try {
			if (appsCfg.existsConfigEntry(DIConsts1.appName)) {
				final String persistedAppID = 
					(String) appsCfg.getConfigEntry(DIConsts1.appName);
				if (!appID.equals(persistedAppID)) {
					ServerDeploymentException sde = 
						new ServerDeploymentException(DI_WRONG_APP_NAME,
							persistedAppID, appID);
					sde.setMessageID("ASJ.dpl_ds.005202");
					throw sde;
				}
			} else {
				obligatoryISMissing(DIConsts1.appName, "cfgEntry", appsCfg);
			}
		} catch (ConfigurationException ex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				CORRUPT_CONFIGURATION, new Object[] { appsCfg.getPath() }, ex);
			sde.setDcNameForObjectCaller(appsCfg);
			sde.setMessageID("ASJ.dpl_ds.005075");
			throw sde;
		}
		return new DeploymentInfo(appID, getVersion());
    }

	private void initApplicationXml(final Configuration appsCfg,
		final DeploymentInfo dInfo) throws ServerDeploymentException {
		try {
			final String applicationXML = 
				(String) EditorUtil.getDeserializedObject(
					appsCfg, DIConsts1.descriptor);
			if (applicationXML != null) {
				dInfo.setApplicationXML(applicationXML);
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.descriptor, "file/cfgEntry", appsCfg, cex);
		}
	}

	private boolean clearDI(DeploymentInfo dInfo) {
		boolean result = false;
		final Hashtable cName_cData = dInfo.getCNameAndCData();
		final Enumeration cnEnum = cName_cData.keys();
		String cName = null;
		ContainerData cData = null;
		Set<Resource> dComps = null;
		while (cnEnum.hasMoreElements()) {
			cName = (String) cnEnum.nextElement();
			cData = (ContainerData) cName_cData.get(cName);
			dComps = cData.getProvidedResources();
			if (dComps == null || dComps.size() == 0) {
				cName_cData.remove(cName);
				result = true;
			}
		}
		return result;
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
		String[] contNames = dInfo.getContainerNames();

		if (contNames == null || contNames.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				NOT_AVAILABLE_INFO_ABOUT_CONTS,
				new Object[] { dInfo.getApplicationName(), action });
			sde.setMessageID("ASJ.dpl_ds.005033");
			throw sde;
			
		} else {
			if (location.bePath()) {
				DSLog.tracePath(location, 
					"Will bootstrap application [{0}] deployed on [{1}].",
					dInfo.getApplicationName(),	
					Convertor.toString(contNames, ""));
			}
		}

		ContainerInterface currentInt = null;
		String temp[] = null;

		for (int i = 0; i < contNames.length; i++) {
			currentInt = communicator
					.getContainer(contNames[i]);
			if (currentInt == null) {
				if (!dInfo.isOptionalContainer(contNames[i])) {
					throw new ServerDeploymentException(
						NOT_AVAILABLE_CONTAINER,
						new Object[] { contNames[i], action,
							dInfo.getApplicationName() });
				}
			} else {
				try {
					currentInt.downloadApplicationFiles(dInfo
							.getApplicationName(), config);
				} catch (WarningException wex) {
					temp = wex.getWarnings();
					if (temp != null && location.beWarning()) {
						for (int k = 0; k < temp.length; k++) {
							DSLog.traceWarning(location, "ASJ.dpl_ds.000211", "{0}",
									temp[k]);
						}
					}
				} catch (ServerDeploymentException sdex) {
					throw sdex;
				} catch (OutOfMemoryError oofmer) {
					throw oofmer;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable th) {
					ServerDeploymentException sde = 
						new ServerDeploymentException(
							UNEXPECTED_EXCEPTION_OCCURRED,
							new Object[] { "downloading files of application "
								+ dInfo.getApplicationName() }, th);
					sde.setMessageID("ASJ.dpl_ds.005082");
					throw sde;
				}
			}
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

		ClassLoader threadLoader = null;
		try {
			threadLoader = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader());

			Object refs = null;
			try {
				refs = EditorUtil.getDeserializedObject(appOrDeployCfg,
						DIConsts1.references);
			} catch (ConfigurationException cex) {
				handleException(DIConsts1.references, "file/cfgEntry",
						appOrDeployCfg, cex);
			}
			if (refs == null) {
				return null;
			}

			ReferenceObject[] refObj = null;
			if (refs instanceof String[]) {
				refObj = DIAdapter1.encodeReferenceObjectArray((String[]) refs);
			} else if (refs instanceof ReferenceObject[]) {
				refObj = (ReferenceObject[]) refs;
			} else if (refs instanceof Hashtable) {
				if (refs != null) {
					List<ReferenceObject> refsArr = new ArrayList<ReferenceObject>();
					Enumeration enum1 = ((Hashtable) refs).keys();
					String refName = null;

					while (enum1.hasMoreElements()) {
						refName = (String) enum1.nextElement();
						if (refName != null) {
							refsArr.add(new ReferenceObject(refName,
									(String) ((Hashtable) refs).get(refName)));
						}
					}
					refObj = new ReferenceObject[refsArr.size()];
					refsArr.toArray(refObj);
				}
			}
			return refObj;
		} finally {
			Thread.currentThread().setContextClassLoader(threadLoader);
		}
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
		return InitiallyStarted.getDefaultInitiallyStarted();
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
		return null;// basauce it is not applicable to this varsion of the
		// DeploymentInfo
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIReader#readStatus(com.
	 * sap.engine.frame.core.configuration.Configuration,
	 * com.sap.engine.frame.core.configuration.Configuration)
	 */
	public Status readStatus(Configuration deployCfg,
			Configuration customAppConfig, boolean isStrict)
			throws ServerDeploymentException {
		// appStatus
		try {
			if (customAppConfig != null
					&& customAppConfig.existsConfigEntry(DIConsts1.status)) {
				final String tempStatus = (String) customAppConfig
						.getConfigEntry(DIConsts1.status);
				return Status.getStatusByName(tempStatus);
			} else if (deployCfg != null
					&& deployCfg.existsConfigEntry(DIConsts1.appStatus)) {
				final Byte tempStatus = (Byte) deployCfg
						.getConfigEntry(DIConsts1.appStatus);
				return Status.getStatusByID(tempStatus.byteValue());
			} else {
				if (isStrict) {
					obligatoryISMissing(DIConsts1.status, "cfgEntry",
							customAppConfig);
				}
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.status, "cfgEntry", customAppConfig, cex);
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
		try {
			if (appsCfg.existsConfigEntry(DIConsts1.failover)) {
				final String failOverKey = ((String) appsCfg
						.getConfigEntry(DIConsts1.failover));
				final FailOver failOver = FailOver
						.getFailOverByKey(failOverKey);
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
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.failover, "cfgEntry", appsCfg, cex);
		} catch (BaseIllegalArgumentException biae) {
			handleException(DIConsts1.failover, "cfgEntry", appsCfg, biae);
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
			if (deployCfg.existsConfigEntry(DIConsts1.startUp)) {
				final Integer tempStartUp = (Integer) deployCfg
						.getConfigEntry(DIConsts1.startUp);
				return StartUp.getStartUpByID(tempStartUp.byteValue());
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.startUp, "cfgEntry", deployCfg, cex);
		}
		return null;
	}

	private Status readStatus(Configuration deployCfg)
		throws ServerDeploymentException {
		try {
			if (deployCfg.existsConfigEntry(DIConsts1.status)) {
				final String status = 
					(String)deployCfg.getConfigEntry(DIConsts1.startUp);
				return Status.getStatusByName(status);
			}
		} catch (ConfigurationException cex) {
			handleException(DIConsts1.startUp, "cfgEntry", deployCfg, cex);
		}
		return Status.STOPPED;
	}
}
