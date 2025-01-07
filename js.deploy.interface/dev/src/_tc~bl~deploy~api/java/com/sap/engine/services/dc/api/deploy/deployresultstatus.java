/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.deploy;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Indicates the deploy status after deploy transaction.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-5</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.deploy.DeployResult#getDeployResultStatus()
 */
public class DeployResultStatus {
	/**
	 * There was an error on deploy.
	 */
	public static final DeployResultStatus ERROR = new DeployResultStatus(
			new Integer(0), "Error");
	/**
	 * Deploy transaction passed successfully.
	 */
	public static final DeployResultStatus SUCCESS = new DeployResultStatus(
			new Integer(1), "Success");
	/**
	 * Deploy transaction passed with some warnings. It is better to check
	 * distinct <code>DeployItemStatus</code> in order to determine which items
	 * are processed with problems.
	 */
	public static final DeployResultStatus WARNING = new DeployResultStatus(
			new Integer(2), "Warning");
	/**
	 * this neve should happen.
	 */
	public static final DeployResultStatus UNKNOWN = new DeployResultStatus(
			new Integer(3), "Unknown");

	private final Integer id;
	private final String name;

	private DeployResultStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this deploy result status.
	 * 
	 * @return name
	 */
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
		if (!(obj instanceof DeployResultStatus)) {
			return false;
		}
		DeployResultStatus other = (DeployResultStatus) obj;
		if (!this.getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}