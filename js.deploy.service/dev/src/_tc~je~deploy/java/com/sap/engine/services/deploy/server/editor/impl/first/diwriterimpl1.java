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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.InvalidValueException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.NoWriteAccessException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.op.IOpConstants;
import com.sap.engine.services.deploy.container.op.util.FailOver;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.Version;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.editor.impl.EditorUtil;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.tc.logging.Location;

/**
 * For Version.FIRST
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DIWriterImpl1 implements DIWriter {
	
	private static final Location location = 
		Location.getLocation(DIWriterImpl1.class);

	private final Version version = Version.FIRST;

	public void modifyDeploymentInfo(Configuration appCfg,
		Configuration deployCfg, DeploymentInfo dInfo)
		throws DeploymentException {

		ValidateUtils.nullValidator(appCfg, "'apps' configuration");
		// deployCfg may be null
		ValidateUtils.nullValidator(dInfo, "deployment info");

		try {
			internallyModifyDeploymentInfo(appCfg, deployCfg, dInfo);
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_STORE_D_INFO_IN_DB, new String[] {
							dInfo.getApplicationName(), "" }, cex);
			sde.setMessageID("ASJ.dpl_ds.005048");
			sde.setDcNameForObjectCaller(deployCfg);
			throw sde;
		}
	}

	public void modifySerialized(Configuration appCfg, DeploymentInfo dInfo)
		throws DeploymentException {
		// do nothing
	}

	private void internallyModifyDeploymentInfo(final Configuration appCfg,
		final Configuration deployCfg, final DeploymentInfo dInfo)
		throws ConfigurationException, DeploymentException {
		
		// PRINT IT
		if (location.beDebug()) {
			DSLog.traceDebugObject(location, "{0}", dInfo);
		}

		// delete old data
		EditorFactory.getInstance().getDIGC(dInfo.getVersion()).delete(
			appCfg, deployCfg);
		modifyAppConfig(appCfg, dInfo);
		modifyDplConfig(deployCfg, dInfo);
	}

	private void modifyDplConfig(final Configuration deployCfg, 
		final DeploymentInfo dInfo) throws ConfigurationException {
	    if (deployCfg == null) {
	    	if (location.bePath()) {
	    		DSLog.tracePath(location, 
	    			"The deploy configuration for [{0}] is NULL.",
	    			dInfo.getApplicationName());
	    	}
	    	return;
	    }
    	modifyStartUp(deployCfg, new Integer(dInfo.getStartUp()));
	    modifyAppStatus(deployCfg, dInfo.getStatus());
    }

	private void modifyAppConfig(final Configuration appCfg,
        final DeploymentInfo dInfo) throws ConfigurationException,
        NameNotFoundException, NoWriteAccessException, InvalidValueException,
        InconsistentReadException, ServerDeploymentException {
	    // ********************APPS********************//
			// application name
			appCfg.modifyConfigEntry(DIConsts1.appName, dInfo
					.getApplicationName(), true);
			// containerNames
			if (dInfo.getContainerNames() != null) {
				appCfg.modifyConfigEntry(DIConsts1.containerNames, dInfo
						.getContainerNames(), true);
			} else if (appCfg.existsConfigEntry(DIConsts1.containerNames)) {
				appCfg.deleteConfigEntry(DIConsts1.containerNames);
			}
			// isStandAlone
			appCfg.modifyConfigEntry(DIConsts1.isStandAlone, new Boolean(dInfo
					.isStandAloneArchive()), true);
			// References
			modifyReferences(appCfg, dInfo.getReferences());
			// RemoteSupport
			if (dInfo.getRemoteSupport() != null) {
				appCfg.modifyConfigEntry(DIConsts1.remoteSupport, dInfo
						.getRemoteSupport(), true);
			} else if (appCfg.existsConfigEntry(DIConsts1.remoteSupport)) {
				appCfg.deleteConfigEntry(DIConsts1.remoteSupport);
			}
			// Properties
			// modifyProperties(appCfg, dInfo.getProperties());
			modifyProperties(appCfg, dInfo.getProperties(), dInfo
					.isCustomJavaVersion());
			// application.xml - deployment descriptor
			if (dInfo.getApplicationXML() != null) {
				EditorUtil.setSerializedObject(appCfg, DIConsts1.descriptor,
						dInfo.getApplicationXML(),
						"of application xml stream of the application.");
			} else {
				if (appCfg.existsFile(DIConsts1.descriptor)) {
					appCfg.deleteFile(DIConsts1.descriptor);
				}
			}
			// FilesForCL
			modifyFilesForCL(appCfg, dInfo);
			// optionalContainers
			final String[] optionalContainers = dInfo.getOptionalContainers(); 
			if (optionalContainers != null) {
				appCfg.modifyConfigEntry(DIConsts1.optionalContainers, 
					optionalContainers, true);
			} else if (appCfg.existsConfigEntry(DIConsts1.optionalContainers)) {
				appCfg.deleteConfigEntry(DIConsts1.optionalContainers);
			}

			modifyProvidedResources(appCfg, dInfo);
			// deployedFileNames
			if (dInfo.getDeployedFileNames() != null) {
				EditorUtil.setSerializedObject(appCfg,
						DIConsts1.deployedFileNames, dInfo
								.getDeployedFileNames(),
						"of deployed file names.");
			} else if (appCfg.existsFile(DIConsts1.deployedFileNames)) {
				appCfg.deleteFile(DIConsts1.deployedFileNames);
			}

			if (appCfg.existsConfigEntry(DIConsts1.loaderName)) {
				// loaderName is not supported anymore.
				appCfg.deleteConfigEntry(DIConsts1.loaderName);
			}
			// additionalClasspath
			if (dInfo.getAdditionalClasspath() != null) {
				appCfg.modifyConfigEntry(DIConsts1.additionalClasspath, dInfo
						.getAdditionalClasspath(), true);
			} else if (appCfg.existsConfigEntry(DIConsts1.additionalClasspath)) {
				appCfg.deleteConfigEntry(DIConsts1.additionalClasspath);
			}
			// encodeResources
			String encodedResources[] = DIAdapter1.decode(dInfo
					.getResourceReferences());
			if (encodedResources != null) {
				EditorUtil.setSerializedObject(appCfg,
						DIConsts1.encodeResources, encodedResources,
						"of encodedResources.");
			} else if (appCfg.existsFile(DIConsts1.encodeResources)) {
				appCfg.deleteFile(DIConsts1.encodeResources);
			}
			// FailOverValue
			modifyFailOver(appCfg, dInfo.getFailOver());
    }

	private Set<String> getValueSet(Map<String, Set<String>> fromMap,
			String underKey) {
		Set<String> set = fromMap.get(underKey);
		if (set == null) {
			set = new HashSet<String>();
			fromMap.put(underKey, set);
		}
		return set;
	}

	private Map<String, String[]> convertValueFromSetToArray(
			Map<String, Set<String>> source) {
		final Map<String, String[]> result = new HashMap<String, String[]>(
				source.size());
		for (Map.Entry<String, Set<String>> entry : source.entrySet()) {
			String[] value = new String[entry.getValue().size()];
			entry.getValue().toArray(value);
			result.put(entry.getKey(), value);
		}
		return result;
	}

	private void modifyProvidedResources(Configuration appCfg,
			DeploymentInfo dInfo) throws ServerDeploymentException,
			ConfigurationException, InconsistentReadException,
			NameNotFoundException, NoWriteAccessException {
		Hashtable<String, Set<String>> containerToComponentNames = new Hashtable<String, Set<String>>(
				dInfo.getCNameAndCData().size());
		Hashtable<String, Set<String>> deployedComponentTypes = new Hashtable<String, Set<String>>();
		Hashtable<String, Set<String>> privateDeployedComponentTypes = new Hashtable<String, Set<String>>();

		for (ContainerData cData : dInfo.getCNameAndCData().values()) {
			for (Resource r : cData.getProvidedResources()) {
				getValueSet(containerToComponentNames, cData.getContName())
						.add(r.getName());
				switch (r.getAccessType()) {
				case PUBLIC:
					getValueSet(deployedComponentTypes, r.getName()).add(
							r.getType());
					break;
				case PRIVATE:
					getValueSet(privateDeployedComponentTypes, r.getName())
							.add(r.getType());
					break;
				}
			}
		}

		// deployedComponents
		if (containerToComponentNames.size() > 0) {
			EditorUtil.setSerializedObject(appCfg,
					DIConsts1.deployedComponents,
					convertValueFromSetToArray(containerToComponentNames),
					"of deployed components.");
		} else if (appCfg.existsFile(DIConsts1.deployedComponents)) {
			appCfg.deleteFile(DIConsts1.deployedComponents);
		}
		// deployedResources_Types
		if (deployedComponentTypes.size() > 0) {
			EditorUtil.setSerializedObject(appCfg,
					DIConsts1.deployedResources_Types,
					convertValueFromSetToArray(deployedComponentTypes),
					"the types of deployed resources.");
		} else if (appCfg.existsFile(DIConsts1.deployedResources_Types)) {
			appCfg.deleteFile(DIConsts1.deployedResources_Types);
		}
		// privateDeployedResources_Types
		if (privateDeployedComponentTypes.size() > 0) {
			EditorUtil.setSerializedObject(appCfg,
					DIConsts1.privateDeployedResources_Types,
					convertValueFromSetToArray(privateDeployedComponentTypes),
					"the types of the private deployed resources.");
		} else if (appCfg.existsFile(DIConsts1.privateDeployedResources_Types)) {
			appCfg.deleteFile(DIConsts1.privateDeployedResources_Types);
		}
	}

	// The references, which getCharacteristic().isPersistent()==false will not
	// be stored in DB -> DIAdapter1.decodeReferenceObjectArray(...)
	public void modifyReferences(Configuration appCfg, ReferenceObject[] refObjs)
			throws ConfigurationException, ServerDeploymentException {
		ValidateUtils.nullValidator(appCfg, "'apps' configuration");
		if (refObjs != null) {
			final String fileName = DIConsts1.references;
			EditorUtil.setSerializedObject(appCfg, fileName, DIAdapter1
					.decodeReferenceObjectArray(appCfg.getPath() + "/"
							+ fileName, refObjs), "of references.");
		} else if (appCfg.existsFile(DIConsts1.references)) {
			appCfg.deleteFile(DIConsts1.references);
		}
	}

	private void modifyFilesForCL(Configuration appCfg, DeploymentInfo dInfo)
			throws ConfigurationException, ServerDeploymentException {
		ValidateUtils.nullValidator(appCfg, "'apps' configuration");
		ValidateUtils.nullValidator(dInfo, "deployment info");

		final String filesForCL[] = DIAdapter1.getFilesForCL(dInfo);
		if (filesForCL != null) {
			EditorUtil.setSerializedObject(appCfg, DIConsts1.containerCLFiles,
					filesForCL, "of class loader files of container.");
		} else if (appCfg.existsFile(DIConsts1.containerCLFiles)) {
			appCfg.deleteFile(DIConsts1.containerCLFiles);
		}
	}

	private void modifyFailOver(Configuration appCfg, FailOver failOver)
			throws ConfigurationException {
		ValidateUtils.nullValidator(appCfg, "'apps' configuration");
		ValidateUtils.nullValidator(failOver, "fail over");
		appCfg.modifyConfigEntry(DIConsts1.failover, failOver.getName(), true);
	}

	private void modifyStartUp(final Configuration deployCfg,
		final Integer startUp) throws ConfigurationException {
		ValidateUtils.nullValidator(deployCfg, "'deploy' configuration");
		ValidateUtils.nullValidator(startUp, "start up");
		deployCfg.modifyConfigEntry(DIConsts1.startUp, startUp, true);
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

	private void modifyAppStatus(final Configuration deployCfg, 
		final Status status) throws ConfigurationException {
        if(!deployCfg.existsConfigEntry(DIConsts1.status)) {
        	deployCfg.addConfigEntry(DIConsts1.status, status.getName());
        } else {
        	deployCfg.modifyConfigEntry(DIConsts1.status, status.getName());
        }
	}

	/**
	 * Method modifies the properties of a deployment info. It writes the java
	 * version in configuration only if it is a custom value.
	 * 
	 * @since 01.02.2006
	 */
	private void modifyProperties(Configuration deployCfg,
			Properties properties, boolean isCustomJavaVersionSet)
			throws ConfigurationException {
		if (properties == null) {
			return;
		}
		ValidateUtils.nullValidator(deployCfg, "'deploy' configuration");

		// make a property sheet for application properties in application's
		// configuration
		if (deployCfg.existsSubConfiguration(DIConsts1.properties)) {
			deployCfg.deleteConfiguration(DIConsts1.properties);
		}
		PropertySheet prSheet = deployCfg.createSubConfiguration(
				DIConsts1.properties, Configuration.CONFIG_TYPE_PROPERTYSHEET)
				.getPropertySheetInterface();
		if (deployCfg.existsConfigEntry(DIConsts1.appProperties)) {
			deployCfg.deleteConfigEntry(DIConsts1.appProperties);
		}
		// exclude the auto generated java version
		if (!isCustomJavaVersionSet) {
			Properties copyProps = (Properties) properties.clone();
			copyProps.remove(IOpConstants.JAVA_VERSION);
			prSheet.createPropertyEntries(copyProps);
		} else {
			prSheet.createPropertyEntries(properties);
		}
	}
}
