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
package com.sap.engine.services.dc.ant.undeploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.sap.engine.services.dc.ant.SAPErrorHandling;
import com.sap.engine.services.dc.ant.SAPJ2EEEngine;
import com.sap.engine.services.dc.ant.SAPListFile;

/**
 * 
 * The class represents a SAP Net Weaver undeployment task.
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public class SAPUndeploy extends Task {

	private String undeployStrategy;
	SAPListFile undeployList;

	private final Collection engines = new ArrayList();

	private final Collection errorHandlings = new ArrayList();

	public SAPUndeploy() {
	}

	/**
	 * Sets the undeployment strategy with which the deployment will be
	 * processed.
	 * 
	 * @param versionRule
	 *            "IfDependingStop" | "UndeployDepending".
	 */
	public void setUndeployStrategy(String undeployStrategy) {
		this.undeployStrategy = undeployStrategy;
	}

	public void addSAPJ2EEEngine(SAPJ2EEEngine engine) {
		this.engines.add(engine);
	}

	public void addSAPErrorHandling(SAPErrorHandling errorHandling) {
		this.errorHandlings.add(errorHandling);
	}

	public void addSAPUndeployList(SAPListFile undeployList) {
		this.undeployList = undeployList;
	}

	public void validate() throws BuildException {
		if (engines.size() != 1) {
			throw new BuildException(
					"The undeploy operation should be triggered on one engine - "
							+ " the originator of the j2ee client libraries. Currently "
							+ engines.size()
							+ " are specified. Please check the occurance(s) of nested 'sapj2eeengine' declarations!");
		}
		if (this.undeployList.getListFilePath() == null
				|| this.undeployList.getListFilePath().trim().equals("")) {
			throw new BuildException(
					"The undeployment list is empty or missing. "
							+ "Please check the 'sapundeploylist' element.");
		}
		if (this.undeployStrategy != null
				&& !this.undeployStrategy.trim().equals("")
				&& !SAPUndeploymentStrategy.isValid(this.undeployStrategy)) {
			throw new BuildException("The undeployment strategy attribute '"
					+ this.undeployStrategy + "' is not correct.");
		}

		for (Iterator iter = this.errorHandlings.iterator(); iter.hasNext();) {
			final SAPErrorHandling errorHandling = (SAPErrorHandling) iter
					.next();
			errorHandling.validate();
		}
	}

	public void execute() throws BuildException {
		undeploy();
	}

	private void undeploy() {
		printSetupData();

		log("Starting to validate the data...");
		validate();
		log("Validation ended");

		final Collection undeployItems = getUndeployItems();

		printItems(undeployItems);

		if (undeployItems.isEmpty()) {
			log("No items are selected for undeployment.");
			return;
		}

		for (Iterator iter = this.engines.iterator(); iter.hasNext();) {
			final SAPJ2EEEngine engine = (SAPJ2EEEngine) iter.next();
			log("Starting validation for the engine '" + engine + "'");
			engine.validate();
			log("Engine data validated successfully");

			final SAPUndeploymentData deploymentData = new SAPUndeploymentData(
					engine, undeployItems, this.errorHandlings,
					this.undeployStrategy);
			log("Starting undeployment for the engine '" + engine + "'");
			try {
				final SAPUndeployResult undeployResult = SAPUndeployHelper
						.getInstance().doUndeploy(this, deploymentData);

				log("The result of the undeployment is " + undeployResult);
			} catch (SAPUndeploymentException de) {
				log("An error occured during the undeployment.",
						Project.MSG_ERR);
				throw new BuildException(de);
			}

			log("Undeployment ended");
		}
	}

	private Collection getUndeployItems() {
		ArrayList undeployItems = new ArrayList();
		File lisFile = new File(undeployList.getListFilePath());
		try {
			FileInputStream fis = new FileInputStream(lisFile);
			FileReader fr = new FileReader(lisFile);
			BufferedReader br = new BufferedReader(fr);
			String sLine = br.readLine();
			while (sLine != null) {
				undeployItems.add(sLine.trim());
				sLine = br.readLine();
			}
			br.close();
			fr.close();
			fis.close();
		} catch (IOException e) {
			throw new BuildException(e);
		}

		return undeployItems;
	}

	private void printSetupData() {
		log("Starting deployment with the following properties:");
		log("Undeployment strategy: " + this.undeployStrategy);
		log("Error Handling Strategies: " + this.errorHandlings);
		log("The targeted SAP J2EE Engines are: " + this.engines);
	}

	private void printItems(Collection undeployList) {
		log("Selected components for undeployment:");
		int idx = 0;
		for (Iterator iter = undeployList.iterator(); iter.hasNext();) {
			final String undeployItem = (String) iter.next();
			log(++idx + ": '" + undeployItem + "'");
		}
	}

}
