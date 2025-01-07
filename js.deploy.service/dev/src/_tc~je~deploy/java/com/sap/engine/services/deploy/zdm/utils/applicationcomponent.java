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
package com.sap.engine.services.deploy.zdm.utils;

import java.io.Serializable;
import java.util.Properties;

import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.utils.DSConstants;

/**
 * Describes the application, which is going to be patched using the rolling
 * approach.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
public class ApplicationComponent implements Serializable {

	private static final long serialVersionUID = -1716537651305056396L;

	private final ApplicationName applicationName;
	private final String filePath;
	private final boolean isStandalone;
	private final Properties properties;

	private final int hashCode;
	private final StringBuilder toString;

	/**
	 * Constructs ApplicationComponent object by its vendor, name, file path,
	 * boolean value indicating if it is standalone application, and Properties.
	 * 
	 * @param dcVendor
	 *            the vendor
	 * @param dcName
	 *            the name
	 * @param filePath
	 *            path to the sda file that is updated
	 * @param isStandalone
	 *            <code>true</code> if standalone <code>false</code> otherwise
	 * @param properties
	 *            the properties
	 */
	public ApplicationComponent(String dcVendor, String dcName,
			String filePath, boolean isStandalone, Properties properties) {
		this.applicationName = new ApplicationName(dcVendor, dcName);
		this.filePath = filePath;
		this.isStandalone = isStandalone;
		this.properties = (properties == null ? new Properties() : properties);

		this.hashCode = evaluateHashCode();
		this.toString = evaluateToStsring();
	}

	/**
	 * Gets the application name to be patched.
	 * 
	 * @return ApplicationName
	 */
	public ApplicationName getApplicationName() {
		return applicationName;
	}

	/**
	 * Gets the file path
	 * 
	 * @return File path as <code>String</code>
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Checks if it is standalone application
	 * 
	 * @return <code>true</code> if standalone, <code>false</code> otherwise
	 */
	public boolean isStandalone() {
		return isStandalone;
	}

	/**
	 * Gets the Properties for this <code>ApplicationComponent</code>
	 * 
	 * @return Properties
	 */
	public Properties getProperties() {
		return properties;
	}

	// ************************** OBJECT **************************//

	public int hashCode() {
		return hashCode;
	}

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

		final ApplicationComponent otherApplicationComponent = (ApplicationComponent) obj;
		if (!this.getApplicationName().equals(
				otherApplicationComponent.getApplicationName())) {
			return false;
		}
		if (!this.getFilePath().equals(otherApplicationComponent.getFilePath())) {
			return false;
		}
		if (this.isStandalone() != otherApplicationComponent.isStandalone()) {
			return false;
		}
		if (!this.getProperties().equals(
				otherApplicationComponent.getProperties())) {
			return false;
		}

		return true;
	}

	public String toString() {
		return toString.toString();
	}

	// ************************** OBJECT **************************//

	// ************************** PRIVATE **************************//

	public int evaluateHashCode() {
		final int offset = 17;
		final int multiplier = 59;

		int result = offset + getApplicationName().hashCode();
		result += result * multiplier + getFilePath().hashCode();
		result += result * multiplier + (isStandalone() ? 1 : 0);
		result += result * multiplier + getProperties().hashCode();

		return result;
	}

	private StringBuilder evaluateToStsring() {
		final StringBuilder sb = new StringBuilder();
		sb.append("ApplicationName = ");
		sb.append(getApplicationName());
		sb.append(DSConstants.EOL);
		sb.append("FilePath = ");
		sb.append(getFilePath());
		sb.append(DSConstants.EOL);
		sb.append("isStandalone = ");
		sb.append(isStandalone());
		sb.append(DSConstants.EOL);
		sb.append("Properties = ");
		sb.append(getProperties());
		sb.append(DSConstants.EOL);
		return sb;
	}

	// ************************** PRIVATE **************************//

}
