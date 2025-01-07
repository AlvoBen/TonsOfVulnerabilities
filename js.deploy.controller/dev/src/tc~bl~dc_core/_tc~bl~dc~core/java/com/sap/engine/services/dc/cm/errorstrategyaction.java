package com.sap.engine.services.dc.cm;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-19
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class ErrorStrategyAction implements Serializable {

	private static final long serialVersionUID = -6326337156089653974L;

	public transient static final ErrorStrategyAction PREREQUISITES_CHECK_ACTION = new ErrorStrategyAction(
			new Integer(0), "PrerequisitesCheckAction");

	public transient static final ErrorStrategyAction DEPLOYMENT_ACTION = new ErrorStrategyAction(
			new Integer(1), "DeploymentAction");

	public transient static final ErrorStrategyAction UNDEPLOYMENT_ACTION = new ErrorStrategyAction(
			new Integer(2), "UndeploymentAction");

	private final Integer id;
	private final String name;

	private ErrorStrategyAction(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
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

		if (!(obj instanceof ErrorStrategyAction)) {
			return false;
		}

		ErrorStrategyAction other = (ErrorStrategyAction) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
