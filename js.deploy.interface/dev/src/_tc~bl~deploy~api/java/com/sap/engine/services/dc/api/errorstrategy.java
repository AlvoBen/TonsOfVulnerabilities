package com.sap.engine.services.dc.api;

import java.util.HashMap;
import java.util.Map;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * 
 * <DT><B>Description: </B></DT>
 * <DD>Depending on the error strategy it is decided whether to stop the process
 * or to continue on the next step. The class defines constants which represent
 * the different error strategies. The error strategies concerns the main
 * processes like deployment and undeployment.</DD>
 * 
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-4-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.ErrorStrategyAction
 */
public final class ErrorStrategy {
	/**
	 * Stop the operation when the first error occurs.
	 */
	public static final ErrorStrategy ON_ERROR_STOP = new ErrorStrategy(
			new Integer(0), "OnErrorStop");
	/**
	 * Try to un/deploy as many as possible items.
	 */
	public static final ErrorStrategy ON_ERROR_SKIP_DEPENDING = new ErrorStrategy(
			new Integer(1), "OnErrorSkipDepending");

	// This strategy is quite error prone
	// private static final ErrorStrategy ON_ERROR_IGNORE =
	// new ErrorStrategy(new Integer(2), "OnErrorIgnore");

	private final Integer id;
	private final String name;

	private static final Map STRATEGY_MAP = new HashMap();

	static {
		STRATEGY_MAP.put(ON_ERROR_STOP.getName(), ON_ERROR_STOP);
		STRATEGY_MAP.put(ON_ERROR_SKIP_DEPENDING.getName(),
				ON_ERROR_SKIP_DEPENDING);
	}

	private ErrorStrategy(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this error strategy
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	/**
	 * Return an error strategy by a given name.
	 * 
	 * @param name
	 *            of error strategy
	 * @return error strategy
	 */
	public static ErrorStrategy getErrorStrategyByName(String name) {
		return (ErrorStrategy) STRATEGY_MAP.get(name);
	}

	/**
	 * Compares two objects. The method returns true only if the argument is a
	 * ErrorStrategy object, is not null and the id of the argument and the
	 * current reference are equal.
	 * 
	 * @param obj
	 *            - the reference object with which to compare.
	 * @return true if this object is the same as the obj argument; false
	 *         otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ErrorStrategy)) {
			return false;
		}

		ErrorStrategy other = (ErrorStrategy) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @return hash code of ID of the error strategy
	 */
	public int hashCode() {
		return this.id.hashCode();
	}

}