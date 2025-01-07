package com.sap.engine.services.dc.ant.deploy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import com.sap.engine.services.dc.ant.SAPErrorHandling;
import com.sap.engine.services.dc.ant.SAPJ2EEEngine;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-6
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */
public class SAPDeploy extends Task {

	private String versionRule;
	private Long deployTimeout;


	private String resultProperty;
	private boolean throwsError;

	private final Collection engines = new ArrayList();
	private final Collection fileSets = new ArrayList();
	private final Collection errorHandlings = new ArrayList();

	public SAPDeploy() {
	}

	/**
	 * @return Returns the versionRule.
	 */
	public String getVersionRule() {
		return this.versionRule;
	}

	/**
	 * Sets the version rule with which the deployment will be processed.
	 * 
	 * @param versionRule
	 *            "all" | "same_and_lower" | "lower".
	 */
	public void setVersionRule(String versionRule) {
		this.versionRule = versionRule;
	}

	public void setThrowsError(boolean throwsErr) {
		this.throwsError = throwsErr;
	}
	
	public void setResultProperty(String resultProperty) {
		this.resultProperty = resultProperty;
	}

	public void addSAPJ2EEEngine(SAPJ2EEEngine engine) {
		this.engines.add(engine);
	}

	public void addFileset(FileSet fileSet) {
		this.fileSets.add(fileSet);
	}
	public void setDeployTimeout(Long deployTimeout) {
		this.deployTimeout = deployTimeout;
	}
	
	public void addSAPErrorHandling(SAPErrorHandling errorHandling) {
		this.errorHandlings.add(errorHandling);
	}

	public void validate() throws BuildException {
		if (engines.size() != 1) {
			throw new BuildException(
					"The deploy operation should be triggered on one engine - "
							+ " the originator of the j2ee client libraries. Currently "
							+ engines.size()
							+ " are specified. Please check the occurance(s) of nested 'sapj2eeengine' declarations!");
		}

		if (this.versionRule != null && !this.versionRule.trim().equals("")
				&& !SAPVersionHandlingRule.isValid(this.versionRule)) {
			throw new BuildException("The version rule attribute '"
					+ this.versionRule + "' is not correct.");
		}
		if( (this.deployTimeout != null) && (this.deployTimeout <= 0) ){
			throw new BuildException("The given deploy timeout " +this.deployTimeout+" is not correct. It must be valid integer greater than 0. Correct its value in used ant xml.");
		}

		for (Iterator iter = this.errorHandlings.iterator(); iter.hasNext();) {
			final SAPErrorHandling errorHandling = (SAPErrorHandling) iter
					.next();
			errorHandling.validate();
		}
	}

	public void execute() throws BuildException {
		deploy();
	}

	private void deploy() {
		printSetupData();

		log("Starting to validate the data...");
		validate();
		log("Validation ended");

		final Collection archives = getArchives();

		printArchives(archives);

		if (archives.isEmpty()) {
			log("No archives are selected for deployment.");
			return;
		}

		for (Iterator iter = this.engines.iterator(); iter.hasNext();) {
			final SAPJ2EEEngine engine = (SAPJ2EEEngine) iter.next();
			log("Starting validation for the engine '" + engine + "'");
			engine.validate();
			log("Engine data validated successfully");

			final SAPDeploymentData deploymentData = new SAPDeploymentData(
					engine, archives, this.errorHandlings, this.versionRule, this.deployTimeout);
			log("Starting deployment for the engine '" + engine + "'");
			SAPDeployResult deployResult = SAPDeployResult.ERROR;
			try {
				deployResult = SAPDeployHelper.getInstance().doDeploy(
						deploymentData);
			} catch (SAPDeploymentException de) {
				log("An error occured during the deployment.", Project.MSG_ERR);
			}
			// additional code for deploy result error handling
			if (SAPDeployResult.ERROR.equals(deployResult) && throwsError) {
				throw new BuildException("The result of the deployment is: "
						+ deployResult);
			}

			if (resultProperty != null && resultProperty.length() > 0) {
				getProject()
						.setProperty(resultProperty, deployResult.getName());
				log("Setting deploy result: " + resultProperty + "="
						+ deployResult.getName());
			}

			log("The result of the deployment is: " + deployResult);
			log("Deployment ended");
		}
	}

	private Collection getArchives() {
		final Collection archives = new ArrayList();
		for (Iterator iter = this.fileSets.iterator(); iter.hasNext();) {
			final FileSet fileSet = (FileSet) iter.next();
			final File rootDir = fileSet.getDir(this.getProject());
			log("root dir " + rootDir);
			final DirectoryScanner dirScanner = fileSet
					.getDirectoryScanner(this.getProject());
			final String[] includedFiles = dirScanner.getIncludedFiles();

			for (int i = 0; i < includedFiles.length; i++) {
				archives.add(new File(rootDir, includedFiles[i])
						.getAbsolutePath());
			}
		}

		return archives;
	}

	private void printSetupData() {
		log("Starting deployment with the following properties:");
		log("Version Handling Rule: " + this.versionRule);
		log("Error Handling Strategies: " + this.errorHandlings);
		log("The targeted SAP J2EE Engines are: " + this.engines);
		log("Deploy timeout: " + this.deployTimeout);
	}

	private void printArchives(Collection archives) {
		log("Selected archives for deployment:");
		int idx = 0;
		for (Iterator iter = archives.iterator(); iter.hasNext();) {
			final String archivePath = (String) iter.next();
			log(++idx + ": '" + archivePath + "'");
		}
	}

}
