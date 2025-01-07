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
package com.sap.engine.services.dc.ant;

import org.apache.tools.ant.BuildException;

/**
 * 
 * This class represents a file with listed: 1) components for undeploy
 * <vendor/component_name> (each component on single line) 2) parameters for
 * add/update <param_name=param_value> (each parameter on a single line) ...
 * 
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public class SAPListFile {

	private String listFilePath;

	public SAPListFile() {
	}

	/**
	 * @return Returns the list file path.
	 */
	public String getListFilePath() {
		return this.listFilePath;
	}

	/**
	 * @param listFileAbsPath
	 *            The path to the list file to set.
	 */
	public void setListFilePath(String listFilePath) {
		this.listFilePath = listFilePath;
	}

	public void validate() throws BuildException {
		if (this.listFilePath == null || this.listFilePath.trim().equals("")) {
			throw new BuildException(
					"The list file path attribute must be set.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "list file path: '" + this.listFilePath + "'.";
	}
}
