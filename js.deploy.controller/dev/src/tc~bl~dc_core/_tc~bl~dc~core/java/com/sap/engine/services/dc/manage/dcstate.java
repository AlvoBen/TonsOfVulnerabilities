package com.sap.engine.services.dc.manage;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description: Holdes the DC status.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-27
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class DCState implements Serializable {

	private static final long serialVersionUID = 4711678294338695018L;
	/**
	 * DC service is fully operational
	 */
	public static final DCState WORKING = new DCState(new Integer(0), "Working");
	/**
	 * DC is being restarted
	 */
	public static final DCState RESTARTING = new DCState(new Integer(1),
			"Restarting");
	/**
	 * DC is starting
	 * 
	 * @deprecated
	 */
	public static final DCState STARTING = new DCState(new Integer(2),
			"Starting");

	/**
	 * DC is not initialized yet
	 */
	public static final DCState NOT_INITIALIZED = new DCState(new Integer(3),
			"not Initialized");

	/**
	 * DC is initializing
	 */
	public static final DCState INITIALIZING = new DCState(new Integer(4),
			"Initializing");

	/**
	 * DC is Initialized
	 */
	public static final DCState INITIALIZED = new DCState(new Integer(5),
			"Initialized");

	/**
	 * DC is deploying
	 */
	public static final DCState DEPLOYING = new DCState(new Integer(6),
			"Deploying");

	/**
	 * DC is undeploying
	 */
	public static final DCState UNDEPLOYING = new DCState(new Integer(7),
			"Undeploying");

	/**
	 * DC has received event that is started(nothing to deploy/undeploy ) but is
	 * still in his initialization.
	 */
	public static final DCState INITIALIZING_TO_WORKING = new DCState(
			new Integer(8), "Initializing to working");

	private final Integer id;
	private final String name;

	private DCState(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
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

		if (!(obj instanceof DCState)) {
			return false;
		}

		DCState other = (DCState) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}