/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.event;

import com.sap.engine.services.dc.api.deploy.DeployItem;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Event object passed to DeploymentListeners.</DD>
 *</DL>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * @see com.sap.engine.services.dc.api.event.DeploymentListener#deploymentTriggered(DeploymentEvent)
 * @see com.sap.engine.services.dc.api.event.DeploymentListener#deploymentPerformed(DeploymentEvent)
 */
public class DeploymentEvent extends DAEvent {

	/**
	 * Constructs a DeploymentEvent object.
	 * 
	 * @param deployItem
	 *            a DeployItem which is being deployed
	 * @param deploymentEventAction
	 *            a DeploymentEventAction which happens
	 */
	public DeploymentEvent(DeployItem deployItem,
			DeploymentEventAction deploymentEventAction) {
		super(deployItem, deploymentEventAction);
	}

	/**
	 * Returns the deploy item.
	 * 
	 * @return deployItem related to the event.
	 */
	public DeployItem getDeployItem() {
		return (DeployItem) super.getUserObject();
	}

	/**
	 * Returns the deployment event action.
	 * 
	 * @return action
	 */
	public DeploymentEventAction getDeploymentEventAction() {
		return (DeploymentEventAction) super.getAction();
	}
}
