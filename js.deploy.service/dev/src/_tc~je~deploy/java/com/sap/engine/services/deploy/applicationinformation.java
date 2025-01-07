/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy;

import java.io.Serializable;
import java.util.Set;

import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.utils.StringUtils;

/*This class belongs to the public API of the DeployService project. */
/**
 * Class holding application information. It stores information about
 * application name, provider name, SAP_MANIFEST.MF file, class loader name,
 * configuration path. It represents either an application or a standalone
 * module.
 * 
 * 
 * @author Anton Georgiev
 */
public class ApplicationInformation implements Serializable {

	static final long serialVersionUID = -8996638989513856038L;

	private final String originalAppName;
	private final ApplicationName applicationName;
	private final String loaderName;
	private final boolean isStandAloneModul;
	private final String[] containerNames;
	private final String[] aliases;
	private final ReferenceObject[] references;
	private final ReferenceObject[] dependants;
	private final ConfigProvider configProvider;
	private final String sJavaVersion;

	/**
	 * Constructs <code>ApplicationInformation</code> object based on
	 * <code>DeploymentInfo</code>
	 * 
	 * @param dInfo
	 *            the DeploymentInfo object
	 */
	public ApplicationInformation(DeploymentInfo dInfo) {
		this.applicationName = dInfo.getApplicationNameO();
		this.originalAppName = null;
		this.loaderName = dInfo.getApplicationName();
		this.isStandAloneModul = dInfo.isStandAloneArchive();
		this.containerNames = dInfo.getContainerNames();
		this.aliases = DUtils.getAliases(dInfo);
		this.references = dInfo.getReferences();
		dependants = getDependants(dInfo);
		this.configProvider = dInfo.getConfigProvider();
		this.sJavaVersion = dInfo.getJavaVersion();
	}

	private ReferenceObject[] getDependants(DeploymentInfo info) {
		Set<Edge<Component>> refFrom = Applications.getReferenceGraph()
			.getReferencesFromOthersTo(Component.create(info.getApplicationName()));
		ReferenceObject[] result = new ReferenceObject[refFrom.size()];
		int counter = 0;
		for (Edge<Component> edge : refFrom) {
			ReferenceObject refObject = new ReferenceObject();
			parseApplicationName(refObject, edge.getFirst().getName());
			refObject
					.setReferenceTargetType(ReferenceObjectIntf.REF_TARGET_TYPE_APPLICATION);
			refObject
					.setReferenceType(Edge.Type.WEAK.equals(edge.getType()) ? ReferenceObjectIntf.REF_TYPE_WEAK
							: ReferenceObjectIntf.REF_TYPE_HARD);
			result[counter++] = refObject;
		}

		return result;
	}

	private static void parseApplicationName(ReferenceObject ref, String appName) {
		final int index = appName
				.indexOf(DeployConstants.DELIMITER_4_PROVIDER_AND_NAME);
		if (index != -1) {
			if (index == 0) {
				ref
						.setReferenceProviderName(DeployConstants.DEFAULT_PROVIDER_4_APPS_SAP_COM);
			} else {
				ref.setReferenceProviderName(StringUtils.intern(appName
						.substring(0, index)));
			}
			ref.setReferenceTarget(appName.substring(index + 1));
		} else {
			ref
					.setReferenceProviderName(DeployConstants.DEFAULT_PROVIDER_4_APPS_SAP_COM);
			ref.setReferenceTarget(appName);
		}
	}

	/**
	 * Returns the original application name.
	 * 
	 * @return the original name of the application.
	 */
	public String getOriginalAppName() {
		return originalAppName;
	}

	/**
	 * Returns the name of the provider of this application.
	 * 
	 * @return provider name of the application.
	 * @deprecated use getApplicationName() instead.
	 */
	@Deprecated
	public String getProviderName() {
		return applicationName.getProvider();
	}

	/**
	 * Provides information about the provider name and application name.
	 * 
	 * @return the applicationName.
	 */
	public ApplicationName getApplicationName() {
		return applicationName;
	}

	/**
	 * Returns the name of the class loader for that application.
	 * 
	 * @return class loader name.
	 */
	public String getLoaderName() {
		return loaderName;
	}

	/**
	 * Checks if the object represents a standalone module.
	 * 
	 * @return <li><code>true</code> - in case of a standalone module <li>
	 *         <code>false</code> - in case of an application
	 */
	public boolean isStandAloneModule() {
		return isStandAloneModul;
	}

	/**
	 * Returns the containers, on which this application is deployed.
	 * 
	 * @return the containers, on which this application is deployed.
	 */
	public String[] getContainerNames() {
		return containerNames;
	}

	/**
	 * Gets the aliases of this application.
	 * 
	 * @return <code>String</code>[] with aliases
	 */
	public String[] getAliases() {
		return this.aliases;
	}

	/**
	 * Gets the references of this application.
	 * 
	 * @return <code>ReferenceObject</code>[]
	 */
	public ReferenceObject[] getReferences() {
		return this.references;
	}

	/**
	 * Gets the dependents that have reference to this application.
	 * 
	 * @return applications that are successors of this application
	 */
	public ReferenceObject[] getDependants() {
		return this.dependants;
	}

	/**
	 * Returns the configuration path to the SAP_MANIFEST.MF file of the
	 * application.
	 * 
	 * @return the configuration path to the SAP_MANIFEST.
	 * @deprecated use getConfigProvider() -> getSapManifestCfg4CustomGlobal()
	 */
	@Deprecated
	public String getSapManifestCfgPath() {
		return (configProvider != null ? configProvider
				.getSapManifestCfg4CustomGlobal() : null);
	}

	/**
	 * Returns the configuration path of this application.
	 * 
	 * @return the configuration path to the application.
	 * @deprecated use getConfigProvider() ->
	 *             getAppGlobalPropsCfg4CustomGlobal()
	 */
	@Deprecated
	public String getApplicationConfigurationPath() {
		return (configProvider != null ? configProvider
				.getAppGlobalPropsCfg4CustomGlobal() : null);
	}

	/**
	 * Gets the <code>ConfigProvider</code> for this application. It contains
	 * information about the configurations used from this application that are
	 * for public use.
	 * 
	 * @return the configProvider.
	 */
	public ConfigProvider getConfigProvider() {
		return configProvider;
	}

	/**
	 * Gets the Java version for this application.
	 * 
	 * @return <code>String</code> representing Java version
	 */
	public String getJavaVersion() {
		return sJavaVersion;
	}

}
