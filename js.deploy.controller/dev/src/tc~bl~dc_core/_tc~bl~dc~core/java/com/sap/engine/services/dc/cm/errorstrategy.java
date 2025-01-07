package com.sap.engine.services.dc.cm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-9
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public final class ErrorStrategy implements Serializable {

	private static final long serialVersionUID = -1528202827971359488L;

	public transient static final ErrorStrategy ON_ERROR_STOP = new ErrorStrategy(
			new Integer(0), "OnErrorStop");

	public transient static final ErrorStrategy ON_ERROR_SKIP_DEPENDING = new ErrorStrategy(
			new Integer(1), "OnErrorSkipDepending");

	// This strategy is quite error prone
	private transient static final ErrorStrategy ON_ERROR_IGNORE = new ErrorStrategy(
			new Integer(2), "OnErrorIgnore");

	private transient static final Map STRATEGY_MAP = new HashMap();

	private final Integer id;
	private final String name;

	static {
		STRATEGY_MAP.put(ON_ERROR_STOP.getName(), ON_ERROR_STOP);
		STRATEGY_MAP.put(ON_ERROR_SKIP_DEPENDING.getName(),
				ON_ERROR_SKIP_DEPENDING);
	}

	public static Map getNameAndErrorStrategy() {
		return STRATEGY_MAP;
	}

	public static ErrorStrategy getErrorStrategyByName(String name) {
		return (ErrorStrategy) STRATEGY_MAP.get(name);
	}

	private ErrorStrategy(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return name;
	}

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

	public int hashCode() {
		return id.hashCode();
	}

}
