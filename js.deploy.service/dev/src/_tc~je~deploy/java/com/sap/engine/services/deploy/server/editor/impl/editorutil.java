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
package com.sap.engine.services.deploy.server.editor.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.deploy.ear.jar.StandaloneModuleReader;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.ObjectSerializer;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class EditorUtil {

	private static final Location location = 
		Location.getLocation(EditorUtil.class);
	/**
	 * Sets serialized object.
	 * 
	 * @param config
	 *            configuration.
	 * @param fileName
	 *            file name.
	 * @param info
	 *            object.
	 * @param errorMessage
	 *            error message.
	 * @throws ServerDeploymentException
	 *             if a problem occurs during serialization.
	 */
	public static void setSerializedObject(Configuration config,
			String fileName, Object info, String errorMessage)
			throws ServerDeploymentException, ConfigurationException {
		ByteArrayInputStream baiStream = null;
		try {
			try {
				baiStream = new ByteArrayInputStream(ObjectSerializer
						.getByteArray(info));
			} catch (Exception ex) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.ERROR_IN_SERIALIZATION,
						new String[] { errorMessage }, ex);
				sde.setMessageID("ASJ.dpl_ds.005031");
				throw sde;				
			}
			config.updateFileAsStream(fileName, baiStream, true);
		} finally {
			close(baiStream);
		}
	}

	public static void setSerializedObject(File file, Object info,
			String errorMessage) throws ServerDeploymentException {
		ByteArrayInputStream baiStream = null;
		try {
			baiStream = new ByteArrayInputStream(ObjectSerializer
					.getByteArray(info));
			FileUtils.writeToFile(baiStream, file);
		} catch (Exception ex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERROR_IN_SERIALIZATION,
					new String[] { errorMessage }, ex);
			sde.setMessageID("ASJ.dpl_ds.005031");
			throw sde;				
		} finally {
			close(baiStream);
		}
	}

	/**
	 * Returns deserialized object.
	 * 
	 * @param appConfig
	 *            application configuration.
	 * @param fileName
	 *            file name.
	 * @return deserialized object.
	 * @throws ServerDeploymentException
	 * @throws ConfigurationException
	 * @throws NameNotFoundException
	 * @throws InconsistentReadException
	 */
	public static Object getDeserializedObject(Configuration appConfig,
		String fileName) throws ServerDeploymentException,
		ConfigurationException {
		InputStream iStream = null;
		try {
			if (appConfig.existsFile(fileName)) {
				iStream = appConfig.getFile(fileName);
				try {
					return ObjectSerializer.getObject(iStream);
				} catch (Exception ex) {
					ServerDeploymentException sde = 
						new ServerDeploymentException(
						ExceptionConstants.ERROR_IN_DESERIZALIZATION, 
						new Object[] {fileName}, ex);
					sde.setMessageID("ASJ.dpl_ds.005032");
					throw sde;
					
				}
			} else if (appConfig.existsConfigEntry(fileName)) {
				return appConfig.getConfigEntry(fileName);
			} else {
				return null;
			}
		} finally {
			close(iStream);
		}
	}

	public static Object getDeserializedObject(File file)
		throws ServerDeploymentException {
		if (!file.exists()) {
			return null;
		}
		InputStream iStream = null;
		try {			
			try {
				iStream = new FileInputStream(file);
				return ObjectSerializer.getObject(iStream);
			} catch (Exception ex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERROR_IN_DESERIZALIZATION, 
					new Object[] {file}, ex);
				sde.setMessageID("ASJ.dpl_ds.005032");
				throw sde;
			}
		} finally {
			close(iStream);
		}
	}

	private static void close(InputStream iStream) {
		try {
			if (iStream != null) {
				iStream.close();
			}
		} catch (IOException ioe) {
			final ServerDeploymentException sde = 
				new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "closing a stream." }, ioe);
			sde.setMessageID("ASJ.dpl_ds.005082");
			DSLog.logErrorThrowable(location, sde);
		}
	}

}
