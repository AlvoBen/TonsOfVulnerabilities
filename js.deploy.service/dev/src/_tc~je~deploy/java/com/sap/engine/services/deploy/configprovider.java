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

import com.sap.engine.services.deploy.container.util.PrintIt;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.editor.impl.second.DIConsts2;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.DSConstants;

/* This class belongs to the public API of the DeployService project. */
/**
 * This class provides methods for getting persistent application configuration
 * and SAP_MANIFEST.MF at instance as well as at custom global level.
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class ConfigProvider implements Serializable, PrintIt {

	private static final long serialVersionUID = 8733112739298887273L;

	private final ApplicationName appNameO;

	private String appGlobalPropsCfg4CustomGlobal = null;
	private String appGlobalPropsCfg4CurrentInstance = null;

	private String sapManifestCfg4CustomGlobal = null;
	private String sapManifestCfg4CurrentInstance = null;

	/**
	 * Constructs a ConfigProvider object.
	 * 
	 * @param appNameO
	 *            name of the application
	 */
	public ConfigProvider(ApplicationName appNameO) {
		this.appNameO = appNameO;
	}

	/**
	 * Gets the configuration, where the application properties are persisted,
	 * at instance level.
	 * 
	 * @return a string with the configuration path at instance level or null if
	 *         there are no application properties for this application.
	 */
	public String getAppGlobalPropsCfg4CurrentInstance() {
		return appGlobalPropsCfg4CurrentInstance;
	}

	private void setAppGlobalPropsCfg4CurrentInstance(
			String appGlobalPropsCfg4CurrentInstance) {
		this.appGlobalPropsCfg4CurrentInstance = appGlobalPropsCfg4CurrentInstance;
	}

	/**
	 * Gets the configuration, where the application properties are persisted,
	 * at custom global level.
	 * 
	 * @return a string with the configuration path at custom global level or
	 *         null if there are no application properties for this application.
	 */
	public String getAppGlobalPropsCfg4CustomGlobal() {
		return appGlobalPropsCfg4CustomGlobal;
	}

	private void setAppGlobalPropsCfg4CustomGlobal(
			String appGlobalPropsCfg4CustomGlobal) {
		this.appGlobalPropsCfg4CustomGlobal = appGlobalPropsCfg4CustomGlobal;
	}

	/**
	 * Sets the existence of the application properties for this application.
	 * 
	 * @param isExist
	 *            <code>true</code> if there are application properties for this
	 *            application, otherwise <code>false</code>.
	 */
	public void setAppGlobalPropsCfg(boolean isExist) {
		if (isExist) {
			setAppGlobalPropsCfg4CurrentInstance(DeployConstants.CURRENT_INSTANCE_CONFIG
					+ "/"
					+ appNameO.getApplicationName()
					+ "/"
					+ DIConsts2.appcfg);
			setAppGlobalPropsCfg4CustomGlobal(DeployConstants.CUSTOM_GLOBAL_CONFIG
					+ "/"
					+ appNameO.getApplicationName()
					+ "/"
					+ DIConsts2.appcfg);
		} else {
			setAppGlobalPropsCfg4CurrentInstance(null);
			setAppGlobalPropsCfg4CustomGlobal(null);
		}
	}

	/**
	 * Gets the configuration, where the SAP_MANIFEST.MF is persisted, at
	 * instance level.
	 * 
	 * @return a string with the configuration path at instance level or null if
	 *         there is no SAP_MANIFEST.MF for this application.
	 */
	public String getSapManifestCfg4CurrentInstance() {
		return PropManager.getInstance().isSapManifestReadable() ? sapManifestCfg4CurrentInstance
				: null;
	}

	private void setSapManifestCfg4CurrentInstance(
			String sapManifestCfg4CurrentInstance) {
		this.sapManifestCfg4CurrentInstance = sapManifestCfg4CurrentInstance;
	}

	/**
	 * Gets the configuration, where the SAP_MANIFEST.MF is persisted, at custom
	 * global level.
	 * 
	 * @return a string with the configuration path at custom global level or
	 *         null if there is no SAP_MANIFEST.MF for this application.
	 * @deprecated to be deleted
	 */
	@Deprecated
    public String getSapManifestCfg4CustomGlobal() {
		return PropManager.getInstance().isSapManifestReadable() ? sapManifestCfg4CustomGlobal
				: null;
	}

	private void setSapManifestCfg4CustomGlobal(
			String sapManifestCfg4CustomGlobal) {
		this.sapManifestCfg4CustomGlobal = sapManifestCfg4CustomGlobal;
	}

	/**
	 * Sets the existence of SAP_MANIFEST.MF for this application.
	 * 
	 * @param isExist
	 *            <code>true</code> if there is SAP_MANIFEST.MF for this
	 *            application, otherwise <code>false</code>.
	 * @deprecated to be deleted
	 */
	@Deprecated
    public void setSapManifest(boolean isExist) {
		if (isExist) {
			setSapManifestCfg4CurrentInstance(DeployConstants.CURRENT_INSTANCE_CONFIG
					+ "/"
					+ appNameO.getApplicationName()
					+ "/"
					+ DIConsts2.appcfg + "/" + DIConsts2.SAP_MANIFEST);
			setSapManifestCfg4CustomGlobal(DeployConstants.CUSTOM_GLOBAL_CONFIG
					+ "/" + appNameO.getApplicationName() + "/"
					+ DIConsts2.appcfg + "/" + DIConsts2.SAP_MANIFEST);
		} else {
			setSapManifestCfg4CurrentInstance(null);
			setSapManifestCfg4CustomGlobal(null);
		}
	}

	/**
	 * Gives the name of the application.
	 * 
	 * @return name of the application
	 */
	public ApplicationName getAppNameO() {
		return appNameO;
	}

	@Override
    public String toString() {
		return print("");
	}

	@Override
    public int hashCode() {
		return getAppNameO().hashCode();
	}

	@Override
    public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final ConfigProvider other = (ConfigProvider) obj;

		if (!getAppNameO().equals(other.getAppNameO())) {
			return false;
		}

		return true;
	}

	@Override
    public Object clone() throws CloneNotSupportedException {
		final ConfigProvider cfgProvider = new ConfigProvider(getAppNameO());
		cfgProvider
				.setAppGlobalPropsCfg4CurrentInstance(getAppGlobalPropsCfg4CurrentInstance());
		cfgProvider
				.setAppGlobalPropsCfg4CustomGlobal(getAppGlobalPropsCfg4CustomGlobal());
		cfgProvider
				.setSapManifestCfg4CurrentInstance(getSapManifestCfg4CurrentInstance());
		cfgProvider
				.setSapManifestCfg4CustomGlobal(getSapManifestCfg4CustomGlobal());
		return cfgProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.op.util.PrintIt#print(java.lang
	 * .String)
	 */
	/**
	 * Returns string representation of the object shifted with the given
	 * <code>String<code>.
	 */
	public String print(String shift) {
		final StringBuffer sb = new StringBuffer(DSConstants.EOL);

		sb.append(shift + "   AppGlobalPropsCfg4CurrentInstance = "
				+ getAppGlobalPropsCfg4CurrentInstance() + DSConstants.EOL);
		sb.append(shift + "   AppGlobalPropsCfg4CustomGlobal = "
				+ getAppGlobalPropsCfg4CustomGlobal() + DSConstants.EOL);
		sb.append(shift + "   SapManifestCfg4CurrentInstance = "
				+ getSapManifestCfg4CurrentInstance() + DSConstants.EOL);
		sb.append(shift + "   SapManifestCfg4CustomGlobal = "
				+ getSapManifestCfg4CustomGlobal() + DSConstants.EOL);

		return sb.toString();
	}

}
