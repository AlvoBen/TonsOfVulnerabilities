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
public final class EventMode implements Serializable {

	private static final long serialVersionUID = -4137440449263297677L;

	public transient static final EventMode SYNCHRONOUS = new EventMode(
			new Integer(0), "synchronous",
			"The events will be spread away in a synchronous manner.");

	/**
	 * @deprecated - asynchronous notification is implemented client side only
	 */
	public transient static final EventMode ASYNCHRONOUS = new EventMode(
			new Integer(1), "asynchronous",
			"The events will be spread away in an asynchronous manner.");

	private final Integer id;
	private final String name;
	private final String description;

	private EventMode(Integer id, String name, String description) {
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

		if (!(obj instanceof EventMode)) {
			return false;
		}

		final EventMode other = (EventMode) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
