package com.sap.engine.services.dc.event;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-5-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class DCEvent implements Serializable {

	private static final long serialVersionUID = -3493109169005758873L;

	private transient final Object userObject;// redundant in this
	// implementation
	private transient final Object action;// redundant in this implementation
	private final String toString;

	DCEvent(Serializable userObject, Serializable action) {
		this.userObject = userObject;
		this.action = action;

		this.toString = this.getClass().getName()
				+ (this.userObject != null ? " [user object = "
						+ this.userObject + "]" : "")
				+ (this.action != null ? " [action = " + this.userObject + "]"
						: "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.toString;
	}

	public abstract void accept(DCEventVisitor visitor);

}
