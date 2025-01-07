package com.sap.engine.services.dc.ant.deploy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-10
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */
final class SAPDeployResult {

	/**
	 * There was an error on deploy.
	 */
	static final SAPDeployResult ERROR = new SAPDeployResult(new Integer(0),
			"Error");

	/**
	 * Deploy transaction passed successfully.
	 */
	static final SAPDeployResult SUCCESS = new SAPDeployResult(new Integer(1),
			"Success");

	/**
	 * Deploy transaction passed with some warnings. It is better to check
	 * distinct <code>DeployItemStatus</code> in order to determine which items
	 * are processed with problems.
	 */
	static final SAPDeployResult WARNING = new SAPDeployResult(new Integer(2),
			"Warning");

	/**
	 * this neve should happen.
	 */
	static final SAPDeployResult UNKNOWN = new SAPDeployResult(new Integer(3),
			"Unknown");

	private final Integer id;
	private final String name;

	private SAPDeployResult(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	String getName() {
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

		if (!(obj instanceof SAPDeployResult)) {
			return false;
		}

		SAPDeployResult other = (SAPDeployResult) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}
