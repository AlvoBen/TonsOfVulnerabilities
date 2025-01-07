package com.sap.engine.services.dc.api.lcm;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Represent the actual component state at the time when the
 * <code>LifeCycleManager</code> command has executed.</DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-24</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class LCMStatus {
	/**
	 * The components is started( running )
	 */
	public static final LCMStatus STARTED = new LCMStatus(new Integer(0),
			"started");
	/**
	 * The component is stopped( not running )
	 */
	public static final LCMStatus STOPPED = new LCMStatus(new Integer(1),
			"stopped");
	/**
	 * The component does not support statuses and could not be retreieved.
	 * Probably not implemented yet.
	 */
	public static final LCMStatus NOT_SUPPORTED = new LCMStatus(new Integer(2),
			"not supported");
	/**
	 * Component status could not be retrieved. Probably some error occurred
	 * during getting the status.
	 */
	public static final LCMStatus UNKNOWN = new LCMStatus(new Integer(3),
			"unknown");
	/**
	 * The component is currenly starting.
	 */
	public static final LCMStatus STARTING = new LCMStatus(new Integer(4),
			"starting");
	/**
	 * The componen is currently stopping.
	 */
	public static final LCMStatus STOPPING = new LCMStatus(new Integer(5),
			"stopping");
	/**
	 * Very rare case where the engine is in safe migrate mode and the
	 * application is updating by some migration controller.
	 */
	public static final LCMStatus UPGRADING = new LCMStatus(new Integer(6),
			"upgrading");
	/**
	 * The component is implicitly stopped.
	 */
	public static final LCMStatus IMPLICIT_STOPPED = new LCMStatus(new Integer(
			7), "implicit stopped");

	private final Integer id;
	private final String name;
	private LCMStatusDetails lcmStatusDetails = null;

	private LCMStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public LCMStatusDetails getLCMStatusDetails() {
		return this.lcmStatusDetails;
	}

	public void setLCMStatusDetails(LCMStatusDetails lcmStatusDetails) {
		this.lcmStatusDetails = lcmStatusDetails;
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

		if (!(obj instanceof LCMStatus)) {
			return false;
		}

		final LCMStatus other = (LCMStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}