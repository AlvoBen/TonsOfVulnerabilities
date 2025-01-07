/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.event;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Indicates undeployement evet action.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-27</DD>
 * </DL>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * @see com.sap.engine.services.dc.api.event.UndeploymentEvent#getUndeploymentEventAction()
 */
public final class UndeploymentEventAction {

	/**
	 * Indicates that the item undeployment finished.
	 */
	public static final UndeploymentEventAction UNDEPLOYMENT_PERFORMED = new UndeploymentEventAction(
			new Integer(0), "undeployment performed",
			"The undeployment has been finished");
	/**
	 * Indicates that the undeployment of the item is at the begining.
	 */
	public static final UndeploymentEventAction UNDEPLOYMENT_TRIGGERED = new UndeploymentEventAction(
			new Integer(1), "undeployment triggered",
			"The undeployment has been triggered");

	private final Integer id;
	private final String name;
	private final String description;

	private UndeploymentEventAction(Integer id, String name, String description) {
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

		if (!(obj instanceof UndeploymentEventAction)) {
			return false;
		}

		final UndeploymentEventAction other = (UndeploymentEventAction) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}
