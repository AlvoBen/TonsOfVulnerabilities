package com.sap.engine.services.dc.lcm;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class LCMResultStatus implements Serializable {

	private static final long serialVersionUID = 1796319606137601123L;

	public transient static final LCMResultStatus SUCCESS = new LCMResultStatus(
			new Integer(0), "success");

	public transient static final LCMResultStatus WARNING = new LCMResultStatus(
			new Integer(1), "warning");

	public transient static final LCMResultStatus NOT_SUPPORTED = new LCMResultStatus(
			new Integer(2), "not supported");

	public transient static final LCMResultStatus ERROR = new LCMResultStatus(
			new Integer(3), "error");

	private final Integer id;
	private final String name;

	private LCMResultStatus(Integer id, String name) {
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
		return this.name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof LCMResultStatus)) {
			return false;
		}

		final LCMResultStatus other = (LCMResultStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
