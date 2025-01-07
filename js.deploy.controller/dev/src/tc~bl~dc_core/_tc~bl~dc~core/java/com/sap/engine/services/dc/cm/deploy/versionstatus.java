package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-11
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public final class VersionStatus implements Serializable {

	private static final long serialVersionUID = -9074569328512692278L;;

	public transient static final VersionStatus NOT_RESOLVED = new VersionStatus(
			new Integer(0), "NOT_RESOLVED");

	/**
	 * repository component has the same version
	 */
	public transient static final VersionStatus SAME = new VersionStatus(
			new Integer(1), "SAME");

	/**
	 * version of this component is lower than the one in the repository
	 */
	public transient static final VersionStatus LOWER = new VersionStatus(
			new Integer(2), "LOWER");

	/**
	 * version of this component is higher than the one in the repository
	 */
	public transient static final VersionStatus HIGHER = new VersionStatus(
			new Integer(3), "HIGHER");

	/**
	 * this component has no previous version in the repository
	 */
	public transient static final VersionStatus NEW = new VersionStatus(
			new Integer(4), "NEW");

	private transient static final Map VERSION_STATUS_MAP = new HashMap();

	private final Integer id;
	private final String name;

	static {
		VERSION_STATUS_MAP.put(NOT_RESOLVED.getName(), NOT_RESOLVED);
		VERSION_STATUS_MAP.put(SAME.getName(), SAME);
		VERSION_STATUS_MAP.put(LOWER.getName(), LOWER);
		VERSION_STATUS_MAP.put(HIGHER.getName(), HIGHER);
		VERSION_STATUS_MAP.put(NEW.getName(), NEW);
	}

	public static VersionStatus getVersionStatusByName(String name) {
		return (VersionStatus) VERSION_STATUS_MAP.get(name);
	}

	private VersionStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return name;
	}

	/*
	 * The operation is implemented in this way because it is not know at the
	 * moment whether the class would become Serializable. If it becomes this
	 * method has to be implemented in this way. If the class is not going to be
	 * Serializable it is prefered and faster to use the default operation
	 * implementation - super.equals(Object obj); (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof VersionStatus)) {
			return false;
		}

		VersionStatus other = (VersionStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
