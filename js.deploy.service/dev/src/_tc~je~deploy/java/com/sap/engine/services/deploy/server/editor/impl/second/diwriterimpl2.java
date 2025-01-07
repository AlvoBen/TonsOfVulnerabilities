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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ExceptionInfo;
import com.sap.engine.services.deploy.container.ReferenceType;
import com.sap.engine.services.deploy.container.ResourceReferenceType;
import com.sap.engine.services.deploy.container.op.IOpConstants;
import com.sap.engine.services.deploy.container.op.util.FailOver;
import com.sap.engine.services.deploy.container.op.util.FileInfo;
import com.sap.engine.services.deploy.container.op.util.ModuleInfo;
import com.sap.engine.services.deploy.container.op.util.ModuleProvider;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.InitiallyStarted;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.dpl_info.module.Version;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.editor.impl.EditorUtil;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DIWriterImpl2 implements DIWriter {

	private final Version version = Version.SECOND;
	
	private static final Location location = 
		Location.getLocation(DIWriterImpl2.class);
	
	public void modifyDeploymentInfo(final Configuration appCfg,
		final Configuration deployCfg, final DeploymentInfo dInfo)
		throws DeploymentException {

		ValidateUtils.nullValidator(appCfg, "'apps' configuration");
		// deployCfg may be null
		ValidateUtils.nullValidator(dInfo, "deployment info");

		try {
			internallyModifyDeploymentInfo(appCfg, deployCfg, dInfo);
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_STORE_D_INFO_IN_DB, 
				new Object[] { dInfo.getApplicationName(), "" }, cex);
			sde.setMessageID("ASJ.dpl_ds.005048");
			sde.setDcNameForObjectCaller(deployCfg);
			throw sde;
		}
	}

	public void modifySerialized(Configuration appCfg, DeploymentInfo dInfo)
		throws DeploymentException {
		try {
			if (dInfo != null) {
				final DeploymentInfo dInfo4Serialization = 
					(DeploymentInfo) dInfo.clone();
				{
					// DeploymentInfo->ContainerData->filesForCL->needed
					final Iterator<ContainerData> cDataIters = dInfo4Serialization
							.getCNameAndCData().values().iterator();
					ContainerData cData = null;
					while (cDataIters.hasNext()) {
						cData = cDataIters.next();
						// absolute -> relative
						cData.setFilesForCL(DIAdapter2.getFilesForCL(cData));
						// absolute -> relative
						cData.setHeavyFilesForCL(DIAdapter2
								.getHeavyFilesForCL(cData));
					}
					// DeploymentInfo->languageLibs->no need, because are
					// handled from the app_libraries_container
					// DeploymentInfo->ModuleProvider->needed, because contains
					// local files
					dInfo4Serialization.setModuleProvider(null);
				}
				EditorUtil.setSerializedObject(appCfg, DIConsts2.serialized,
						dInfo4Serialization,
						"of deployment info of the application.");
			} else {
				ConfigUtils.deleteFile(appCfg, DIConsts2.serialized);
			}
		} catch (CloneNotSupportedException cnsex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_STORE_D_INFO_IN_DB, new String[] {
							dInfo.getApplicationName(), "serialization" },
					cnsex);
			sde.setMessageID("ASJ.dpl_ds.005048");
			throw sde;
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_STORE_D_INFO_IN_DB, new String[] {
							dInfo.getApplicationName(), "serialization" }, cex);
			sde.setMessageID("ASJ.dpl_ds.005048");
			sde.setDcNameForObjectCaller(appCfg);
			throw sde;
		}
	}

	private void internallyModifyDeploymentInfo(Configuration appCfg,
		Configuration deployCfg, DeploymentInfo dInfo)
		throws ConfigurationException, DeploymentException {
		
		// PRINT IT
		if (location.beDebug()) {
			DSLog.traceDebug(location, "{0}", CAConvertor.toString(dInfo, ""));
		}

		// delete old data
		EditorFactory.getInstance().getDIGC(dInfo.getVersion())
			.delete(appCfg, deployCfg);

		modifyAppConfig(appCfg, dInfo);
		modifyDeployCfg(deployCfg, dInfo);
		modifySerialized(appCfg, dInfo);
	}

	private void modifyDeployCfg(final Configuration deployCfg,
        final DeploymentInfo dInfo) throws ConfigurationException,
        DeploymentException {
	    // ********************DEPLOY********************//
	    if (deployCfg != null) {
	    	// version
	    	deployCfg.modifyConfigEntry(DIConsts2.version, 
	    		dInfo.getVersion().getName(), true);
	    	// startUp
	    	modifyStartUp(deployCfg, new Integer(dInfo.getStartUpO().getId()));
	    	// initiallyStarted
	    	modifyInitiallyStarted(deployCfg, dInfo.getInitiallyStarted(), 
	    		dInfo.getModuleProvider());
	    	modifyAppStatus(deployCfg, dInfo.getStatus());
	    } else {
	    	if (location.bePath()) {
	    		DSLog.tracePath(location, "The deploy configuration for [{0}] is NULL.",
	    				dInfo.getApplicationName());
	    	}
	    }
    }

	private void modifyAppConfig(final Configuration appCfg, 
		final DeploymentInfo dInfo) throws ConfigurationException, 
		ServerDeploymentException {
		// version
		appCfg.modifyConfigEntry(
			DIConsts2.version, dInfo.getVersion().getName(), true);
		// globalData
		modifyGlobalData(appCfg, dInfo);
		// references
		modifyReferences(appCfg, dInfo.getReferences());
		// FilesForCL, containerNames, optionalContainers,
		// deployedComponents,
		// deployedResources_Types, privateDeployedResources_Types,
		// deployedFileNames
		modifyContainerData(appCfg, dInfo);
		// remoteSupport
		modifyPropSheetWithOrder(appCfg, DIConsts2.remoteSupport, 
			CAConvertor.asSet(dInfo.getRemoteSupport()));
		// properties
		// modifyProperties(appCfg, dInfo.getProperties());
		modifyProperties(
			appCfg, dInfo.getProperties(), dInfo.isCustomJavaVersion());
		// application_xml
		if (dInfo.getApplicationXML() != null) {
			EditorUtil.setSerializedObject(appCfg,
				DIConsts2.application_xml, dInfo.getApplicationXML(),
				"of application xml stream of the application.");
		} else {
			if (appCfg.existsFile(DIConsts2.application_xml)) {
				appCfg.deleteFile(DIConsts2.application_xml);
			}
		}
		if (dInfo.getContainerInfoXML() != null) {
			EditorUtil.setSerializedObject(appCfg,
				DIConsts2.containers_info_xml, dInfo.getApplicationXML(),
				"of containers-info xml stream of the application.");
		} else {
			if (appCfg.existsFile(DIConsts2.containers_info_xml)) {
				appCfg.deleteFile(DIConsts2.containers_info_xml);
			}
		}
		// appcfg, SAP_MANIFEST -> stored in DeployUtilTransaction
    }

	private void modifyGlobalData(Configuration appCfg, DeploymentInfo dInfo)
		throws ConfigurationException {
		final PropertySheet ps = ConfigUtils.recreateSubConfiguration(
			appCfg, DIConsts2.globalData, 
			Configuration.CONFIG_TYPE_PROPERTYSHEET)
			.getPropertySheetInterface();
		// appName
		ConfigUtils.createPropertyEntry(
			ps, DIConsts2.appName, dInfo.getApplicationName());
		// isStandAlone
		ps.createPropertyEntry(
			DIConsts2.isStandAlone, 
			new Boolean(dInfo.isStandAloneArchive()).toString(), "boolean");
		// failover
		modifyFailOver(ps, dInfo.getFailOver());
		// additionalClasspath
		ConfigUtils.createPropertyEntry(ps, DIConsts2.additionalClasspath,
				dInfo.getAdditionalClasspath());
	}

	// The references, which getCharacteristic().isPersistent()==false will not
	// be
	// stored in DB -> writeReferenceObject(...)
	public void modifyReferences(Configuration appCfg, ReferenceObject[] refObjs)
			throws ConfigurationException, ServerDeploymentException {
		ValidateUtils.nullValidator(appCfg, "'apps' configuration");
		final Configuration refsCfg = ConfigUtils.recreateSubConfiguration(
				appCfg, DIConsts2.references,
				Configuration.CONFIG_TYPE_STANDARD);
		if (refObjs != null && refObjs.length > 0) {
			PropertySheet ps = null;
			for (int i = 0; i < refObjs.length; i++) {
				ps = refsCfg.createSubConfiguration(i + "",
						Configuration.CONFIG_TYPE_PROPERTYSHEET)
						.getPropertySheetInterface();
				writeReferenceObject(refsCfg.getPath(), ps, refObjs[i]);
			}
		} else {
			refsCfg.deleteConfiguration();
		}
		// update serialized
		ConfigUtils.deleteFile(appCfg, DIConsts2.serialized);
	}

	// The references, which getCharacteristic().isPersistent()==false will not
	// be
	// stored in DB.
	private void writeReferenceObject(String cfgPath, PropertySheet ps,
			ReferenceObject refObj) throws ConfigurationException {
		final ReferenceType rType = refObj.getCharacteristic();
		if (rType.isPersistent()) {
			ps.createPropertyEntry(DIConsts2.RO_TARGET_NAME, refObj
					.getReferenceTarget(), "");
			{// NOTE : the provide name may be null. is that correct?
				// (read/write)
				final String provider = refObj.getReferenceProviderName();
				if (provider != null) {
					ps.createPropertyEntry(DIConsts2.RO_TARGET_VENDOR,
							provider, "");
				}
			}
			ps.createPropertyEntry(DIConsts2.RO_TARGET_TYPE, refObj
					.getReferenceTargetType(), "");
			ps.createPropertyEntry(DIConsts2.RO_REFERENCE_TYPE, refObj
					.getReferenceType(), "");
			{// ReferenceType
				if (!rType.isClassloading()) {
					ps.createPropertyEntry(DIConsts2.RC_IS_CLASSLOADING, rType
							.isClassloading()
							+ "", "");
				}
				if (!rType.isFunctional()) {
					ps.createPropertyEntry(DIConsts2.RC_IS_FUNCTIONAL, rType
							.isFunctional()
							+ "", "");
				}
			}
		} else {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "The reference [{0}] won't be persisted in [{1}].",
						refObj.print(""),
						cfgPath);
			}
		}
	}

	private void modifyResourceReferences(Configuration cNameCfg, Set resRefs)
			throws ConfigurationException {
		ValidateUtils.nullValidator(cNameCfg, "'cNameCfg' configuration");
		final Configuration resRefsCfg = ConfigUtils.recreateSubConfiguration(
				cNameCfg, DIConsts2.resourceReferences,
				Configuration.CONFIG_TYPE_STANDARD);
		if (resRefs != null && resRefs.size() > 0) {
			PropertySheet ps = null;
			final Iterator rrIter = resRefs.iterator();
			int i = -1;
			while (rrIter.hasNext()) {
				ps = resRefsCfg.createSubConfiguration(++i + "",
						Configuration.CONFIG_TYPE_PROPERTYSHEET)
						.getPropertySheetInterface();
				writeResourceReference(ps, (ResourceReference) rrIter.next());
			}
		} else {
			resRefsCfg.deleteConfiguration();
		}
	}

	private void writeResourceReference(PropertySheet ps,
			ResourceReference resRef) throws ConfigurationException {
		ps.createPropertyEntry(DIConsts2.RR_TARGET_ID, resRef.getResRefName(),
				"");
		ps.createPropertyEntry(DIConsts2.RR_TARGET_TYPE,
				resRef.getResRefType(), "");
		ps.createPropertyEntry(DIConsts2.RR_REFERENCE_TYPE, resRef
				.getReferenceType(), "");
		{// ResourceReferenceType
			final ResourceReferenceType rrType = resRef.getType();
			if (!rrType.isClassloading()) {
				ps.createPropertyEntry(DIConsts2.RC_IS_CLASSLOADING, rrType
						.isClassloading()
						+ "", "");
			}
			if (!rrType.isFunctional()) {
				ps.createPropertyEntry(DIConsts2.RC_IS_FUNCTIONAL, rrType
						.isFunctional()
						+ "", "");
			}
		}
	}

	public void modifyFilesForCL(Configuration cNameCfg, ContainerData cData)
			throws ConfigurationException {
		final Set relative = DIAdapter2.getFilesForCL(cData);
		modifyPropSheetWithOrder(cNameCfg, DIConsts2.filesForCL, relative);
	}

	public void modifyHeavyFilesForCL(Configuration cNameCfg,
			ContainerData cData) throws ConfigurationException {
		final Set relative = DIAdapter2.getHeavyFilesForCL(cData);
		modifyPropSheetWithOrder(cNameCfg, DIConsts2.heavyFilesForCL, relative);
	}

	private void modifyFailOver(PropertySheet ps, FailOver failOver)
			throws ConfigurationException {
		ConfigUtils.updatePropertyEntry(ps, DIConsts2.failover, failOver
				.getKey());
	}

	private void modifyExceptionInfo(Configuration appCfg,
			Configuration deployCfg, ExceptionInfo exceptionInfo)
			throws ConfigurationException, ServerDeploymentException {
		ValidateUtils.nullValidator(deployCfg, "'deploy' configuration");
		final Configuration clElemIdCfg = ConfigUtils.recreateSubConfiguration(
				deployCfg, PropManager.getInstance().getClElemID() + "",
				Configuration.CONFIG_TYPE_STANDARD);
		if (exceptionInfo != null) {
			EditorUtil.setSerializedObject(clElemIdCfg,
					DIConsts2.encodedExceptionInfo, exceptionInfo.decode(),
					"on exception info.");
		} else if (clElemIdCfg
				.existsConfigEntry(DIConsts2.encodedExceptionInfo)) {
			clElemIdCfg.deleteConfigEntry(DIConsts2.encodedExceptionInfo);
		}
	}

	private void modifyStartUp(final Configuration deployCfg,
		final Integer startUp) throws ConfigurationException {
		assert deployCfg != null : "'deploy' configuration is null";
		assert startUp != null : "start up is null";
		deployCfg.modifyConfigEntry(DIConsts2.startUp, 
			AdditionalAppInfo.getStartUpString(startUp.intValue()), true);
	}

	private void modifyAppStatus(final Configuration deployCfg, 
		final Status status) throws ConfigurationException {
        if(!deployCfg.existsConfigEntry(DIConsts2.status)) {
        	deployCfg.addConfigEntry(DIConsts2.status, status.getName());
        } else {
        	deployCfg.modifyConfigEntry(DIConsts2.status, status.getName());
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

	private void modifyContainerData(Configuration appCfg, DeploymentInfo dInfo)
			throws ConfigurationException, ServerDeploymentException {
		// recreate : to delete the old data
		ValidateUtils.missingDCinDIValidator(dInfo, "modify",
				new ContainerInterface[0]);
		final Configuration contData = ConfigUtils.recreateSubConfiguration(
				appCfg, DIConsts2.containerData,
				Configuration.CONFIG_TYPE_STANDARD);
		final Hashtable cName_cData = dInfo.getCNameAndCData();
		final Enumeration cnEnum = cName_cData.keys();
		String cName;
		ContainerData cData;
		while (cnEnum.hasMoreElements()) {
			cName = (String) cnEnum.nextElement();
			cData = (ContainerData) cName_cData.get(cName);
			modifyContainerData(contData, cData);
		}
	}

	private void modifyContainerData(Configuration contData, ContainerData cData)
			throws ConfigurationException {
		final String cName = cData.getContName();
		// recreate : to delete the old data
		final Configuration cNameCfg = ConfigUtils.recreateSubConfiguration(
				contData, cName, Configuration.CONFIG_TYPE_STANDARD);

		{// isOptional
			cNameCfg.modifyConfigEntry(DIConsts2.isOptional, new Boolean(cData
					.isOptional()), true);
		}
		{// filesForCL
			modifyFilesForCL(cNameCfg, cData);
		}
		{// heavyFilesForCL
			modifyHeavyFilesForCL(cNameCfg, cData);
		}
		{// deployedComponents
			modifyProvidedResources(cNameCfg, cData.getProvidedResources());
		}
		{// deployedFileNames
			modifyPropSheetWithOrder(cNameCfg, DIConsts2.deployedFileNames,
					cData.getDeployedFileNames());
		}
		{// resourceReferences
			modifyResourceReferences(cNameCfg, cData.getResourceReferences());
		}
	}

	// WARNING: the set is ordered one and the order will be kept.
	// The order starts from 1.
	private void modifyPropSheetWithOrder(Configuration cNameCfg,
			String psName, Set fromStrings) throws ConfigurationException {
		if (fromStrings == null || fromStrings.size() == 0) {
			ConfigUtils.deleteSubConfiguration(cNameCfg, psName);
			return;
		}
		ValidateUtils.nullValidator(cNameCfg, "confgiuration");
		// recreate : to delete the old data
		final Configuration psCfg = ConfigUtils.recreateSubConfiguration(
				cNameCfg, psName, Configuration.CONFIG_TYPE_PROPERTYSHEET);
		final PropertySheet ps = psCfg.getPropertySheetInterface();
		String propName = null, defaultValue = "", description = "";

		final Iterator strIter = fromStrings.iterator();
		int index = 0;
		while (strIter.hasNext()) {
			propName = (new Integer(++index)).toString();
			defaultValue = (String) strIter.next();
			ps.createPropertyEntry(propName, defaultValue, description);
		}
	}

	private void modifyProvidedResources(Configuration cNameCfg,
		Set<Resource> providedResources) throws ConfigurationException {
		if (providedResources == null || providedResources.size() == 0) {
			throw new IllegalStateException(
					"ASJ.dpl_ds.006053 Each deployed application must have at least one deployed component.");
		}
		// recreate : to delete the old data
		final Configuration dcParent = ConfigUtils.recreateSubConfiguration(
				cNameCfg, DIConsts2.deployedComponents,
				Configuration.CONFIG_TYPE_STANDARD);

		Map<String, Set<String>> resourceNamesTypes = new HashMap<String, Set<String>>(
				providedResources.size());
		Map<String, Resource.AccessType> resourceNamesAccessTypes = new HashMap<String, Resource.AccessType>(
				providedResources.size());
		for (Resource r : providedResources) {
			Set<String> types = resourceNamesTypes.get(r.getName());
			if (types == null) {
				types = new HashSet<String>();
				resourceNamesTypes.put(r.getName(), types);
			}
			types.add(r.getType());
			resourceNamesAccessTypes.put(r.getName(), r.getAccessType());
		}

		Configuration dcCfg = null;
		int i = -1;
		for (String rName : resourceNamesTypes.keySet()) {
			dcCfg = dcParent.createSubConfiguration(++i + "");
			dcCfg.modifyConfigEntry(DIConsts2.compName, rName, true);
			dcCfg.modifyConfigEntry(DIConsts2.accessModifier,
					resourceNamesAccessTypes.get(rName).name(), true);
			modifyPropSheetWithOrder(dcCfg, DIConsts2.resTypes,
					resourceNamesTypes.get(rName));
		}
	}

	private void modifyInitiallyStarted(final Configuration deployCfg, 
		final InitiallyStarted initiallyStarted,
		final ModuleProvider moduleProvider) throws DeploymentException {
		ValidateUtils.nullValidator(deployCfg, "'deploy' configuration");
		ValidateUtils.nullValidator(initiallyStarted, "InitiallyStarted");

		try {
			deployCfg.modifyConfigEntry(DIConsts2.initiallyStarted,
				initiallyStarted.getName(), true);
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "persisting the initially started info in configuration entry"
							+ deployCfg
							+ "/"
							+ DIConsts2.initiallyStarted
							+ " ." }, cex);
			sde.setMessageID("ASJ.dpl_ds.005082");
			sde.setDcNameForObjectCaller(deployCfg);
			throw sde;
		}

		try {
			if (moduleProvider != null) {
				final ModuleInfo mInfos[] = moduleProvider.getModuleInfo();
				if (mInfos != null && mInfos.length > 0) {
					final Configuration mpCfg = ConfigUtils
							.recreateSubConfiguration(deployCfg,
									DIConsts2.moduleProvider,
									Configuration.CONFIG_TYPE_STANDARD);
					ModuleInfo mInfo = null;
					FileInfo fInfos[] = null, fInfo = null;
					Configuration miCfg = null, fisCfg = null, fiCfg;
					File file = null;
					for (int i = 0; i < mInfos.length; i++) {
						mInfo = mInfos[i];
						miCfg = mpCfg.createSubConfiguration(i + "");
						miCfg.modifyConfigEntry(DIConsts2.moduleUri, mInfo
								.getModuleUri(), true);
						miCfg.modifyConfigEntry(DIConsts2.j2eeModuleType, mInfo
								.getJ2eeModuleType().name(), true);
						fisCfg = miCfg
								.createSubConfiguration(DIConsts2.fileInfos);

						fInfos = mInfo.getFileInfos();
						if (fInfos != null) {
							for (int k = 0; k < fInfos.length; k++) {
								fInfo = fInfos[k];
								fiCfg = fisCfg.createSubConfiguration(k + "");
								file = new File(fInfo.getFilePath());
								fiCfg.modifyConfigEntry(DIConsts2.fileType,
										fInfo.getFileType().getName(), true);
								fiCfg.modifyConfigEntry(DIConsts2.fileUri,
										fInfo.getFileUri(), true);
								fiCfg.updateFileByKey(file.getName(), file,
										true);
							}
						}
					}
				}
			} else {
				ConfigUtils.deleteSubConfiguration(deployCfg,
						DIConsts2.moduleProvider);
			}
		} catch (ConfigurationException cex) {
			throw new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "persisting the module provider info in configuration"
							+ deployCfg + "/" + DIConsts2.moduleProvider + " ." },
					cex);
		}
	}

	/**
	 * Method modifies the properties of a deployment info. It writes the java
	 * version in configuration only if it is a custom value.
	 * 
	 * @since 01.02.2006
	 */
	private void modifyProperties(Configuration appCfg, Properties properties,
			boolean isCustomJavaVersionSet) throws ConfigurationException {
		if (properties == null) {
			return;
		}
		ValidateUtils.nullValidator(appCfg, "'apps' configuration");
		// exclude the auto generated java version
		if (!isCustomJavaVersionSet) {
			// exclude the auto generated java version
			Properties copyProps = (Properties) properties.clone();
			copyProps.remove(IOpConstants.JAVA_VERSION);
			final PropertySheet ps = ConfigUtils.recreateSubConfiguration(
					appCfg, DIConsts2.properties,
					Configuration.CONFIG_TYPE_PROPERTYSHEET)
					.getPropertySheetInterface();
			ps.createPropertyEntries(copyProps);
		} else {
			final PropertySheet ps = ConfigUtils.recreateSubConfiguration(
					appCfg, DIConsts2.properties,
					Configuration.CONFIG_TYPE_PROPERTYSHEET)
					.getPropertySheetInterface();
			ps.createPropertyEntries(properties);
		}
	}
}
