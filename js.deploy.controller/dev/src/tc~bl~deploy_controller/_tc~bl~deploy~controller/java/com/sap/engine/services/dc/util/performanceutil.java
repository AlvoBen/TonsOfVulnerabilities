package com.sap.engine.services.dc.util;

public class PerformanceUtil {

	private static final boolean isBoostPerformanceDisabled;

	public static final String BOOST = "com.sap.engine.disable.monitoring";

	static {
		final String boostValue = System.getProperty(BOOST);
		isBoostPerformanceDisabled = boostValue == null
				|| !Boolean.parseBoolean(boostValue);
	}

	private PerformanceUtil() {
	}

	/*
	 * This method determines whether boost performance is disabled. This helps
	 * to exclude some consuming operations during runtime
	 * 
	 * @return <code>true</code> if system property
	 * "com.sap.boost.engine.performance" absent or is not set to "true",
	 * otherwise <code>false</code>
	 */
	public static boolean isBoostPerformanceDisabled() {
		return isBoostPerformanceDisabled;
	}

}
