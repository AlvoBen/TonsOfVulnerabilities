/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.utils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.containers.Containers;

/**
 * @author Luchesar Cekov
 */
public class StandaloneModulesUtil {
	public static void addSuitableContainersForStandAlone(Set cNames,
			File moduleFile, Properties properties) throws DeploymentException {
		final String swtType = properties
				.getProperty(DeployService.softwareType);
		final String swtSubType = properties
				.getProperty(DeployService.softwareSubType);
		String contName = null;
		if (swtType != null || swtSubType != null) {
			findContainersSupportingSoftType(swtType, swtSubType, cNames);
		}

		String moduleFilePath = moduleFile.getName();
		if (moduleFilePath.lastIndexOf('.') != -1) {
			String extension = moduleFilePath.substring(moduleFilePath
					.lastIndexOf('.'));
			if (extension.equalsIgnoreCase(".jar")) {
				contName = getContainerNameForJarFile(moduleFile);
				if (contName != null) {
					cNames.add(contName);
				}
			} else {
				ContainerInterface cont = null;
				Iterator containersEnum = Containers.getInstance().getAll()
						.iterator();
				String[] contExts = null;
				ContainerInfo cinfo = null;
				while (containersEnum.hasNext()) {
					cont = (ContainerInterface) containersEnum.next();
					cinfo = cont.getContainerInfo();
					if (cinfo.isJ2EEContainer()) {
						contExts = cinfo.getFileExtensions();
						if (foundString(extension, contExts)) {
							cNames.add(cinfo.getName());
						}
					} else {
						if (findInNonJ2EEContainer(moduleFilePath, extension,
								cinfo)) {
							cNames.add(cinfo.getName());
						}
					}
				}
			}
		} else {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.FILE_WITHOUT_EXT, new String[] {
							moduleFile.getName(), "" });
			sde.setMessageID("ASJ.dpl_ds.005044");
			throw sde;
		}
	}

	private static void findContainersSupportingSoftType(String softType,
			String swtSubType, Set cNames) {
		Iterator containers = Containers.getInstance().getAll().iterator();
		ContainerInterface cont = null;
		while (containers.hasNext()) {
			cont = (ContainerInterface) containers.next();
			if (cont.getContainerInfo().isSoftwareTypeSupported(softType,
					swtSubType)) {
				cNames.add(cont.getContainerInfo().getName());
			}
		}
	}

	private static String getContainerNameForJarFile(File moduleFile)
			throws DeploymentException {
		JarFile jar = null;
		ContainerInterface cinterf = null;
		try {
			jar = new JarFile(moduleFile);
			Enumeration entries = jar.entries();
			JarEntry entry = null;
			while (entries.hasMoreElements()) {
				entry = (JarEntry) entries.nextElement();
				if (entry.getName().equalsIgnoreCase("meta-inf/ejb-jar.xml")) {
					cinterf = getContainer(J2EEModule.Type.ejb.name());
					break;
				} else if (entry.getName().equalsIgnoreCase(
						"META-INF/application-client.xml")) {
					cinterf = getContainer(J2EEModule.Type.java.name());
					break;
				}
			}
		} catch (IOException ioex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERROR_IN_PROCESSING_JAR,
					new String[] { moduleFile.getAbsolutePath() }, ioex);
			sde.setMessageID("ASJ.dpl_ds.005043");
			throw sde;
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException ioex) {
					ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.ERROR_IN_PROCESSING_JAR,
							new String[] { moduleFile.getAbsolutePath() }, ioex);
					sde.setMessageID("ASJ.dpl_ds.005043");
					throw sde;
				}
			}
		}
		if (cinterf != null) {
			return cinterf.getContainerInfo().getName();
		}
		return null;

	}

	private static boolean foundString(String str, String[] arr) {
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				if (str.equalsIgnoreCase(arr[i].toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean findInNonJ2EEContainer(String moduleFilePath,
			String extension, ContainerInfo cinfo) {
		String[] names = cinfo.getFileNames();
		if (foundString(moduleFilePath, names)) {
			return true;
		}
		names = cinfo.getFileExtensions();
		if (foundString(extension, names)) {
			return true;
		}
		return false;
	}

	public static ContainerInterface getContainer(String moduleName) {
		Iterator containers = Containers.getInstance().getAll().iterator();
		ContainerInterface cont = null;
		while (containers.hasNext()) {
			cont = (ContainerInterface) containers.next();
			if (moduleName.equalsIgnoreCase(cont.getContainerInfo()
					.getJ2EEModuleName())) {
				return cont;
			}
		}
		return null;
	}
}
