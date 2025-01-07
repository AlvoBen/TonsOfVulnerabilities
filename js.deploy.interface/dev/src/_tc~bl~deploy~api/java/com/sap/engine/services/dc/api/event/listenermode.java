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
 * <LI>global - receives events from all Un/DeployProcessors
 * <LI>local - recieves un/deployment events only from the current processor
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
public final class ListenerMode {
	/**
	 * Local listeners are used when the client (deploy or undeploy processor)
	 * is interested only by events from actions performed by itself.<BR>
	 * For example, if client A has registered a local deployment listener and
	 * client B has registered a listener (global or local), the client A will
	 * receive only notification for events triggered by its own actions and not
	 * for events triggered by actions of client B.
	 */
	public transient static final ListenerMode LOCAL = new ListenerMode(
			new Integer(0), "local",
			"The listener is valid only for the current Deploy Controller instance.");

	/**
	 * If a client (deploy or undeploy processor) registers a global listener
	 * for an event, then this client will receive notifications:
	 * <UL>
	 * <LI>when it performs an action that triggers this event</LI>
	 * <LI>every time another client anywhere in the cluster performs an action
	 * that triggers this event.</LI>
	 * </UL>
	 * For example, if client A and client B have registered global deployment
	 * listeners for a deployment event X and client C has registered no
	 * listeners at all, then client A will receive events when:
	 * <UL>
	 * <LI>Client A has performed a deployment action that triggers event X.</LI>
	 * <LI>Client B has performed a deployment action that triggers event X.</LI>
	 * <LI>Client C has performed a deployment action that triggers event X.</LI>
	 * </UL>
	 */
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

	/**
	 * Returns the id of this listener mode.
	 * 
	 * @return id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this listener mode.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the description of the listenet mode.
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
		return this.id.hashCode();
	}
}
