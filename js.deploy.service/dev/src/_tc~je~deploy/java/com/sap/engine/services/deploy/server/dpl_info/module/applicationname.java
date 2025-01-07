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
package com.sap.engine.services.deploy.server.dpl_info.module;

import java.io.IOException;
import java.io.Serializable;

import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.utils.StringUtils;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class ApplicationName implements Serializable {

	static final long serialVersionUID = -6260254710303153260L;

	private String provider;
	private String name;

	private String applicationName;

	public ApplicationName(String applicationName) {
		String[] parsedAppName = parseApplicationName(applicationName);
		this.provider = parsedAppName[0];
		this.name = parsedAppName[1];
		this.applicationName = getApplicationName(getProvider(), getName());

		this.provider = StringUtils.intern(this.provider);
		this.name = StringUtils.intern(this.name);
		this.applicationName = StringUtils.intern(this.applicationName);
	}

	public ApplicationName(String provider, String name) {
		this.provider = provider;
		this.name = name;
		this.applicationName = getApplicationName(getProvider(), getName());

		this.provider = StringUtils.intern(this.provider);
		this.name = StringUtils.intern(this.name);
		this.applicationName = StringUtils.intern(this.applicationName);
	}

	private String getApplicationName(String provider, String name) {
		StringBuilder sb = new StringBuilder(provider.length() + name.length()
				+ 1);
		sb.append(provider).append(
				DeployConstants.DELIMITER_4_PROVIDER_AND_NAME).append(name);
		return StringUtils.intern(sb.toString());
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the provider.
	 */
	public String getProvider() {
		return provider;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String toString() {
		return getApplicationName();
	}

	public int hashCode() {
		return getApplicationName().hashCode();
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

		final ApplicationName otherApplicationName = (ApplicationName) obj;

		if (!getApplicationName().equals(
				otherApplicationName.getApplicationName())) {
			return false;
		}

		return true;
	}

	public Object clone() throws CloneNotSupportedException {
		return new ApplicationName(getApplicationName());
	}

	public static String[] parseApplicationName(String appName) {
		String[] result = new String[2];
		final int index = appName
				.indexOf(DeployConstants.DELIMITER_4_PROVIDER_AND_NAME);
		if (index != -1) {
			if (index == 0) {
				result[0] = DeployConstants.DEFAULT_PROVIDER_4_APPS_SAP_COM;
			} else {
				result[0] = StringUtils.intern(appName.substring(0, index));
			}
			result[1] = appName.substring(index + 1);
		} else {
			result[0] = DeployConstants.DEFAULT_PROVIDER_4_APPS_SAP_COM;
			result[1] = appName;
		}
		return result;
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.provider = StringUtils.intern(this.provider);
		this.name = StringUtils.intern(this.name);
		this.applicationName = StringUtils.intern(this.applicationName);
	}
}
