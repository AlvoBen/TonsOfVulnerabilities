/*
 * Created on May 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.services.dc.api.event;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Indicates deployement evet action.</DD>
 *</DL>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * @see com.sap.engine.services.dc.api.event.DeploymentEvent#getDeploymentEventAction()
 */
public class DeploymentEventAction {
	/**
	 * Indicates that the deployment of the item is at the begining.
	 */
	public static final DeploymentEventAction DEPLOYMENT_TRIGGERED = new DeploymentEventAction(
			new Integer(0), "deployment triggered",
			"The deployment has been triggered");

	/**
	 * Indicates that the item deployment finished.
	 */
	public static final DeploymentEventAction DEPLOYMENT_PERFORMED = new DeploymentEventAction(
			new Integer(1), "deployment performed",
			"The deployment has been performed");

	private final Integer id;
	private final String name;
	private final String description;

	private DeploymentEventAction(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	/**
	 * Returns the action id.
	 * 
	 * @return action id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Gets this action's name.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the action's description.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return this.description;
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

		if (!(obj instanceof DeploymentEventAction)) {
			return false;
		}

		final DeploymentEventAction other = (DeploymentEventAction) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}
