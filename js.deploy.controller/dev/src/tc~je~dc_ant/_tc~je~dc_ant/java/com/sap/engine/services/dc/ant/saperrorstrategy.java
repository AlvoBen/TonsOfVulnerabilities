package com.sap.engine.services.dc.ant;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-9
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */
public final class SAPErrorStrategy {

	public static final SAPErrorStrategy ON_ERROR_STOP = new SAPErrorStrategy(
			new Integer(0), "stop");

	public static final SAPErrorStrategy ON_ERROR_SKIP_DEPENDING = new SAPErrorStrategy(
			new Integer(1), "skip");

	private final Integer id;
	private final String name;

	private static final Map STATEGY_MAP = new HashMap();

	static {
		STATEGY_MAP.put(ON_ERROR_STOP.getName(), ON_ERROR_STOP);
		STATEGY_MAP.put(ON_ERROR_SKIP_DEPENDING.getName(),
				ON_ERROR_SKIP_DEPENDING);
	}

	public static SAPErrorStrategy getErrorStrategyByName(String name) {
		return (SAPErrorStrategy) STATEGY_MAP.get(name);
	}

	public static boolean isValid(String name) {
		return STATEGY_MAP.containsKey(name);
	}

	private SAPErrorStrategy(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * @return error strategy name
	 */
	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	/**
	 * compares two objects.The method returns true only if the argument is a
	 * ErrorStrategy object, is not null and the id of the argument and the
	 * current reference are equal.
	 * 
	 * @param obj
	 * @return
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof SAPErrorStrategy)) {
			return false;
		}

		final SAPErrorStrategy other = (SAPErrorStrategy) obj;

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
