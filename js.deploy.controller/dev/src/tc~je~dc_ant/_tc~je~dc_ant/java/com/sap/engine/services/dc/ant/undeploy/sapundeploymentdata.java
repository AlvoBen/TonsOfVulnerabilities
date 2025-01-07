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

import java.util.Collection;

import com.sap.engine.services.dc.ant.SAPJ2EEEngine;

/**
 * 
 * This class represents undeployment data passed from the undeploy task
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public final class SAPUndeploymentData {

	private final String undeployStrategy;

	private final Collection errorHandlings;

	private final Collection items;

	private final SAPJ2EEEngine engine;

	private final String toString;

	SAPUndeploymentData(SAPJ2EEEngine engine, Collection items,
			Collection errorHandlings, String undeployStrategy) {
		this.engine = engine;
		this.items = items;
		this.errorHandlings = errorHandlings;
		this.undeployStrategy = undeployStrategy;

		this.toString = this.generateToString();
	}

	/**
	 * @return Returns the archives.
	 */
	public Collection getItems() {
		return this.items;
	}

	/**
	 * @return Returns the engine.
	 */
	public SAPJ2EEEngine getEngine() {
		return this.engine;
	}

	/**
	 * @return Returns the errorHandlings.
	 */
	public Collection getErrorHandlings() {
		return this.errorHandlings;
	}

	/**
	 * @return Returns the versionRule.
	 */
	public String getUndeployStrategy() {
		return this.undeployStrategy;
	}

	private String generateToString() {
		return "\nengine: '" + this.engine + "'\narchives: '" + this.items
				+ "'\nerror handlings: '" + this.errorHandlings
				+ "'\nversion handling rule: '" + this.undeployStrategy + "'";
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
