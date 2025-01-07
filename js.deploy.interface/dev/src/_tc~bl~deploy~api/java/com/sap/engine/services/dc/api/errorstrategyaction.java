package com.sap.engine.services.dc.api;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * 
 * <DT><B>Description: </B></DT>
 * <DD>The error strategy action defines the different supported actions which
 * are related with the supported error handling mechanism. Each
 * <code>ErrorStrategyAction</code> is mapped to a <code>ErrorStrategy</code>.
 * By mapping these two classes it is possible to specify that a concrete
 * process has to stop in case of error or to continue.</DD>
 * 
 * <DT><B>Usage: </B></DT>
 * <DD>DeployProcessor.setErrorStrategy(
 * [ErrorStrategyAction.PREREQUISITES_CHECK_ACTION | DEPLOYMENT_ACTION
 * ],errorStrategy);</DD>
 * <DD>UndeployProcessor.setErrorStrategy(
 * [ErrorStrategyAction.PREREQUISITES_CHECK_ACTION | UNDEPLOYMENT_ACTION
 * ],errorStrategy );</DD>
 * 
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-7-20</DD>
 * </DL>
 * 
 * @see com.sap.engine.services.dc.api.ErrorStrategy
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public final class ErrorStrategyAction {
	/**
	 * Prerequisite error strategy action. Valid in both deployProcessor and
	 * undeployProcessor.
	 * 
	 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor
	 * @see com.sap.engine.services.dc.api.undeploy.UndeployProcessor
	 */
	public static final ErrorStrategyAction PREREQUISITES_CHECK_ACTION = new ErrorStrategyAction(
			new Integer(0), "PrerequisitesCheckAction");
	/**
	 * Deployment error strategy action. Make sense only for deployProcessor.
	 * 
	 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor
	 */
	public static final ErrorStrategyAction DEPLOYMENT_ACTION = new ErrorStrategyAction(
			new Integer(1), "DeploymentAction");
	/**
	 * Undeployment error strategy action. Make sense only for
	 * undeployProcessor.
	 * 
	 * @see com.sap.engine.services.dc.api.undeploy.UndeployProcessor
	 */
	public static final ErrorStrategyAction UNDEPLOYMENT_ACTION = new ErrorStrategyAction(
			new Integer(2), "UndeploymentAction");

	private final Integer id;
	private final String name;

	private ErrorStrategyAction(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this error strategy action.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ErrorStrategyAction)) {
			return false;
		}

		ErrorStrategyAction other = (ErrorStrategyAction) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}