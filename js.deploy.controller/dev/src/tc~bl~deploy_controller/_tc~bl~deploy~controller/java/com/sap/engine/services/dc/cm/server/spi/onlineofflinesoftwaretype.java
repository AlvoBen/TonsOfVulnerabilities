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
public final class OnlineOfflineSoftwareType {

	public static final OnlineOfflineSoftwareType ONLINE = new OnlineOfflineSoftwareType(
			new Integer(0), "Online");

	public static final OnlineOfflineSoftwareType OFFLINE = new OnlineOfflineSoftwareType(
			new Integer(1), "Offline");

	public static final OnlineOfflineSoftwareType POST_ONLINE = new OnlineOfflineSoftwareType(
			new Integer(2), "Post online");

	private final Integer id;
	private final String name;

	private OnlineOfflineSoftwareType(Integer id, String name) {
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

		if (!(obj instanceof OnlineOfflineSoftwareType)) {
			return false;
		}

		final OnlineOfflineSoftwareType other = (OnlineOfflineSoftwareType) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
