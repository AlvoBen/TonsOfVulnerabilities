package com.sap.engine.services.dc.cm.server.spi;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class ServerState {

	/**
	 * Indicates that at least one server node and one dispatcher node of the
	 * Server is online.
	 */
	public static final ServerState ONLINE = new ServerState(new Integer(0),
			"Online");

	/**
	 * Indicates that each server and dispatcher nodes of the Server are
	 * offline.
	 */
	public static final ServerState OFFLINE = new ServerState(new Integer(1),
			"Offline");

	private final Integer id;
	private final String name;

	private ServerState(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
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

		if (!(obj instanceof ServerState)) {
			return false;
		}

		final ServerState other = (ServerState) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}