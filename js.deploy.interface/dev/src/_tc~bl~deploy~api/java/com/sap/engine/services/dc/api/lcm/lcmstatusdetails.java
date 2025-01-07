/*
 * Created on 2006-9-5 by radoslav-i
 */
package com.sap.engine.services.dc.api.lcm;

/**
 * @author radoslav-i
 */
public final class LCMStatusDetails {

	public transient static final LCMStatusDetails NO_STOPPED_FLAG = new LCMStatusDetails(
			new Integer(-1), "");
	public transient static final LCMStatusDetails STOPPED_ON_ERROR = new LCMStatusDetails(
			new Integer(0), "STOPPED ON ERROR");
	public transient static final LCMStatusDetails STOPPED_OK = new LCMStatusDetails(
			new Integer(1), "STOPPED OK");
	public transient static final LCMStatusDetails IMPLICIT_STOPPED_ON_ERROR = new LCMStatusDetails(
			new Integer(2), "IMPLICIT STOPPED ON ERROR");
	public transient static final LCMStatusDetails IMPLICIT_STOPPED_OK = new LCMStatusDetails(
			new Integer(3), "IMPLICIT STOPPED OK");

	private final Integer id;
	private final String name;
	private String description;

	private LCMStatusDetails(Integer id, String name) {
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof LCMStatusDetails)) {
			return false;
		}

		final LCMStatusDetails other = (LCMStatusDetails) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
