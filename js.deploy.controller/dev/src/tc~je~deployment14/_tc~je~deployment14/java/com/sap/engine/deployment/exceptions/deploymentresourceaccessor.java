package com.sap.engine.deployment.exceptions;

import com.sap.localization.ResourceAccessor;

/**
 * @author Mariela Todorova
 * 
 */
public class DeploymentResourceAccessor extends ResourceAccessor {
	static final long serialVersionUID = 6551879881371492688L;

	private static final String BUNDLE_NAME = "com.sap.engine.deployment.exceptions.DeploymentResourceBundle";

	private static final DeploymentResourceAccessor instance = new DeploymentResourceAccessor();

	private DeploymentResourceAccessor() {
		super(BUNDLE_NAME);
	}

	public static DeploymentResourceAccessor getInstance() {
		return instance;
	}

}