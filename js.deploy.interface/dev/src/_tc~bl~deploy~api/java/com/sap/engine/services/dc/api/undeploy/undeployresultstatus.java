package com.sap.engine.services.dc.api.undeploy;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Indicates the undeploy status after undeploy transaction.
 * UndeployResultStatus – shows
 * <UL>
 * <LI>ERROR - occurs if there is at least one archive which has not been
 * undeployed successfully.</LI>
 * <LI>WARNING - if there are no archives with status ERROR and all of them have
 * been undeployed successfully but there are warnings which occurred while
 * stopping or removing them from containers.</LI>
 * <LI>SUCCESS - if there are no archives with status ERROR or WARNING and all
 * of the archives have been successfully undeployed.</LI>
 * </UL>
 * </DD>
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
 */
public final class UndeployResultStatus {
	/**
	 * At least one item has not been undeployed successfully.
	 */
	public static final UndeployResultStatus ERROR = new UndeployResultStatus(
			new Integer(0), "Error");
	/**
	 * All items has been undeployed successfully.
	 */
	public static final UndeployResultStatus SUCCESS = new UndeployResultStatus(
			new Integer(1), "Success");
	/**
	 * Undeploy transaction passed with some warnings. It is better to check
	 * distinct <code>UndeployItemStatus</code> in order to determine which
	 * items are processed with problems.
	 */
	public static final UndeployResultStatus WARNING = new UndeployResultStatus(
			new Integer(2), "Warning");

	// public static final UndeployResultStatus UNKNOWN = new
	// UndeployResultStatus(new Integer(3), "Unknown");

	private final Integer id;
	private final String name;

	private UndeployResultStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this undeploy result status.
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
		if (!(obj instanceof UndeployResultStatus)) {
			return false;
		}
		final UndeployResultStatus other = (UndeployResultStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}