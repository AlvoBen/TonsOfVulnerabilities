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
package com.sap.engine.services.dc.api.undeploy;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Keeps the status of the deploy item after deployment.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-8</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class UndeployItemStatus {
	/**
	 * Indicates that the undeployItem is not processed yet.
	 */
	public static final UndeployItemStatus INITIAL = new UndeployItemStatus(
			new Integer(0), "Initial");
	/**
	 * Indicates that undeployItem is skipped regarding some reason.
	 */
	public static final UndeployItemStatus SKIPPED = new UndeployItemStatus(
			new Integer(1), "Skipped");
	/**
	 * Undeploy item was undeployed successfully.
	 */
	public static final UndeployItemStatus SUCCESS = new UndeployItemStatus(
			new Integer(2), "Success");
	/**
	 * Undeploy item was undeployed with some warnings.
	 */
	public static final UndeployItemStatus WARNING = new UndeployItemStatus(
			new Integer(3), "Warning");

	/**
	 * Undeploy item was not undeployed due to failure.
	 */
	public static final UndeployItemStatus ABORTED = new UndeployItemStatus(
			new Integer(4), "Aborted");
	/**
	 * UndeployItem was not deployed in order to be undeployed.
	 */
	public static final UndeployItemStatus NOT_DEPLOYED = new UndeployItemStatus(
			new Integer(5), "NotDeployed");

	/**
	 * Undeploy item was addmitted for undeploy but not undeployed yet.
	 */
	public static final UndeployItemStatus ADMITTED = new UndeployItemStatus(
			new Integer(6), "Admitted");

	/**
	 * Undeploy of an item with such software type is not supported.
	 */
	public static final UndeployItemStatus NOT_SUPPORTED = new UndeployItemStatus(
			new Integer(7), "NotSupported");
	/*
	 * public static final UndeployItemResult OFFLINE_ADMITTED = new
	 * UndeployItemResult(new Integer(8), "OfflineAdmitted");
	 */
	/*
	 * public static final UndeployItemResult OFFLINE_SUCCESS = new
	 * UndeployItemResult(new Integer(9), "OfflineSuccess");
	 */
	/*
	 * public static final UndeployItemResult OFFLINE_ABORTED = new
	 * UndeployItemResult(new Integer(10), "OfflineAborted");
	 */
	/**
	 * undeployItem contradicts with some prerequisites and conditions
	 */
	public static final UndeployItemStatus PREREQUISITE_VIOLATED = new UndeployItemStatus(
			new Integer(11), "PrerequisiteViolated");
	/*
	 * public static final UndeployItemResult OFFLINE_WARNING = new
	 * UndeployItemResult(new Integer(12), "OfflineWarning");
	 */

	private final Integer id;
	private final String name;

	private UndeployItemStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this undeploy item status.
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
		return this.id.hashCode();
	}

}