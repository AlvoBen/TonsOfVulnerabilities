package com.sap.sdm.is.security;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public abstract class SecurityFactory {
	private static SecurityFactory instance = null;

	public static void setInstance(SecurityFactory instance) {
		SecurityFactory.instance = instance;

		return;
	}

	public static SecurityFactory getInstance() {
		return instance;
	}

	public abstract String2SHA createHashedString(String text);

}
