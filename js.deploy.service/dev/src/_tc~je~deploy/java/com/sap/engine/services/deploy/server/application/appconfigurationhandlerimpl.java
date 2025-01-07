package com.sap.engine.services.deploy.server.application;

import java.util.Properties;

import com.sap.engine.frame.core.configuration.ConfigMetaData;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.CustomParameterMappings;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.dummyimpl.ConfigurationHandlerDummyImpl;
import com.sap.engine.services.deploy.container.AppConfigurationHandler;

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

public class AppConfigurationHandlerImpl extends ConfigurationHandlerDummyImpl
		implements AppConfigurationHandler {
	private final ConfigurationHandler handler;

	public AppConfigurationHandlerImpl(ConfigurationHandler handler) {
		this.handler = handler;
		if (this.handler == null) {
			throw new NullPointerException(
					"The configuration handler should not be " + this.handler
							+ ".");
		}
	}

	public String[] getAllRootNames() throws ConfigurationException {
		return handler.getAllRootNames();

	}

	public ConfigMetaData[] getAllRootConfigsMetaData()
			throws ConfigurationException {
		return handler.getAllRootConfigsMetaData();

	}

	public Configuration createSubConfiguration(String s)
			throws ConfigurationException, NameAlreadyExistsException,
			NameNotFoundException, ConfigurationLockedException {
		return handler.createSubConfiguration(s);
	}

	public Configuration createRootConfiguration(String s)
			throws ConfigurationException, NameAlreadyExistsException,
			ConfigurationLockedException {
		return handler.createRootConfiguration(s);
	}

	public Configuration openConfiguration(String s, int i)
			throws ConfigurationException, NameNotFoundException,
			ConfigurationLockedException {
		return handler.openConfiguration(s, i);
	}

	public Configuration openConfiguration(String path, int accessmode,
			boolean checkTransaction) throws ConfigurationException,
			NameNotFoundException, ConfigurationLockedException,
			InconsistentReadException {
		return handler.openConfiguration(path, accessmode, checkTransaction);
	}

	public void closeConfiguration(Configuration configuration)
			throws ConfigurationException {
		handler.closeConfiguration(configuration);
	}

	public void addConfigurationChangedListener(
			ConfigurationChangedListener configurationChangedListener, String s) {
		handler
				.addConfigurationChangedListener(configurationChangedListener,
						s);
	}

	public void addConfigurationChangedListener(
			ConfigurationChangedListener configurationChangedListener,
			String s, int i) {
		handler.addConfigurationChangedListener(configurationChangedListener,
				s, i);
	}

	public void removeConfigurationChangedListener(
			ConfigurationChangedListener configurationChangedListener, String s) {
		handler.removeConfigurationChangedListener(
				configurationChangedListener, s);
	}

	public String getId() throws ConfigurationException {
		return handler.getId();
	}

	public String getTransactionId() throws ConfigurationException {
		return handler.getTransactionId();
	}

	public Properties getCurrentProperties() {
		return handler.getCurrentProperties();
	}

	/**
	 * Create a new sub configuration with the specified type and open it in
	 * Write mode The parent of the configuration to be created must exist
	 * already. This method is available due to an optimization. It enables
	 * creating a sub configuration without the need to open (lock) the parent.
	 * 
	 * 
	 * @param path
	 *            The name of the configuration to be created
	 * @param type
	 *            The type of the configuration to be created
	 * @return new root Configuration
	 * @exception NameAlreadyExistsException
	 *                if there is already a rootconfiguration with this name
	 * @exception NameNotFoundException
	 *                if the parent does not exist
	 * @exception ConfigurationLockedException
	 *                in case the configuration is locked
	 * @exception ConfigurationException
	 *                in case of other errors
	 */
	public Configuration createSubConfiguration(String path, int type)
			throws ConfigurationException, NameAlreadyExistsException,
			NameNotFoundException, ConfigurationLockedException {
		return handler.createSubConfiguration(path, type);
	}

	/**
	 * close all configurations and all its sub configuratons currently open in
	 * this configuration handler and release all ressources. NOTE: Not
	 * committed changes will be lost.
	 * 
	 * @exception ConfigurationException
	 *                in case of internal errors (e.g. Database Errors)
	 */
	public void closeAllConfigurations() throws ConfigurationException {
	}

	/**
	 * commit all modifications on the configuration handler. Note: The open
	 * configurations may still be open and hold locks. When you finished with
	 * the configuration, you must call closeConfiguration()or
	 * closeAllConfigurations() in order to release the locks.
	 * 
	 * @exception ConfigurationException
	 *                in case a database access error occurs
	 */
	public void commit() throws ConfigurationException {
	}

	/**
	 * rollback all modifications on the configuration handler. Note: The open
	 * configurations may still be open and hold locks. When you finished with
	 * the configuration, you must call closeConfiguration()or
	 * closeAllConfigurations() in order to release the locks.
	 * 
	 * @exception ConfigurationException
	 *                in case a database access error occurs
	 */
	public void rollback() throws ConfigurationException {
	}

	/**
	 * Returns the CustomerParameterInterface to create/modify custom parameter
	 * mappings. Per ConfigurationHandler instance exists only one
	 * CustomParameterMappings object
	 * 
	 * @return CustomParameterMappings interface
	 * 
	 * @exception ConfigurationException
	 *                in case a database access error occurs
	 */
	public CustomParameterMappings getCustomParameterMappings()
			throws ConfigurationException {
		return handler.getCustomParameterMappings();
	}

	/**
	 * Closes all configurations associated with this CustomParameterMappings
	 * instance and removes it from open configs structure.
	 * 
	 * Note: The CustomParamMapping instance will also be released when using
	 * handler.closeAllConfigurations();
	 * 
	 * @exception ConfigurationException
	 *                in case a database access error occurs
	 */
	public void releaseCustomParameterMappings() throws ConfigurationException {
		handler.releaseCustomParameterMappings();
	}
}
