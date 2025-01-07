package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-8
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public final class DeploymentStatus implements Serializable {

	private static final long serialVersionUID = -2217366273495084052L;

	public transient static final DeploymentStatus INITIAL = new DeploymentStatus(
			new Integer(0), "Initial");

	public transient static final DeploymentStatus SKIPPED = new DeploymentStatus(
			new Integer(1), "Skipped");

	public transient static final DeploymentStatus SUCCESS = new DeploymentStatus(
			new Integer(2), "Success");

	public transient static final DeploymentStatus WARNING = new DeploymentStatus(
			new Integer(3), "Warning");

	public transient static final DeploymentStatus ABORTED = new DeploymentStatus(
			new Integer(4), "Aborted");

	public transient static final DeploymentStatus ADMITTED = new DeploymentStatus(
			new Integer(5), "Admitted");

	public transient static final DeploymentStatus PREREQUISITE_VIOLATED = new DeploymentStatus(
			new Integer(6), "PrerequisiteViolated");

	public transient static final DeploymentStatus ALREADY_DEPLOYED = new DeploymentStatus(
			new Integer(7), "AlreadyDeployed");

	public transient static final DeploymentStatus DELIVERED = new DeploymentStatus(
			new Integer(8), "Delivered");

	public transient static final DeploymentStatus OFFLINE_ADMITTED = new DeploymentStatus(
			new Integer(9), "OfflineAdmitted");

	public transient static final DeploymentStatus OFFLINE_SUCCESS = new DeploymentStatus(
			new Integer(10), "OfflineSuccess");

	public transient static final DeploymentStatus OFFLINE_ABORTED = new DeploymentStatus(
			new Integer(11), "OfflineAborted");

	public transient static final DeploymentStatus OFFLINE_WARNING = new DeploymentStatus(
			new Integer(12), "OfflineWarning");

	public transient static final DeploymentStatus FILTERED = new DeploymentStatus(
			new Integer(13), "Filtered");

	public transient static final DeploymentStatus REPEATED = new DeploymentStatus(
			new Integer(14), "Repeated");

	private transient static final Map DEPLOYMENT_STATUS_MAP = new HashMap();

	private final Integer id;
	private final String name;

	static {
		DEPLOYMENT_STATUS_MAP.put(INITIAL.getName(), INITIAL);
		DEPLOYMENT_STATUS_MAP.put(SKIPPED.getName(), SKIPPED);
		DEPLOYMENT_STATUS_MAP.put(SUCCESS.getName(), SUCCESS);
		DEPLOYMENT_STATUS_MAP.put(WARNING.getName(), WARNING);
		DEPLOYMENT_STATUS_MAP.put(ABORTED.getName(), ABORTED);
		DEPLOYMENT_STATUS_MAP.put(ADMITTED.getName(), ADMITTED);
		DEPLOYMENT_STATUS_MAP.put(PREREQUISITE_VIOLATED.getName(),
				PREREQUISITE_VIOLATED);
		DEPLOYMENT_STATUS_MAP.put(ALREADY_DEPLOYED.getName(), ALREADY_DEPLOYED);
		DEPLOYMENT_STATUS_MAP.put(DELIVERED.getName(), DELIVERED);
		DEPLOYMENT_STATUS_MAP.put(OFFLINE_ADMITTED.getName(), OFFLINE_ADMITTED);
		DEPLOYMENT_STATUS_MAP.put(OFFLINE_SUCCESS.getName(), OFFLINE_SUCCESS);
		DEPLOYMENT_STATUS_MAP.put(OFFLINE_ABORTED.getName(), OFFLINE_ABORTED);
		DEPLOYMENT_STATUS_MAP.put(OFFLINE_WARNING.getName(), OFFLINE_WARNING);
		DEPLOYMENT_STATUS_MAP.put(FILTERED.getName(), FILTERED);
		DEPLOYMENT_STATUS_MAP.put(REPEATED.getName(), REPEATED);
	}

	public static DeploymentStatus getDeploymentStatusByName(String name) {
		return (DeploymentStatus) DEPLOYMENT_STATUS_MAP.get(name);
	}

	private DeploymentStatus(Integer id, String name) {
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

		if (!(obj instanceof DeploymentStatus)) {
			return false;
		}

		DeploymentStatus other = (DeploymentStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
