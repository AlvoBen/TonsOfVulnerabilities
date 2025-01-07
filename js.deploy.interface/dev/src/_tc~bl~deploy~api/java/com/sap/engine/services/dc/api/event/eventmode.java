/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.event;

/**
 *<DL>
 * <DT><B>Title: </B>
 * <DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Specifies how to register the listener.
 * <UL>
 * <LI>synchronously - the server waits the invoked listeners method to finish</LI>
 * <LI>asynchronously - the server does not waits the invoked listeners method
 * to finish</LI>
 * </UL>
 * </DD>
 * 
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
 * 
 */
public final class EventMode {
	/**
	 * The Deploy Controller sends an event to a listener. The listener process
	 * the information. When the listener finishes the information processing,
	 * the Deploy Controller send the event to the next listener.
	 */
	public transient static final EventMode SYNCHRONOUS = new EventMode(
			new Integer(0), "synchronous",
			"The events will be spread away in a synchronous manner.");
	/**
	 * The Deploy Controller sends an event to a listener. Without waiting for
	 * the listener to finish with processing the event, the Deploy Controller
	 * sends the event to the next listener.
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

	/**
	 * Returns the id of this event mode.
	 * 
	 * @return event id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this event mode.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the description of this event mode.
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
		return this.id.hashCode();
	}

}
