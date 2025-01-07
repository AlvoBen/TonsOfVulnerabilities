package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-14
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class ValidationStatus implements Serializable {

	private static final long serialVersionUID = 5372657888448593798L;

	public transient static final ValidationStatus ERROR = new ValidationStatus(
			new Integer(0), "Error");

	public transient static final ValidationStatus SUCCESS = new ValidationStatus(
			new Integer(1), "Success");

	private final Integer id;
	private final String name;

	private ValidationStatus(Integer id, String name) {
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

		if (!(obj instanceof ValidationStatus)) {
			return false;
		}

		ValidationStatus other = (ValidationStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
