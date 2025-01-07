/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.event;

import com.sap.engine.services.dc.api.undeploy.UndeployItem;
import com.sap.engine.services.dc.api.event.UndeploymentEventAction;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Event object passed to UndeploymentListeners.</DD>
 *</DL>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 */
public class UndeploymentEvent extends DAEvent {

	/**
	 * Constructs an UndeploymentEvent object.
	 * 
	 * @param undeployItem
	 *            which is being deployed
	 * @param undeploymentEventAction
	 *            which happens
	 */
	public UndeploymentEvent(UndeployItem undeployItem,
			UndeploymentEventAction undeploymentEventAction) {
		super(undeployItem, undeploymentEventAction);
	}

	/**
	 * Returns the undeploy item.
	 * 
	 * @return undeployItem related to the event.
	 */
	public UndeployItem getUndeployItem() {
		return (UndeployItem) super.getUserObject();
	}

	/**
	 * Returns the undeployment event action.
	 * 
	 * @return action
	 */
	public UndeploymentEventAction getUndeploymentEventAction() {
		return (UndeploymentEventAction) super.getAction();
	}

}
