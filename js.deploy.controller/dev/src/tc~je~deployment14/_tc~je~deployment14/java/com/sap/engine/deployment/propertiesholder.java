/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.deployment;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Properties;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Mariela Todorova
 */
public class PropertiesHolder implements Constants {
	private static final Location location = Location
			.getLocation(PropertiesHolder.class);

	// properties from deployment14.properties file
	/**
	 * Property for setting work dir of SAP Deployment Manager
	 */
	public static final String WORK_DIR = "work.dir";

	/**
	 * Property for setting log dir of SAP Deployment Manager
	 */
	public static final String LOG_DIR = "log.dir";

	/**
	 * Property for specifying the location of j2ee dir
	 */
	public static final String J2EE_DIR = "j2ee.dir";

	/**
	 * Property for specifying whether temp dirs should be deleted
	 */
	public static final String CLEAR_TEMP_DIRS = "clear.temp.dirs";

	/**
	 * Property for specifying the time-to-wait factor for DEPLOY script
	 */
	public static final String TIME_TO_WAIT_FACTOR = "time.to.wait.factor";

	/**
	 * Property for specifying sda attributes
	 */
	public static final String SDA_ATTRIBUTES = "sda.attributes";

	// properties from System.properties used for CTS runs
	/**
	 * System property for setting cts work dir of SAP Deployment Manager
	 */
	public static final String CTS_WORK_DIR = "cts.working.dir";

	/**
	 * System property for specifying the location of j2ee home
	 */
	public static final String J2EE_HOME = "J2EE_HOME";

	// properties from DEPLOY script
	/**
	 * System property for specifying whether to use DS or DC
	 */
	public static final String PROXY = "PROXY";

	private static Properties properties = null;

	public static void init() {
		init(null);
	}

	/**
	 * Initialize the properties when the functionality is used client side.
	 * <p>
	 * Note: The method should not be called when running on server side.
	 * </p>
	 * 
	 * @param path
	 */
	public static void init(String path) {

		String propsDir = null;
		if (path != null) {
			propsDir = path;
		} else {
			if (null == (propsDir = getPropertiesFileByJ2eeHome())) {
				if (null == (propsDir = getPropertiesFileByServerCurrentDir())) {
					propsDir = getPropertiesFileByDeploymentScripts();
				}
			}
		}

		properties = loadProperties(propsDir + sep + DEPLOY_PROPS);

		String sProp = System.getProperty(J2EE_HOME);
		if (sProp != null) {
			properties.setProperty(J2EE_HOME, sProp);
		}

		sProp = System.getProperty(CTS_WORK_DIR);

		if (sProp != null) {
			properties.setProperty(CTS_WORK_DIR, sProp);
		}

	}

	private static String getPropertiesFileByJ2eeHome() {
		String propertiesFilePath = System.getProperty(J2EE_HOME) + sep
				+ DEPLOYMENT + sep + CFG;
		return validatedPropertiesFilePath(propertiesFilePath);
	}

	private static String getPropertiesFileByServerCurrentDir() {
		String propertiesFilePath = ".." + sep + ".." + sep + DEPLOYMENT + sep
				+ CFG;
		return validatedPropertiesFilePath(propertiesFilePath);
	}

	private static String getPropertiesFileByDeploymentScripts() {
		return ".." + sep + CFG;
	}

	/**
	 * Returns the given file path if it contains the deployment properties
	 * file, otherwise returns null
	 * 
	 * @param propertiesFilePath
	 * @return propertiesFilePath or null
	 */
	private static String validatedPropertiesFilePath(String propertiesFilePath) {
		if (new File(propertiesFilePath + sep + DEPLOY_PROPS).isFile()
				&& new File(propertiesFilePath + sep + DEPLOY_PROPS).canRead()) {
			return propertiesFilePath;
		}
		return null;
	}

	private static Properties loadProperties(String propsFile) {
		try {
			properties = new Properties();
			BufferedReader reader = new BufferedReader(
					new FileReader(propsFile));
			String line = null;
			String key = null;
			String value = null;
			int index = -1;

			while ((line = reader.readLine()) != null) {
				index = line.indexOf('=');

				if (line.startsWith("!") || line.startsWith("#") || index <= 0) {
					continue;
				}

				key = line.substring(0, index);
				value = line.substring(index + 1);
				properties.setProperty(key, value);
			}
		} catch (Exception exc) {// $JL-EXC$
			System.out.println("Could not load properties");// $JL-SYS_OUT_ERR$
			exc.printStackTrace();
		}

		return properties;
	}

	public static String getProperty(String key) {
		if (properties == null) {
			return null;
		}

		Logger.trace(location, Severity.DEBUG, "Returning property " + key
				+ "=" + properties.getProperty(key));
		return properties.getProperty(key);
	}

}
