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
 * <DD>Shows deployItems version status.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-1</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.deploy.DeployItem#getVersionStatus()
 */
public class DeployItemVersionStatus {
	/**
	 * No version information is available.
	 */
	public static final DeployItemVersionStatus NOT_RESOLVED = new DeployItemVersionStatus(
			new Integer(0), "NOT_RESOLVED");

	/**
	 * The component on the J2EE Engine and the new one have one and the same
	 * versions.
	 */
	public static final DeployItemVersionStatus SAME = new DeployItemVersionStatus(
			new Integer(1), "SAME");

	/**
	 * The new component has a lower version (it is older) than the already
	 * installed one.
	 */
	public static final DeployItemVersionStatus LOWER = new DeployItemVersionStatus(
			new Integer(2), "LOWER");

	/**
	 *The new component has a higher version (it is newer) than the already
	 * installed one.
	 */
	public static final DeployItemVersionStatus HIGHER = new DeployItemVersionStatus(
			new Integer(3), "HIGHER");

	/**
	 * There is no previous instance of this component deployed on the J2EE
	 * Engine.
	 */
	public static final DeployItemVersionStatus NEW = new DeployItemVersionStatus(
			new Integer(4), "NEW");

	private final Integer id;
	private final String name;

	private DeployItemVersionStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this deploy item version status.
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

		if (!(obj instanceof DeployItemVersionStatus)) {
			return false;
		}

		DeployItemVersionStatus other = (DeployItemVersionStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}