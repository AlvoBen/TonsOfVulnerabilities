package com.sap.engine.services.dc.ant;

import org.apache.tools.ant.BuildException;

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
public class SAPErrorHandling {

	private String errorAction;
	private String errorStrategy;

	public SAPErrorHandling() {
	}

	/**
	 * @return Returns the errorAction.
	 */
	public String getErrorAction() {
		return this.errorAction;
	}

	/**
	 * Sets the error action with which the deployment will be processed.
	 * 
	 * @param errorAction
	 *            "prerequisites" | "deploy" | "undeploy".
	 */
	public void setErrorAction(String errorAction) {
		this.errorAction = errorAction;
	}

	/**
	 * @return Returns the errorStrategy.
	 */
	public String getErrorStrategy() {
		return this.errorStrategy;
	}

	/**
	 * Sets the error action with which the deployment will be processed.
	 * 
	 * @param errorStrategy
	 *            "stop" | "skip".
	 */
	public void setErrorStrategy(String errorStrategy) {
		this.errorStrategy = errorStrategy;
	}

	public void validate() throws BuildException {
		boolean errActionValidated = false;
		if (this.errorAction != null && !this.errorAction.trim().equals("")) {
			if (!SAPErrorStrategyAction.isValid(this.errorAction)) {
				throw new BuildException("The error action attribute '"
						+ this.errorAction + "' is not correct.");
			}

			errActionValidated = true;
		}

		boolean errStrategyValidated = false;
		if (this.errorStrategy != null && !this.errorStrategy.trim().equals("")) {
			if (!SAPErrorStrategy.isValid(this.errorStrategy)) {
				throw new BuildException("The error strategy attribute '"
						+ this.errorStrategy + "' is not correct.");
			}

			errStrategyValidated = true;
		}

		if (errActionValidated != errStrategyValidated) {
			throw new BuildException(
					"The error action and strategy are incorrect. "
							+ "The one is correct but the other is not set. \n"
							+ "Is error strategy validated: "
							+ errStrategyValidated
							+ "\nIs error action validated: "
							+ errActionValidated);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "error action: '" + this.errorAction + "', error strategy: '"
				+ this.errorStrategy + "'";
	}

}
