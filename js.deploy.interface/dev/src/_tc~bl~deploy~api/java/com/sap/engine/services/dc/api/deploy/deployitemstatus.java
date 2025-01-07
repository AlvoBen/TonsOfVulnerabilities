package com.sap.engine.services.dc.api.deploy;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Shows the status of the deploy item after deployment.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.deploy.DeployItem#getDeployItemStatus()
 */
public final class DeployItemStatus {
	/**
	 * The SDA has only been prepared for delivery to the J2EE Engine.
	 */
	public static final DeployItemStatus INITIAL = new DeployItemStatus(
			new Integer(0), "Initial");
	/**
	 * The SDA was not allowed to be deployed by at least one of the filters,
	 * set by the client.
	 */
	public static final DeployItemStatus SKIPPED = new DeployItemStatus(
			new Integer(1), "Skipped");
	/**
	 * The deployment of the SDA on the J2EE Engine finished successfully.
	 */
	public static final DeployItemStatus SUCCESS = new DeployItemStatus(
			new Integer(2), "Success");
	/**
	 * The deployment of the SDA on the J2EE Engine finished but with some
	 * warnings.
	 */
	public static final DeployItemStatus WARNING = new DeployItemStatus(
			new Integer(3), "Warning");
	/**
	 * The deployment of the SDA on the J2EE Engine has been aborted.
	 */
	public static final DeployItemStatus ABORTED = new DeployItemStatus(
			new Integer(4), "Aborted");
	/**
	 * The SDA has passed the validation check.
	 */
	public static final DeployItemStatus ADMITTED = new DeployItemStatus(
			new Integer(5), "Admitted");
	/**
	 * The SDA has not passed the validation check.
	 */
	public static final DeployItemStatus PREREQUISITE_VIOLATED = new DeployItemStatus(
			new Integer(6), "PrerequisiteViolated");
	/**
	 * There is already an SDA with the same version.
	 */
	public static final DeployItemStatus ALREADY_DEPLOYED = new DeployItemStatus(
			new Integer(7), "AlreadyDeployed");
	/**
	 * The SDA has been successfully delivered to the J2EE Engine, but was not
	 * registered into the Deploy controller repository.
	 */
	public static final DeployItemStatus DELIVERED = new DeployItemStatus(
			new Integer(8), "Delivered");
	/*
	 * public static final DeployItemStatus OFFLINE_ADMITTED = new
	 * DeployItemStatus(new Integer(9), "OfflineAdmitted");
	 */
	/*
	 * public static final DeployItemStatus OFFLINE_SUCCESS = new
	 * DeployItemStatus(new Integer(10), "OfflineSuccess");
	 */
	/*
	 * public static final DeployItemStatus OFFLINE_ABORTED = new
	 * DeployItemStatus(new Integer(11), "OfflineAborted");
	 */
	/*
	 * public static final DeployItemStatus OFFLINE_WARNING = new
	 * DeployItemStatus(new Integer(12), "OfflineWarning");
	 */
	/**
	 * The SDA has been rejected by the specified deployment filters.
	 */
	public static final DeployItemStatus FILTERED = new DeployItemStatus(
			new Integer(13), "Filtered");
	/**
	 * The SDA is repeated among the batch item.
	 */
	public static final DeployItemStatus REPEATED = new DeployItemStatus(
			new Integer(14), "Repeated");

	private final Integer id;
	private final String name;

	private DeployItemStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this deploy item status.
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

		if (!(obj instanceof DeployItemStatus)) {
			return false;
		}

		final DeployItemStatus other = (DeployItemStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}