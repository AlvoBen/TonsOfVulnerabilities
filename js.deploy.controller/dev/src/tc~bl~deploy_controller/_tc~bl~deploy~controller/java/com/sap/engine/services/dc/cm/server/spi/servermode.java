package com.sap.engine.services.dc.cm.server.spi;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class ServerMode {

	public static final ServerMode SAFE = new ServerMode(new Integer(0),
			"safe", "DEPLOY");

	public static final ServerMode NORMAL = new ServerMode(new Integer(1),
			"normal", "NONE");

	public static final ServerMode UNKNOWN = new ServerMode(new Integer(2), "",
			"");

	private final Integer id;
	private final String name;
	private final String action;

	private final String toString;

	private ServerMode(Integer id, String name, String action) {
		this.id = id;
		this.name = name;
		this.action = action;

		this.toString = "'" + this.name + "' mode, action '" + this.action
				+ "'";
	}

	private Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getAction() {
		return this.action;
	}

	public String toString() {
		return this.toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ServerMode)) {
			return false;
		}

		final ServerMode other = (ServerMode) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
