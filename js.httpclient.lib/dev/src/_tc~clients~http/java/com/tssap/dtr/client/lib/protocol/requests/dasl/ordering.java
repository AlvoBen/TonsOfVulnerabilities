package com.tssap.dtr.client.lib.protocol.requests.dasl;

/**
 * Enumeration used to specify the ordering of DASL SEARCH results.
 */
public class Ordering {
	private final String name;
	private Ordering(String name) { 
		this.name = name; 
	}
	public String toString() { 
		return name; 
	}

	public static final Ordering ASCENDING = new Ordering("ascending");
	public static final Ordering DESCENDING = new Ordering("descending");

}
