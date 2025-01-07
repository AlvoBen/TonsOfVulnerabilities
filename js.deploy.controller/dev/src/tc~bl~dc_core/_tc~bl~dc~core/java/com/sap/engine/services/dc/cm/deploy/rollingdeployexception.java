package com.sap.engine.services.dc.cm.deploy;

/**
 * 
 * Title: J2EE Deployment Team Description: Copyright: Copyright (c) 2003
 * Company: SAP AG
 * 
 * @version 1.0
 * @since 7.0
 * @deprecated The exception will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 * 
 */
public class RollingDeployException extends DeploymentException {

	private static final long serialVersionUID = 6522000535826916613L;

	/**
	 * @param errMessage
	 */
	public RollingDeployException(String errMessage) {
		super(errMessage);
	}

	/**
	 * @param errMessage
	 * @param throwable
	 */
	public RollingDeployException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
