package com.sap.engine.services.dc.api.lcm;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Represent execution status after invoking one of the
 * <code>LifeCycleManager</code>s command</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-25</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class LCMResultStatus {

	/**
	 * Command has finished successfully.
	 */
	public static final LCMResultStatus SUCCESS = new LCMResultStatus(
			new Integer(0), "success");
	/**
	 * Command has completed successfully but with warning. In this case it is
	 * necessary to check description of the <code>LCMResult</code> object
	 * returned from the method.
	 */
	public static final LCMResultStatus WARNING = new LCMResultStatus(
			new Integer(1), "warning");

	/**
	 * Indicates that the executed command is not supported from the requested
	 * object.
	 */
	public static final LCMResultStatus NOT_SUPPORTED = new LCMResultStatus(
			new Integer(2), "not supported");

	/**
	 * Command has finished erroneously. the requested object.
	 */
	public static final LCMResultStatus ERROR = new LCMResultStatus(
			new Integer(3), "error");

	private final Integer id;
	private final String name;

	private LCMResultStatus(Integer id, String name) {
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

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof LCMResultStatus)) {
			return false;
		}

		final LCMResultStatus other = (LCMResultStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}