package com.sap.engine.services.dc.event;

import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class DeploymentEvent extends DCEvent {

	private static final long serialVersionUID = 7200810483254208182L;

	private final DeploymentBatchItem deploymentBatchItem;
	private final DeploymentEventAction deploymentEventAction;
	private final String toString;

	public DeploymentEvent(DeploymentBatchItem deploymentBatchItem,
			DeploymentEventAction deploymentEventAction) {
		super(deploymentBatchItem, deploymentEventAction);

		this.deploymentBatchItem = deploymentBatchItem;
		this.deploymentEventAction = deploymentEventAction;
		this.toString = genToString();
	}

	public DeploymentBatchItem getDeploymentBatchItem() {
		return this.deploymentBatchItem;
	}

	public DeploymentEventAction getDeploymentEventAction() {
		return this.deploymentEventAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.event.DCEvent#accept(com.sap.engine.services
	 * .dc.event.DCEventVisitor)
	 */
	public void accept(DCEventVisitor visitor) {
		visitor.visit(this);
	}

	public String toString() {
		return this.toString;
	}

	private String genToString() {
		return "Deployment Event, action: " + deploymentEventAction
				+ ", component:\n " + deploymentBatchItem;
	}

}
