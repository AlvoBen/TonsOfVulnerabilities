package com.sap.engine.services.dc.cm.undeploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class UndeployItemStatus implements Serializable {

	private static final long serialVersionUID = -2418476268093740057L;

	public transient static final UndeployItemStatus INITIAL = new UndeployItemStatus(
			new Integer(0), "Initial");

	public transient static final UndeployItemStatus PREREQUISITE_VIOLATED = new UndeployItemStatus(
			new Integer(1), "PrerequisiteViolated");

	public transient static final UndeployItemStatus SKIPPED = new UndeployItemStatus(
			new Integer(2), "Skipped");

	public transient static final UndeployItemStatus SUCCESS = new UndeployItemStatus(
			new Integer(3), "Success");

	public transient static final UndeployItemStatus WARNING = new UndeployItemStatus(
			new Integer(4), "Warning");

	public transient static final UndeployItemStatus ABORTED = new UndeployItemStatus(
			new Integer(5), "Aborted");

	public transient static final UndeployItemStatus NOT_DEPLOYED = new UndeployItemStatus(
			new Integer(6), "NotDeployed");

	public transient static final UndeployItemStatus ADMITTED = new UndeployItemStatus(
			new Integer(7), "Admitted");

	public transient static final UndeployItemStatus NOT_SUPPORTED = new UndeployItemStatus(
			new Integer(8), "NotSupported");

	public transient static final UndeployItemStatus OFFLINE_ADMITTED = new UndeployItemStatus(
			new Integer(9), "OfflineAdmitted");

	public transient static final UndeployItemStatus OFFLINE_SUCCESS = new UndeployItemStatus(
			new Integer(10), "OfflineSuccess");

	public transient static final UndeployItemStatus OFFLINE_ABORTED = new UndeployItemStatus(
			new Integer(11), "OfflineAborted");

	public transient static final UndeployItemStatus OFFLINE_WARNING = new UndeployItemStatus(
			new Integer(12), "OfflineWarning");

	private transient static final Map STATUS_MAP = new HashMap();

	static {
		STATUS_MAP.put(INITIAL.getName(), INITIAL);
		STATUS_MAP.put(PREREQUISITE_VIOLATED.getName(), PREREQUISITE_VIOLATED);
		STATUS_MAP.put(SKIPPED.getName(), SKIPPED);
		STATUS_MAP.put(SUCCESS.getName(), SUCCESS);
		STATUS_MAP.put(WARNING.getName(), WARNING);
		STATUS_MAP.put(ABORTED.getName(), ABORTED);
		STATUS_MAP.put(NOT_DEPLOYED.getName(), NOT_DEPLOYED);
		STATUS_MAP.put(ADMITTED.getName(), ADMITTED);
		STATUS_MAP.put(NOT_SUPPORTED.getName(), NOT_SUPPORTED);
		STATUS_MAP.put(OFFLINE_ADMITTED.getName(), OFFLINE_ADMITTED);
		STATUS_MAP.put(OFFLINE_SUCCESS.getName(), OFFLINE_SUCCESS);
		STATUS_MAP.put(OFFLINE_ABORTED.getName(), OFFLINE_ABORTED);
		STATUS_MAP.put(OFFLINE_WARNING.getName(), OFFLINE_WARNING);
	}

	public static UndeployItemStatus getUndeployItemStatusByName(String name) {
		return (UndeployItemStatus) STATUS_MAP.get(name);
	}

	// TODO: calculate
	// private static final long serialVersionUID = ...

	private final Integer id;
	private final String name;

	private UndeployItemStatus(Integer id, String name) {
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

		if (!(obj instanceof UndeployItemStatus)) {
			return false;
		}

		UndeployItemStatus other = (UndeployItemStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
