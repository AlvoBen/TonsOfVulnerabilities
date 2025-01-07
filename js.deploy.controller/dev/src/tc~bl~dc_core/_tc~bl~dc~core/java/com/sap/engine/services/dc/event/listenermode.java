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
public final class ListenerMode implements Serializable {

	private static final long serialVersionUID = -3663642435583309877L;

	public transient static final ListenerMode LOCAL = new ListenerMode(
			new Integer(0), "local",
			"The listener is valid only for the current Deploy Controller instance.");

	public transient static final ListenerMode GLOBAL = new ListenerMode(
			new Integer(1),
			"global",
			"The listener is valid for all the Deploy Controller instances running on the cluster.");

	private final Integer id;
	private final String name;
	private final String description;

	private ListenerMode(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public String toString() {
		return name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ListenerMode)) {
			return false;
		}

		final ListenerMode other = (ListenerMode) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
