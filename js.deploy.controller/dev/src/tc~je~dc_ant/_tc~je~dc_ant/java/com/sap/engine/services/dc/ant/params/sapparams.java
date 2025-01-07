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
package com.sap.engine.services.dc.ant.params;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.sap.engine.services.dc.ant.SAPJ2EEEngine;
import com.sap.engine.services.dc.ant.SAPListFile;

/**
 * 
 * This class represents an Ant task for adding/removing/updating parameters in
 * the Net Weaver
 * 
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public class SAPParams extends Task {

	private SAPListFile paramsListFile;

	private final Collection engines = new ArrayList();

	private boolean remove;

	public SAPParams() {
	}

	/**
	 * Sets the remove flag. If the flag is true the available current
	 * parameters which do not match the provided list will be removed. Note! An
	 * empty list and enabled remove wil remove all current parameters.
	 * 
	 * @param versionRule
	 *            "true" | "false" | "yes" | "no".
	 */
	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	public void addSAPJ2EEEngine(SAPJ2EEEngine engine) {
		this.engines.add(engine);
	}

	public void addSAPParamsList(SAPListFile paramsList) {
		this.paramsListFile = paramsList;
	}

	public void validate() throws BuildException {
		if (engines.size() != 1) {
			throw new BuildException(
					"The parameters operation should be triggered on one engine - "
							+ " the originator of the j2ee client libraries. Currently "
							+ engines.size()
							+ " are specified. Please check the occurance(s) of nested 'sapj2eeengine' declarations!");
		}
		if (this.paramsListFile.getListFilePath() == null
				|| this.paramsListFile.getListFilePath().trim().equals("")) {
			throw new BuildException(
					"The parameters list file path is empty or missing. "
							+ "Please check the 'sapparamslist' element.");
		}
	}

	public void execute() throws BuildException {
		setParameters();
	}

	private void setParameters() {
		printSetupData();

		log("Starting to validate the data...");
		validate();
		log("Validation ended");

		final Collection params = getParams();

		printParams(params);

		for (Iterator iter = this.engines.iterator(); iter.hasNext();) {
			final SAPJ2EEEngine engine = (SAPJ2EEEngine) iter.next();
			log("Starting validation for the engine '" + engine + "'");
			engine.validate();
			log("Engine data validated successfully");

			final SAPParametersData paramsData = new SAPParametersData(engine,
					params, remove);
			log("Starting set parameters for the engine '" + engine + "'");
			try {
				SAPParamsResult result = SAPParamsHelper.getInstance()
						.setParameters(this, paramsData);

				log("The result of the parameters setting is: updated "
						+ result.getParamsUpdated() + "; added "
						+ result.getParamsAdded() + "; removed "
						+ result.getParamsRemoved());
			} catch (SAPParamsException de) {
				log("An error occured during the parameters setting.",
						Project.MSG_ERR);
				throw new BuildException(de);
			}

			log("Setting parameters ended");
		}
	}

	private Collection getParams() {
		Collection params = new ArrayList();
		HashMap duplicateChecker = new HashMap();
		File lisFile = new File(paramsListFile.getListFilePath());
		try {
			FileInputStream fis = new FileInputStream(lisFile);
			FileReader fr = new FileReader(lisFile);
			BufferedReader br = new BufferedReader(fr);
			String sLine = br.readLine();
			while (sLine != null) {
				int idx = sLine.indexOf("=");
				if (idx != -1) {
					String name = sLine.substring(0, idx);
					String value = sLine.substring(idx + 1);
					String containedValue = (String) duplicateChecker.get(name);
					if (containedValue == null) {
						params.add(new String[] { name, value });
						duplicateChecker.put(name, value);
					} else {
						log("Parameter '"
								+ name
								+ "' is already specified in the list with value '"
								+ containedValue
								+ "'! Duplicate parameters are not allowed. The value '"
								+ value + "' will not be considered!");
					}
				} else {
					log("No value is specified for parameter '"
							+ sLine
							+ "'! It will be skipped. Please use <param>=<value>.");
				}
				sLine = br.readLine();
			}
			br.close();
			fr.close();
			fis.close();
		} catch (IOException e) {
			throw new BuildException(e);
		}

		return params;
	}

	private void printSetupData() {
		log("Starting parameters set with the following properties:");
		log("The targeted SAP J2EE Engines are: " + this.engines);
	}

	private void printParams(Collection paramsList) {
		log("Selected parameters:");
		int idx = 0;
		for (Iterator iter = paramsList.iterator(); iter.hasNext();) {
			String[] paramData = (String[]) iter.next();
			final String name = paramData[0];
			final String value = paramData[1];
			log(++idx + ": " + name + "=" + value);
		}
	}
}
