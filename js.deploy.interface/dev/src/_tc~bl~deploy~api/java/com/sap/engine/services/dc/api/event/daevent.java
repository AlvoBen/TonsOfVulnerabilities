/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.event;

/**
 * Abstract class for all Deploy controller API events.
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 */
public abstract class DAEvent {

	private final Object userObject;
	private final Object action;
	private final String toString;

	DAEvent(Object userObject, Object action) {
		this.userObject = userObject;
		this.action = action;

		this.toString = this.getClass().getName()
				+ (this.action != null ? " [action = " + this.action + "]" : "")
				+ (this.userObject != null ? " [user object = "
						+ this.userObject + "]" : "");
	}

	/**
	 * Returns the user object which iz used as a container event information.
	 * 
	 * @return user object
	 */
	public Object getUserObject() {
		return this.userObject;
	}

	/**
	 * Returns the event action
	 * 
	 * @return action
	 */
	public Object getAction() {
		return this.action;
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
