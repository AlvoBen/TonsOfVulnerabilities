package com.tssap.dtr.client.lib.protocol.requests.dasl;

/**
 * Enumeration used in DASL SEARCH requests to specify whether
 * string values and patterns should be treated case-sensitive.
 */
public class CaseSensitivity {
	private final String name;
	private CaseSensitivity(String name) { 
		this.name = name; 
	}
	public String toString() { 
		return name; 
	}

	public static final CaseSensitivity CASE_SENSITIVE = new CaseSensitivity("1");
	public static final CaseSensitivity CASE_INSENSITIVE = new CaseSensitivity("0");

}
