package com.sap.engine.services.dc.ant.deploy;

import java.util.Collection;

import com.sap.engine.services.dc.ant.SAPJ2EEEngine;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-9
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */
final class SAPDeploymentData {

	private final String versionRule;
	private final Collection errorHandlings;
	private final Collection archives;
	private final SAPJ2EEEngine engine;
	private final String toString;
	private final Long deployTimeout;


	SAPDeploymentData(SAPJ2EEEngine engine, Collection archives,
			Collection errorHandlings, String versionRule, Long deployTimeout) {
		this.engine = engine;
		this.archives = archives;
		this.errorHandlings = errorHandlings;
		this.versionRule = versionRule;
		this.deployTimeout = deployTimeout;

		this.toString = this.generateToString();
	}

	Long getDeployTimeout() {
		return deployTimeout;
	}

	/**
	 * @return Returns the archives.
	 */
	Collection getArchives() {
		return this.archives;
	}

	/**
	 * @return Returns the engine.
	 */
	SAPJ2EEEngine getEngine() {
		return this.engine;
	}

	/**
	 * @return Returns the errorHandlings.
	 */
	Collection getErrorHandlings() {
		return this.errorHandlings;
	}

	/**
	 * @return Returns the versionRule.
	 */
	String getVersionRule() {
		return this.versionRule;
	}

	private String generateToString() {
		return "\nengine: '" + this.engine + "'\narchives: '" + this.archives
				+ "'\nerror handlings: '" + this.errorHandlings
				+ "'\nversion handling rule: '" + this.versionRule
				+ "'\ndeploy timeout: '" + this.deployTimeout+ "'";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.toString;
	}

}
