package com.sap.engine.services.dc.repo.explorer;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class SearchClauseTarget implements Serializable {

	private static final long serialVersionUID = -5197479703520472067L;

	public transient static final SearchClauseTarget SDU = new SearchClauseTarget(
			new Integer(0), "Sdu");

	public transient static final SearchClauseTarget SCA = new SearchClauseTarget(
			new Integer(1), "Sca");

	public transient static final SearchClauseTarget SDA = new SearchClauseTarget(
			new Integer(2), "Sda");

	private final Integer id;
	private final String name;

	private SearchClauseTarget(Integer id, String name) {
		checkArg(id, "id");
		checkArg(name, "name");

		this.id = id;
		this.name = name;
	}

	private void checkArg(Object arg, String argName) {
		if (arg == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3421] The argument '" + argName
							+ "' is null.");
		}
	}

	private Integer getId() {
		return id;
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

		if (!(obj instanceof SearchClauseTarget)) {
			return false;
		}

		SearchClauseTarget other = (SearchClauseTarget) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
